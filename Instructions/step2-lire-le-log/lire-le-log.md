Lire le log
==============

Si vous avez sauté les steps précédents vous pouvez trouver le projet initial de ce step dans Instructions/step2-lire-le-log/initial.

Le log n'est pas en csv à l'origine. Nous allons écrire quelques fonctions qui lisent et parsent le log dans son format d'origine.

1 - Parser une ligne 
-----------------------
<br>

La fonction re-seq découpe une ligne en fonction du pattern. Appliquez cette fonction à la chaîne "12 ab" pour obtenir les deux chaînes "12" et "ab"

<pre><code> user=> (re-seq #"(\d*) (.*)" "12 ab") 
(["12 ab" "12" "ab"])
</code></pre>

Le pattern d'extraction des données pour notre fichier vous est fourni dans la variable time-pattern.

Nous aurons besoin de convertir la chaîne qui contient un nombre. Une fonction str-to-long vous est fournie. Essayez cette fonction sur la chaîne "12"

Dans un premier temps vous allez devoir écrire la fonction build-reading qui prend la sortie du regexp avec le pattern time-pattern et renvoie un vector contenant la date, le nom du service et la durée en long. Le test est fourni.

<br>
**-Solution-**
<pre><code>(defn build-reading
  "re-seq returns a vector consisting of the line and each group. This function filters out the line and converts duration into a long"
  [[s t l d]]
  [t l (str-to-long d)] )
</code></pre>

Vous allez ensuite écrire la fonction parse-line. 

Cette fonction doit d'abord appliquer re-seq sur la ligne, puis transformer le résultat avec la fonction build-reading. Pour faciliter le test la fonction de transformation est passée en paramèttre. Le test est fourni. Vous aurez besoin d'importer clojure.java.io.

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

Pour appeler la parse-file nous avons besoin d'une fonction "parser" qui prend la ligne du fichier en paramètre. 

Nous allons construire cette fonction en définissant une fonction partielle ou curry à partir de parse-line. Nous avons 2 paramètre à curryer. partial prend une fonction à n arguments et un paramètre et retourne une fonction à n-1 arguments.

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

Vous pouvez utiliser la fonction dataset et lui passer une liste de noms de colonnes et les données extraites du fichier.

<br>
**-Solution-**

<pre><code>
(defn readings-to-dataset
  "build a dataset from a list of readings"
  [readings]
  {:pre [(seq? readings)] } 
  (dataset [:timestamp :servicename :duration] readings))
</code></pre>

On peut maintenant extraire les données lues dans le fichier dans un dataset en combinant la fonction d'extraction des données extract-data  et la fonction de création de dataset readings-to-dataset.

<br>
**-Solution-**

<pre><code>(defn convert-to-dataset
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

Et bien toujours pas car la date en chaîne n'est pas un timestamp valable.



