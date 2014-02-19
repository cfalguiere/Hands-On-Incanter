Assemblage final
================

Si vous avez sauté les steps précédents vous pouvez trouver le projet initial de ce step dans Instructions/step6-main/initial.

Ecrivez une fonction main.

Cette fonction accepte un fichier en paramètre, le parse, calcule le tableau de statistiques et le sauve dans un fichier et sauve un des charts.

Vous pouvez utiliser la fonction save sur les charts et les datasets.

Par exemple :

<pre><code>user=> (save (metrics-ds)  "stats.csv")
user=> (save (count-bar-chart data-ds :servicename) "barchart.png")
</code></pre>

Le main est dans le code. Vous devez ajouter le main au project.clj et coder la fonction appelée.

Le main s'execute avec 

<pre><code>lein run resources/sample.log 
</code></pre>

<br>
**-Solution-**
<pre><code>(defn response-time-analysis
  "compute and save response time statistics"
  [filename]
  {:pre  [(string? filename)] } 
  (let [ data-ds (convert-to-dataset "resources/sample.log")
	 metrics (compute-metrics metric-functions data-ds) 
	 metrics-ds (metrics-to-dataset metric-functions metrics) ]
    (print metrics-ds)
    (save metrics-ds  "stats.csv")
    (save (count-bar-chart data-ds :servicename) "barchart.png")
    ))
</code></pre>
