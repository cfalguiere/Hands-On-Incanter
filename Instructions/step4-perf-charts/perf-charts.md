# Des charts améliorés

Maintenant que nous avons des timestamps nous allons pouvoir afficher des séries chronologiques. Nous allons aussi voir comment configurer les charts.

Un ensemble de fonctions de chart avec un paramétrage des titres, des couleurs vous sont fournies dans Instructions/step4-perf-charts/perf-charts.txt. 

Placez ces fonctions dans un module charts dans le projet hoincanter. 

Adaptez les fonctions car la source n'a pas les mêmes noms de colonnes et traduisez les titres en français.

Ensuite depuis le REPL, essayez chacune des fonctions sur les relevés dont le temps est supérieur à 40 ms.

user=> (def ds (convert-to-dataset "../logs/TIME_MONITOR_2013-12-20.log"))
user=> (def dslong  ($where {:duration {:gt 40}} ds))


