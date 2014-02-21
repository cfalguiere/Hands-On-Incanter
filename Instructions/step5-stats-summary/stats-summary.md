Un summary amélioré
===============

Si vous avez sauté les steps précédents vous pouvez trouver le projet initial de ce step dans Instructions/step5-stats-summary/initial.

La fonction summary d'IncanteR affiche un résumé assez basique. Nous allons construire un summary plus adapté à nos besoins.

Le but est de constuire un tableau qui affiche les principales statistiques (nombre d'occurences, moyenne, écart-type, min …) pour chaque service. Ce tableau est construit sous la forme d'un dataset.


1 - Le module Summary
-------------------
<br>

Vous placerez ces fonctions dans un module summary. Vous allez commencer par déplacer la fonction q et son test dans ce module.

2 - Dataset pour une métrique
-------------------
<br>

Premier problème regrouper les données par nom de service et faire les calculs.

$rollup permet d'utiliser une fonction de son choix. Par exemple

<pre><code>user=> ($rollup #(count %) :duration :servicename dslong)</code></pre>

qui utilise la fonction anonyme #(count %) est fonctionnellement équivalente à  

<pre><code>user=> ($rollup count :duration :servicename dslong)</code></pre>

Mais cette fonction n'accepte que les fonctions qui retournent une valeur scalaire.

Nous allons donc calculer un dataset avec $rollup pour chaque métrique puis nous les fusionnerons pour constituer le dataset summary. 

Implémentez la fonction compute-metric pour qu'elle retourne un dataset contenant les noms de service dans la colonne 1 et leur compte dans la colonne 2.

Pour faciliter la fusion et le debug, le dataset d'une métrique aura comme nom de colonne le nom de la métrique. 

La fonction coll-names vous permettra de remplacer la liste des noms de colonnes du dataset.

Faites d'abord un test sur la fonction count.

<br>
**-Solution-**

<pre><code>(defn compute-metric
  "compute a metric grouped by label"
  [ds metric-name f]
  {:pre [(and (dataset? ds) (keyword? metric-name) (function? f))] } 
  (col-names 
      ($rollup f :duration :servicename ds)    
      [:servicename metric-name])) </code></pre>



3 - Application à l'ensemble des métriques
-------------------
<br>
Le tableau associatif metric-functions liste les métriques que vous voulons calculer. 

<pre><code>(def metric-functions {:count count :mean mean :sd sd})
</pre></code>

Nous allons mapper la fonction de calcul d'une métrique sur le tableau de métriques. 

<pre><code>(map key  metric-functions)
</code></pre>

La fonction map sur une map va passer un couple (clé, valeur) pour chaque entrée du tableau associatif. key retourne la clé.

Un essai sur cette fonction simple montre que le résultat est une liste de chaînes. Avec compute-metric le résultat sera une liste de datasets.

Vous devez adapter la fonction compute-metric pour qu'elle accepte un couple clé valeur au lieu de 2 paramètres.

La solution la plus simple est de destructurer les paramètres de compute-metric. 

Déstructuring correspond à démonter. Nous allons utiliser une forme très simple de desctructuring. 

Nous allons indiquer dans la liste des paramètres que nous voulons recevoir la structure "démontée" en décrivant le vecteur. Les arguments seront accessibles directement.

Vous pourrez trouver plus d'informations là
[Clojure: Destructuring](http://blog.jayfields.com/2010/07/clojure-destructuring.html)

La fonction compute-metric attend maintenant un vecteur contenant la clé et la valeurs qui sont extraits directement en raison de la déstructuration.

Pensez à modifier la fonction et le test.

<br>
**-Solution-**

<pre><code>(defn compute-metric
  "compute a metric grouped by label"
  [ds [metric-name metric-function]]
  {:pre [(and (dataset? ds) (keyword? metric-name) (function? metric-function))] } 
  (col-names
   ($rollup metric-function :duration :servicename ds)
   [:servicename metric-name])) 

d'utiliser une fonction curry et 
</code></pre>

Il nous reste un problème. La fonction compute-metric utilise deux paramètres. Le premier est le dataset qui sera le même pour toutes les métriques.

Comment pouvons nous faire ?

<br>
**-Solution-**

Nous avons déjà utilisé le currying. 

Une fois curriée la fonction compute-metric n'attend plus qu'un paramètre qui est passé automatiquement.

(defn compute-metrics
  "compute metrics grouped by label"
  [metric-functions ds]
  {:pre  [(and (dataset? ds) (map? metric-functions))]  } 
  (map (partial compute-metric ds) metric-functions))
</code></pre>




4 - Fusion des colonnes 
-------------------
<br>

Maintenant que nous avons une liste de datasets, nous allons les fusionner.


Allez dans un REPL et étudiez la structure des données.

<pre><code>user=> (use '(hoincanter core summary)
user=> (use '(incanter core io stats))
user=> (def ds (load-dataset "resources/sample.log"))
user=> (def metrics-list  (compute-metrics {:count count, :mean mean} ds))
</code></pre>

La fonction sel vous permet de lire la colonne 1 du dataset qui contient la métrique quelque soit son nom.

<pre><code>user=> (sel (nth metrics-list 0) :cols 1)
</code></pre>

Si on construit une fonction anonyme à partir de de sel et on mappe cette fonction sur la liste des datasets des métriques, on obtient une liste des colonnes metriques. 

<pre><code>user=> (def cols (map #(sel % :cols 1) metrics-list))
</code></pre>

Cela va nous servir pour reconstruire un dataset.

Il nous faut aussi des noms de colonnes. Vous pouvez obtenir la liste des noms de services dans le premier dataset

<pre><code>user=> (def services ($ :servicename (nth metrics-list 0)))
</code></pre>

L'assemblage de la liste des noms de services et de la liste des colonnes vous donnera un dataset dont les colonnes sont le nom de service suivi de chacune des métriques

La fonction conj-cols d'Incanter permet de fusionner les colonnes. Mais l'application à une liste et une liste de liste ne donne pas le résultat attendu.

Est ce que vous voyez pourquoi ? 

<br>
**-Solution-**
conj-col essaye de joidre une colonne qui contient des noms de service avec ce qu'il prend pour un dataset. Il construit un dataset à partir de la liste en considérant que c'est une liste de lignes

Nous allons donc utiliser reduce pour ajouter les colonnes une à une. L'accumulation est initialisée par la liste des noms de services. Ensuite conj-cols est appliqué sur un dataset et une colonne.

<br>
**-Solution-**

<pre><code>user=> (reduce conj-cols services cols)
</code></pre>

Il ne reste plus qu'à ajouter les noms de colonnes du dataset.

<br>
**-Solution-**

<pre><code>user=> (col-names (reduce conj-cols services cols) '(:servicename :count :mean))
</code></pre>

La liste des labels de colonne dépend de la liste des métriques. Vous pouvez utiliser keys pour extraire la liste des clés de la map metric-functions et concat pour les rassembler en une seule liste avec le nom de la colonne :servicename.
  
<br>
**-Solution-**
<pre><code>user=> (concat [:servicename] (keys metric-functions))
</code></pre>

Vous avez maintenant tous les éléments pour écrire la fonction metrics-to-dataset.

<br>
**-Solution-**

<pre><code>(defn metrics-to-dataset
  "compute metrics grouped by label"
  [metric-functions metrics]
  {:pre [(and  (seq? metrics)  (map? metric-functions))] } 
  (let [cols (map #(sel % :cols 1) metrics)
        service-names ($ :servicename (nth metrics 0))
	metric-names (concat [:servicename] (keys metric-functions))]
    (col-names (reduce conj-cols service-names cols) metric-names)))
</code></pre>


5 - Min, max et quantiles
-------------------
<br>

La liste de métriques fournies se limite à des fonctions simples à appeler. 

Nous voulons aussi les métriques min, max, quantiles 90% et 95%. 

Vous devez ajouter ces fonctions  dans la map  metric-functions. Mais comment faire pour passer les paramètres ?

Dupliquez le test de compute-metric et testez avec la fonction min. Ce test défini sa propre liste de métriques.

Ensuite adaptez la map metric-functions.

<br>
**-Solution-**

<pre><code>user=>(def metric-functions {
  :count count 
  :mean mean :sd sd
  :min (partial q 0) 
  :q90 (partial q 0.90)
  :q95 (partial q 0.95) 
  :max (partial q 1)})
</code></pre>

<pre><code>(fact "it should return a dataset "
      (let [ds (load-dataset "resources/sample.log")
	    metric-ds (compute-metric ds [:min (partial q 0)]) ]
	(print  metric-ds)
	(round ($ :min ($where {:servicename "WS_1R_DetailAbonne"} metric-ds))) => 261))
</code></pre>



 
