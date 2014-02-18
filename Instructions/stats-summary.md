Un summary amélioré
===============

La fonction summary d'IncanteR affiche un résumé assez basique. Nous allons construire un summary plus adapté à nos besoins.

Le but est de constuire un tableau qui affiche les principales statistiques (nombre d'occurences, moyenne, écart-type, min …) pour chaque service. Ce tableau est construit sous la forme d'un dataset.

Le structure de données est la suivante. Le dataset est une map qui comporte deux entrées, un vecteur contenant les noms de colonnes et une liste contenant les lignes. Chaque ligne est un vecteur de valeurs.

Vous placerez ces fonctions dans un module summary. Vous allez commencer par déplacer la fonction q et son test dans ce module.

Dataset pour une métrique
-------------------
<br>

$rollup permet d'utiliser une fonction de son choix. Par exemple

<pre><code>user=> ($rollup #(count %) :duration :servicename dslong)</code></pre>

qui utilise la fonction anonyme #(count %) est fonctionnellement équivalente à  

<pre><code>user=> ($rollup count :duration :servicename dslong)</code></pre>

Mais cette fonction n'accepte que les fonctions qui retournent une seule valeur.

Nous allons donc calculer un dataset avec $rollup pour chaque métrique puis nous les fusionnerons pour constituer le dataset summary. 

Implémentez la fonction compute-metric pour qu'elle retourne ce dataset.

Pour faciliter la fusion, le dataset d'une métrique aura comme nom de colonne le nom de la métrique. 

La fonction coll-names vous permettra de remplacer la liste des noms de colonnes.

Faites d'abord un test sur la fonction count.

<pre><code>(defn compute-metric
  "compute a metric grouped by label"
  [ds metric-name f]
  {:pre [(and (dataset? ds) (keyword? metric-name) (function? f))] } 
  (col-names 
      ($rollup f :duration :servicename ds)    
      [:servicename metric-name])) </code></pre>



Application à l'ensemble des métriques
-------------------

Le tableau associatid metric-functions liste les métriques que vous voulons calculer. 
<pre><code>
(def metric-functions {:count count :mean mean :sd sd})
</pre></code>

Nous allons mapper la fonction de calcul d'une métrique sur le tableau de métriques. 

<pre><code>
(map key  metric-functions)
</code></pre>

Un essaie sur avec une fonction simple montre que le résultat sera une liste de datasets.

La fonction map va passer un vecteur pour chaque entrée du tableau associatif. Vous devez donc adapter la fonction compute-metric.

La solution la plus simple est d'utiliser une fonction curry et de destructurer les paramètres de compute-metric. Pensez à modificer la fonction et le test.

<pre><code>(defn compute-metric
  "compute a metric grouped by label"
  [ds [metric-name f]]
  {:pre [(and (dataset? ds) (keyword? metric-name) (function? f))] } 
  (col-names
   ($rollup f :duration :servicename ds)
   [:servicename metric-name])) 

(defn compute-metrics
  "compute metrics grouped by label"
  [metric-functions ds]
  {:pre [(dataset? ds)] } 
  (map (partial compute-metric ds) metric-functions))
</code></pre>

Une fois curriée la fonction compute-metric n'attend plus qu'un paramètre qui est passé automatiquement.
La fonction compute-metric attend maintenant un vecteur contenant la clé et la valeurs qui sont extraits directement en raison de la déstructuration.

Fusion des colonnes 
-------------------

Allez dans un REPL et étudiez la structure des données.

<pre><code>
user=> (use '(hoincanter core summary)
user=> (use '(incanter core io stats))
user=> (def ds (convert-to-dataset "resources/sample.log"))
user=> (def metrics-list  (compute-metrics {:count count, :mean mean} ds))
</code></pre>

La fonction sel vous permet de lire la colonne 1 (celle de la métrique)

<pre><code>user=> (sel (nth metrics-list 0) :cols 1)
</code></pre>

Si on mappe cette fonction sur la liste des des datasets des métriques, on obtient une liste des colonnes metriques.

<pre><code>user=> (def cols (map #(sel % :cols 1) metrics-list))
</code></pre>

Vous pouvez obtenir la liste des noms de services dans le premier dataset

<pre><code>user=> (def services ($ :servicename (nth metrics-list 0)))
</code></pre>

L'assemblage de la liste des noms de services et de la liste des colonnes vous donnera un dataset dont les colonnes sont le nom de service suivi de chacune des métriques

La fonction conj-cols d'Incanter permet de de fusionner les colonnes. Mais l'application à une liste et une liste de liste ne donne pas le résultat attendu. 

Nous allons donc utiliser reduce pour ajouter les colonnes une à une. L'accumulation est initialisée par la liste des noms de services.


<pre><code>user=> (reduce conj-cols services cols)
</code></pre>

Il ne reste plus qu'à ajouter les noms de colonnes du dataset.

<pre><code>user=> (col-names (reduce conj-cols services cols) '(:servicename :count :mean))
</code></pre>

La liste des labels de colonne dépend de la liste des métriques. Vous pouvez utiliser keys pour extraire la liste des clés de la map metrics et concat pour les rassembler en une seule liste.
  
<pre><code>user=> (concat [:servicename] (keys metric-functions))
</code></pre>

Vous avez maintenant tous les éléments pour écrire la fonction compute-metrics-table.

----
TODO ajout des quantiles 

Les valeurs décimales sont formatées avec 2 chiffres après la virgule. La fonction de formatage est passée en paramètre.


# 
