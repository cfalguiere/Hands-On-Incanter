Des charts améliorés
===========

Si vous avez sauté les steps précédents vous pouvez trouver le projet initial de ce step dans Instructions/step4-perf-charts/initial.

Maintenant que nous avons des timestamps nous allons pouvoir afficher des séries chronologiques. Nous allons aussi voir comment configurer les charts.

Un ensemble de fonctions de chart avec un paramétrage des titres, des couleurs vous sont fournies dans Instructions/step4-perf-charts/perf-charts.txt. 

Placez ces fonctions dans un module charts dans le projet hoincanter. Vous devrez créer un fichier charts.clj dans src.

Adaptez les fonctions car la source n'a pas les mêmes noms de colonnes et traduisez les titres en français.

Vous aurez besoin du module incanter.charts et d'autres modules.

Ensuite depuis le REPL, essayez chacune des fonctions sur les relevés dont le temps est supérieur à 40 ms.

<br>
**-Solution-**
<pre><code>user=> (use '(hoincanter core charts)
user=> (def ds (convert-to-dataset "../logs/TIME_MONITOR_2013-12-20.log"))
user=> (def dslong  ($where {:duration {:gt 40}} ds))
user=> (view (perf-histogram dslong))
</code></pre>


