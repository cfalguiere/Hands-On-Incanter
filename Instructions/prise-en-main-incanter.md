# Prise en main d'Incanter

Dans un premier temps vous avez transformé le fichier de texte en un fichier .csv avec un autre outil. 

Ces fichiers apparaissent sous le nom TIME_MONITOR_<DATE>.csv dans le répertoire log.

Vous allez lire ce fichier et faire quelques analyses sur vos données avec Incanter en REPL.

## 1 Installation d'IncanteR
<br>

Commencez par créer un projet leiningen

<pre><code>$ leiningen new nom-projet</code></pre>

Editez le project.clj pour ajouter la librairie Incanter [incanter "1.5.4"] dans les :dependancies

<pre><code>  :dependencies [[org.clojure/clojure "1.5.1"][incanter "1.5.4"]])
</code></pre>

Mettez à jour les dépendances de leiningen

<pre><code>$ lein deps</code></pre>


## 2 Chargement du fichier
<br>

Copiez des fichiers CSV dans le répertoire du projet pour qu'il soit accessible plus facilement

<pre><code>$ cp logs/TIME_MONITOR_2013-12-18.csv hoincanter/</code></pre>


Ouvrez un REPL

<pre><code>$ lein repl</code></pre>

Vous aurez besoin d'importer les modules core, io, stats, et charts de la librairie incanter

<pre><code>user=> (use '(incanter core io stats charts))</code></pre>

Chargez le fichier CSV

<pre><code>user=> (def ds (read-dataset  "TIME_MONITOR_2013-12-18.csv" :header true :delim \;) )</code></pre>

## 3 Quelques statistiques
<br>

Consultez les valeurs et relevez les noms de colonnes, le nombre de ligne et le summary du dataset

<pre><code>user=> (view ds)
user=> (nrow ds)
user=> (summary ds)
</code></pre>

Sélectionnez la colonne des temps de réponse (duration) et calculez sa moyenne, son écart type, son maximum et le quantile 95%

<pre><code>user=> (view ($ :duration ds))
user=> (mean ($ :duration ds))
user=> (sd ($ :duration ds))
user=> (quantile ($ :duration ds) :probs[1]) 
user=> (quantile ($ :duration ds) :probs[0.95]) 
</code></pre>

Pour faciliter l'écriture des quantiles, nous allons écrire une fonction utilitaire q qui prend deux paramètres, le seuil de probabilité et la série. Vérifiez ensuite que la fonction est correcte en calculant le maximum.

<pre><code> user=> (defn q [p serie] (quantile serie :probs [p]))
user=> (q 1 ($ :duration ds))
</code></pre>

Lorsque vous avez plusieurs agrégats à calculer, pouvez alléger l'écriture en utilisant with-data. Construisez un vecteur contenant le nombre, la moyenne, l'écart-type, le minimum, le maximum, et le quantile 95% de la colonne duration.

<pre><code> user=> (with-data ($ :duration ds)
  #_=> [ (count $data) (mean $data) (sd $data) (q 0 $data) (q 1 $data) (q 0.95 $data) ] )
</code></pre>  

## 4 Filtrer et catégoriser
<br>

Les données qui nous préoccupent sont les temps de réponse supérieurs à 40 ms. Nous allons extraire un dataset contenant seulement ces relevés sous le nom dslong.
 
Vous pouvez utiliser $where. Le langage de requêtes est le même que celui de MongoDB [Langage de requêtes]
(http://docs.mongodb.org/manual/tutorial/query-documents/)

<pre><code> user=> (def dslong  ($where {:duration {:gt 40}} ds))
</code></pre>  

Jetons un coup d'oeil à la répartiion. 

Summary vous permet de voir les différentes valeurs de services. Nous pouvons filtrer sur un service et calculer la moyenne des temps par exemple

<pre><code> user=> (mean  ($ :duration  ($where {:servicename "RS_OW_AgencyDataSupplierService"} dslong)))
1556.1666666666667
</code></pre> 

Il y a un moyen plus facile de les obtenir par $group-by. Cette fonction créer une map de dataset. Une entrée est crée pour chaque valeur de la clé et elle contient les valeurs filtrées sur cette valeur.

Essayez $group-by sur dslong. C'est une map donc vous pouvez utiliser keys pour avoir seulement la liste des valeurs de la catégorie et 

<pre><code> ($group-by :servicename dslong)
</code></pre>  

Vous pouvez obtenir le dataset correspondant à une valeur de servicename et l'utiliser pour calculer des statistiques

<pre><code> user=> (def dslongADS (get  ($group-by :servicename dslong) {:servicename "RS_OW_AgencyDataSupplierService"} ))
</code></pre>  

<pre><code> user=> (mean  ($ :duration  dslongADS ))
</code></pre> 

Pour finir, nous allons afficher la moyenne  des temps de réponse pour chaque service. La fonction $rollup fait un $group-by et applique une fonction sur chaque groupe. Certains agrégats (mean, count …) ont un racourci syntaxique.
 
<pre><code> user=> ($rollup mean :duration :servicename ds20)
</code></pre>

## 5 Les charts
<br>

Pour le moment, nous ne pouvons pas afficher les données sous une forme chronologique car la date n'a pas un format connu.

Nous pouvons cependant afficher l'histogramme des temps de réponse.

<pre><code> user=> (histogram  :duration :data ds20)
</code></pre>  

Le diagramme est bien crée mais il faut utiliser view pour l'afficher.

<pre><code> user=> (view (histogram  :duration :data ds20))
</code></pre>  

Les couleurs et le rendu peuvent être modifiés. Nous ferons celà dans une fonction plus tard.

Nous pouvons aussi afficher les relevés par catégorie sous forme de diagramme en barre ou de camembert.

<pre><code> user=> (view (bar-chart :servicename :duration :vertical false :data  ($rollup count :duration :servicename dslong)))
user=> (view (pie-chart :servicename  :duration  :vertical false :data  ($rollup count :duration :servicename dslong)))
</code></pre>  

Pour faciliter l'usage, nous allons écrire une fonction qui parse le fichier d'origine qui n'est pas un csv et construit le dataset.

Nous allons aussi écrire une fonction qui fabrique une date correcte pour que l'on puisse visualiser la série temporelle.

Nous utiliserons ensuite ces deux fonctions dans le REPL, mais pour le moment, sortons du REPL en tapant exit.




