# Des charts améliorés

Maintenant que nous avons des timestamps nous allons pouvoir afficher des séries chronologiques. Nous allons aussi voir comment configurer les charts.

Un ensemble de fonctions de chart avec un paramétrage des titres, des couleurs vous sont fournies dans elements/perf-charts.txt. Placez ces fonctions dans un module charts dans le projet hoincanter. Adaptez les fonctions si besoin et traduisez les titres en français.

Ensuite depuis le REPL, essayez chacune des fonctions sur les relevés dont le temps est supérieur à 40 ms.

user=> (def ds (convert-to-dataset "../logs/TIME_MONITOR_2013-12-20.log"))
user=> (def dslong  ($where {:duration {:gt 40}} ds))

