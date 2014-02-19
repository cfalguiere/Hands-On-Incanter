Assemblage final
================

Ecrivez une fonction main.

Cette fonction accepte un fichier en paramètre, le parse, calcule le tableau de statistiques et le sauve dans un fichier et sauve un des charts.

Vous pouvez utiliser la fonction save sur les charts et les dataset

Par exemple :

<pre><code>(save (metrics-ds)  "stats.csv")
(save (count-bar-chart data-ds :servicename) "barchart.png")
</code></pre>

Le main est dans le code. Vous devez ajouter le main au project.clj et coder la fonction appelée.

Le main s'execute avec 

<pre><code>lein run resources/sample.log 
</code></pre>

