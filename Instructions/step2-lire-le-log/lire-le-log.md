Lire le log
==============

Si vous avez sauté les steps précédents vous pouvez trouver le projet initial de ce step dans Instructions/step2-lire-le-log/initial.

Le log n'est pas en csv à l'origine. Nous allons écrire quelques fonctions qui lisent et parsent le log dans son format d'origine. Ensuite nous contruirons le dataset. 

Un dataset s'utilise comme une sorte de table, mais il est en fait implémenté comme une map contenant d'une part les noms de colonnes, d'autre part une liste de vecteurs contenant chacun une ligne de données. La fonction dataset permet de construire un dataset à partir de ces deux types de données.

1 - Parser une ligne 
-----------------------
<br>
Nous allons d'abord nous occuper de parser une ligne du fichier. Le pattern d'extraction des données pour notre fichier vous est fourni dans la variable time-pattern.

La fonction re-seq découpe une ligne en fonction du pattern. 
Pour vous familiariser avec les regex, appliquez cette fonction à la chaîne "12 ab" pour obtenir les deux chaînes "12" et "ab"

<pre><code> user=> (re-seq #"(\d*) (.*)" "12 ab") 
(["12 ab" "12" "ab"])
</code></pre>

Il y a trop de colonnes et les nombres ne sont pas du bon type. Vous allez devoir écrire la fonction build-reading qui prend la sortie du regexp avec le pattern time-pattern et renvoie un vecteur contenant la date, le nom du service et la durée en long. Le test est fourni.

Nous aurons besoin de convertir la chaîne qui contient la durée en nombre. Une fonction str-to-long vous est fournie. Essayez cette fonction sur la chaîne "12"

<br>
**-Solution-**
<pre><code>(defn build-reading
  "re-seq returns a vector consisting of the line and each group. This function filters out the line and converts duration into a long"
  [[s t l d]]
  [t l (str-to-long d)] )
</code></pre>

Vous allez ensuite écrire la fonction parse-line. 

Cette fonction doit d'abord extraire les groupes avec re-seq. Ensuite nous alons contruire le vecteur dont nous avons besoin avec la fonction build-reading. 

Pour faciliter le test la fonction de transformation (ici build-reading) est passée en paramèttre. Le test est fourni. 

Vous aurez besoin d'importer clojure.java.io.

<br>
**-Solution-**
<pre><code>(defn parse-line
  "returns the file line as a vector representing the reading"
  [pattern mapper source]
  {:pre  [(and  (function? mapper) (string? source) (= java.util.regex.Pattern (type pattern)))]} 
  (first (map mapper (re-seq pattern source))))
</code></pre>

 
2 - Parser le fichier
-----------------------
<br>

La fonction parse-file est fournie. Elle parcours le fichier et transmet chaque ligne au parseur.

<pre><code>(defn parse-file 
  "read the file and returns a list of readings"
  [filename parser]
  {:pre  [(and  (function? parser) (string? filename))]} 
  (with-open [rdr (io/reader filename)]
     (doall (map parser (line-seq rdr)))))
</code></pre>

Pour appeler la parse-file nous avons besoin d'une fonction "parser" qui n'a qu'un paramètre la ligne du fichier à traiter. 

Nous allons construire cette fonction en appliquant la technique du currying (ou application partielle) à la fonction parse-line. 

Le currying consiste à construire une fonction à n-1 arguments à partir d'une fonction à n arguments. Sous une forme plus technique, l'arité est diminuée de 1. Le premier argument de la fonction est fixé.
 
En Clojure le currying se fait avec la fonction partial. 

Nous avons curryer les deux premiers paramètres de parse-line pour obtenir une fonction a un seul paramètre.

La fonction qui définie la fonction parser et lance la lecture du fichier s'appelle extract-data et le test est fourni.

<br>
**-Solution-**

<pre><core>(defn extract-data 
  "get readings from the file"
  [filename]
  {:pre  [(string? filename)]} 
  (let [parser (partial (partial parse-line time-pattern)  build-reading) ]
    (parse-file filename parser))) 
</core></pre>

3 - Construire le dataset
-----------------------
<br>
Il nous reste à construire un dataset avec les bons noms de colonnes à partir de la sequence obtenue à la lecture du fichier.

Nous allons construire une fonction qui construit le dataset à partir d'une liste de noms de colonnes et d'une séquence de vecteur représentant le timestamp, le nom du service et la durée.

<br>
**-Solution-**

<pre><code>
(defn readings-to-dataset
  "build a dataset from a list of readings"
  [readings]
  {:pre [(seq? readings)] } 
  (dataset [:timestamp :servicename :duration] readings))
</code></pre>

On peut maintenant combiner la fonction d'extraction des données extract-data  et la fonction de création de dataset readings-to-dataset pour charger les données dans un dataset.

<br>
**-Solution-**

<pre><code>(defn load-dataset
  "get a dataset from the file"
  [filename]
  {:pre  [(string? filename)] } 
  (readings-to-dataset (extract-data filename)))
</code></pre>

Vous pouvez maintenant ouvrir un REPL et lire un des fichiers .log fournis. Vous aurez besoin d'importer le module hoincanter.core.

<pre><code>user=> (use 'hoincanter.core)
user=> (use '(incanter io core charts stats))
user=> (view (load-dataset "resources/sample.log"))
user=> (view (histogram :duration :data (convert-to-dataset "resources/sample.log")))
</code></pre>

Et la time-series-plot ? 

Et bien toujours pas car la date en chaîne n'est pas un timestamp valable.



