Vous venez de livrer une application. Vous avez quelques soucis de performance mais fort heureusement les développeurs ont pensé à mettre des logs pour tracer les temps de réponse des appels de services.

Ce hands-on nécessite leiningen et un éditeur.

Documentation Incanter

- [Incanter](http://incanter.org/)
- [Getting Started](https://github.com/liebke/incanter/wiki)
- [Documentation](http://data-sorcery.org/contents/)
- [Accès direct à l'API](http://liebke.github.io/incanter/)

# 1 Prise en main

Dans un premier temps vous avez transformé le fichier de texte en un fichier .csv avec un autre outil. 

Ces fichiers apparaissent sous le nom TIME_MONITOR_<DATE>.csv dans le répertoire log.

Vous allez lire ce fichier et faire quelques analyses sur vos données avec Incanter en REPL.

## 1.1 Chargement du fichier
<br>

Commencez par créer un projet leiningen

<pre><code>$ leiningen new nom-projet</code></pre>

Editez le project.clj pour ajouter la librairie Incanter [incanter "1.5.4"] dans les :dependancies

<pre><code>  :dependencies [[org.clojure/clojure "1.5.1"][incanter "1.5.4"]])
</code></pre>

Mettez à jour les dépendances de leiningen

<pre><code>$ lein deps</code></pre>

Copiez des fichiers CSV dans le répertoire du projet pour qu'il soit accessible plus facilement

<pre><code>$ cp logs/TIME_MONITOR_2013-12-18.csv hoincanter/</code></pre>

Ouvrez un REPL

<pre><code>$ lein repl</code></pre>

Vous aurez besoin d'importer les modules core, io, stats, et charts de la librairie incanter

<pre><code>user=> (use '(incanter core io stats charts))</code></pre>

Chargez le fichier CSV

<pre><code>user=> (def ds (read-dataset  "TIME_MONITOR_2013-12-18.csv" :header true :delim \;) )</code></pre>

## 1.2 Quelques statistiques
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

## 1.3 Filtrer et catégoriser
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

## 1.4 Les charts
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



# 2 - Ecrire un programme

Pour faciliter l'usage, nous allons écrire une fonction qui parse le fichier d'origine qui n'est pas un csv et construit le dataset.

Nous allons aussi écrire une fonction qui fabrique une date correcte pour que l'on puisse visualiser la série temporelle.

Nous utiliserons ensuite ces deux fonctions dans le REPL, mais pour le moment, sortons du REPL en tapant exit.

## 2.1 Midje

Nous allons utiliser Midje au lieu de Clojure.test.

[Documentation](https://github.com/marick/Midje/wiki)

Ouvrez le fichier profiles.clj qui se trouve dans le répertoire .lein dans votre home et s'il est vide ajoutez la ligne suivante. Sinon insérez le plugin lein-midje au bon endroit.
 
<pre><code>
{:user {:plugins [[lein-midje "3.0.0"]]}}
</code></pre>

Vous avez maintenant une tâche midje disponible dans lein.

Ouvrer le fichier project.clj et ajoutez la dépendance envers midje. Elle n'est pas utile au run-time mais seulement pour le développement. N'oubliez pas de mettre à jour les dépendances.

<pre><code>  :profiles {:dev {:dependencies [[midje "1.5.1"]]}}
</code></pre>
  
Le template de projet contient un fichier core.clj dans src et core_test.clj dans test.

Ouvrez le fichier test et ajoutez un test Midje très simple puis lancez le test

<pre><code>(ns hoincanter.core-test
(ns hoincanter.core-test
  (:use  [midje.sweet])
  (:require [clojure.test :refer :all]
            [hoincanter.core :refer :all]
	   ))

(fact "dummy test"
      (+ 1 1) => 3)
</code></pre>
        
<pre><code> $ lein midje 
</code></pre>


Les deux tests échouent. Tout va bien !

Relancez un REPL et lancez les tests en suivant les instructions ci-dessous 

<pre><code> user=> (use 'midje.repl)
user=> (autotest)
</code></pre>

Corrigez les deux tests et sauvez. Regardez ce qui se passe dans le REPL.

## 2.2 Fonction q

Pour commencer nous alons écrire le test de la fonction q qui raccourci l'écriture du quantile. Rappelez vous que le quantile 0 est le le minimum de la liste et pensez à importer le module stats.

<pre><code>(defn q [p serie] (quantile serie :probs [p])) 

(fact "quantile 0 equals min of the list"
      (round (first (q 0 [3 2 1]))) => 1 )
</code></pre>

Vous devez importer incanter.stats et clojure.contrib.generic.math-functions qui se trouve dans [org.clojure/clojure-contrib "1.2.0"].

Vérifiez que vous pouvez utiliser la fonction q dans le REPL. Vous devez importer le module hoincanter.core.

## 2.3 Parser


TODO step2.handout 

La fonction re-seq découpe une ligne en fonction du pattern. Appliquez cette fonction à la chaîne "12 ab" pour obtenir les deux chaînes "12" et "ab"

<pre><code> user=> (re-seq #"(\d*) (.*)" "12 ab") 
(["12 ab" "12" "ab"])
</code></pre>

Le pattern d'extraction des données pour notre fichier vous est fourni dans la variable time-pattern.

Nous aurons besoin de convertir la chaîne qui contient un nombre. Une fonction str-tolong vous est fournie. Essayez cette fonction sur la chaîne "12"

Dans un premier temps vous allez devoir écrire la fonction build-reading qui prend la sortie du regexp avec le pattern time-pattern et renvoie un vector contenant la date, le nom du service et la durée en long. Le test est fourni.

<pre><code>(defn build-reading
  "re-seq returns a vector consisting of the line and each group. This function filters out the line and converts duration into a long"
  [[s t l d]]
  [t l (str-to-long d)] )
</code></pre>

Vous allez ensuite écrire la fonction parse-line. Cette fonction doit d'abord appliquer re-seq sur la ligne, puis transformer le résultat avec la fonction build-reading. Vous faciliter le test la fonction de transformation est passée en paramèttre. Le test est fourni. Vous aurez besoin de clojure.java.io.
 
<pre><code>(defn parse-file 
  "read the file and returns a list of readings"
  [filename parser]
  {:pre  [(and  (function? parser) (string? filename))]} 
  (with-open [rdr (io/reader filename)]
     (doall (map parser (line-seq rdr)))))
</code></pre>

Pour appeler la parse-file nous avons besoin d'une fonction "parser" qui prend la ligne du fichier en paramètre. 

Nous allons construire cette fonction en définissant une fonction partielle ou curry à partir de parse-line. Nous avons 2 paramètre à curryer. partial prend une fonction à n arguments et un paramètre ()et retourne une fonction à n-1 arguments.

<pre><core>(defn extract-data 
  "get readings from the file"
  [filename]
  {:pre  [(string? filename)]} 
  (let [parser (partial (partial parse-line time-pattern)  build-reading) ]
    (parse-file filename parser))) 
</core></pre>

Il nous reste à construire un dataset avec les bons noms de colonnes à partir de la sequence obtenue à la lecture du fichier.

<pre><code>
(defn readings-to-dataset
  "build a dataset from a list of readings"
  [readings]
  {:pre [(seq? readings)] } 
  (dataset [:timestamp :servicename :duration] readings))
</code></pre>

On peut maintenant convertir en dataset les données lues dans le fichier.

<pre><code>(defn to-dataset
  "get a dataset from the file"
  [filename]
  {:pre  [(string? filename)] } 
  (readings-to-dataset (extract-data filename)))
</code></pre>

Vous pouvez maintenant ouvrir un REPL et lire un des fichiers .log fournis. Vous aurez besoin d'importer le module hoincanter.core.

<pre><code>user=> (use 'hoincanter.core)
user=> (use '(incanter io core charts stats))
user=> (view (convert-to-dataset "resources/sample.log"))
user=> (view (histogram :duration :data (convert-to-dataset "resources/sample.log")))
</code></pre>

Et la time-series-plot ? 

Et bien toujours pas car la date n'est pas un timestamp numérique

## 2.3 Calculer les timestamps

Nous allons utilier clj-time qui est un wrapper sur Joda Time

[clj-time](https://github.com/clj-time/clj-time)

Ajoutez [clj-time "0.6.0"] dans le projet.clj et dans le REPL importez clj-time.core, clj-time.format et clj-time.coerce. date-time vous permet de créer une date et to-long la convertie en long.

<pre><code>user=> (use '(clj-time core coerce))
user=> (def d (date-time 2013 12 19 8 0 1 004))
#'user/d
user=> d
#<DateTime 2013-02-01T14:05:00.000Z>
user=> (to-long d)
1359727500000
</code></pre>

Le format de notre fichier est presque ISO 8601.

<pre><code>user=> (to-long "2013-12-19 08:00:01,004")
nil
user=> (to-long "2013-12-19T08:00:01,004")
1387440001004
</code></pre>


<pre><code>user=> (def custom-formatter (formatter "yyyy-MM-dd HH:mm:ss,SSS"))
#'user/custom-formatter
user=> (parse custom-formatter "2013-12-19 08:00:01,004")
#<DateTime 2013-12-19T08:00:01.004Z>
</code></pre>

Modifiez les fonction mapper build-reading et readings-to-dataset pour ajouter une colonne ts qui contient le timestamp. Ajoutez également les tests correspondants.

Une fois la fonction disponible vous pouvez visualiser vos données avec le timestamps et enfin le time-series-plot. Assurez vous que vous avez bien importé incanter.charts.

<pre><code>user=> (view (convert-to-dataset "resources/sample.log"))
user=> (view (time-series-plot :ts :duration  :data (convert-to-dataset "resources/sample.log")))
#
</code></pre>

C'est un peu plus intéressant avec plus de données.

<pre><code>user=> (view (convert-to-dataset "../logs/TIME_MONITOR_2013-12-20.log"))
</code></pre>


# 3 Des charts améliorés

Un ensemble de fonctions de chart avec un paramétrage des titres, des couleurs vous sont fournies dans elements/perf-charts.txt. Placez ces fonctions dans un module charts dans le projet hoincanter. Adaptez les fonctions si besoin et traduisez les titres en français.

Ensuite depuis le REPL, essayez chacune des fonctions sur les relevés dont le temps est supérieur à 40 ms.

user=> (def ds (convert-to-dataset "../logs/TIME_MONITOR_2013-12-20.log"))
user=> (def dslong  ($where {:duration {:gt 40}} ds))



