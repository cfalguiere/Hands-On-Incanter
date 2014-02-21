Des charts améliorés
===========

Si vous avez sauté les steps précédents vous pouvez trouver le projet initial de ce step dans Instructions/step4-perf-charts/initial.

Maintenant que nous avons des timestamps nous allons pouvoir afficher des séries chronologiques. Nous allons aussi voir comment adapter les charts à nos goûts et nos besoin.

1 - Compréhension du code fourni
-----------
<br>
Un ensemble de fonctions de chart avec un paramétrage des titres, des couleurs vous sont fournies dans Instructions/step4-perf-charts/perf-charts.txt. 

Placez ces fonctions dans un module charts dans le projet hoincanter. Vous devrez créer un fichier charts.clj dans src.

Pour commencer, expliquez à quoi sert doto. Essayez de réécrire count-bar-chart sans le doto.

Et tant que nous y sommes, pourquoi certaines fonctions commencent par . ou finissent par . ?

**-Solution-**

Les charts s'appuient sur JFreeChart. Une partie est wrappée, pour une autre il faut s'en remettre à l'interop.

Certaines couleurs sont fournies par exemple java.awt.Color/red. Pour spécifier d'autres couleurs, il faut créer un objet couleur.  java.awt.Color. appele le constructeur de la classe Java Color pour construire un objet Color. Le point en dernière position est un constructeur.

De même les modifications courantes, ajouter une série, changer la couleur ou l'épaisseur sont wrappées. Mais pour l'histogramme, la modification d'apparence requirt l'accès au renderer. 
histogram crée un objet JFreeChart sur lequel nous pouvons obtenir le renderer et le manipuler par les méthodes de renderer. .setPaint, .getPlot, .getRenderer sont des appels de méthodes sur les objets Java correspondants.

doto permet de chaîner des fonctions alors qu'elles sont présentées successivement. La sortie du premier argument est passé en entrée du deuxième, etc.

C'est très utilisé pour les wrappers sur les builders Java où l'on a une structure de type chart.setAttr1(x).setAttr2(y)) qui devrait s'écrire en Clojure (setAtt2 (setAttr1 (chart) x ) y).

Sans le doto, le code ressemblerait à ça
<pre><code>(set-stroke-color      
      (bar-chart factor :t :vertical false
		 :title (str "Count by " (name factor))
		 :x-label (name factor)
		 :y-label nil
		 :data  ($rollup count :t factor ds))
      (java.awt.Color. 121 209 24) :series 0)
</code></pre> 


2 - Adaptation des charts customisés
-----------
Adaptez les fonctions car la source n'a pas les mêmes noms de colonnes et traduisez les titres en français.

Vous aurez besoin du module incanter.charts et d'autres modules d'incanter.

3 - Tests manuels

Ce type de fonction est difficile à tester en test unitaire car les images sont différentes à chaque run. On peut tout au plus faire un test électrique pour vérifier que les objets JFreeChart sont bien créés.

Depuis le REPL, essayez chacune des fonctions sur les relevés dont le temps est supérieur à 40 ms.

<br>
**-Solution-**
<pre><code>user=> (use '(hoincanter core charts)
user=> (use '(incanter core charts))
user=> (def ds (load-dataset "../logs/TIME_MONITOR_2013-12-20.log"))
user=> (def dslong  ($where {:duration {:gt 40}} ds))
user=> (view (perf-histogram dslong))
</code></pre>


