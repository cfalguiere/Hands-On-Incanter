# Première fonction

Si vous avez sauté les steps précédents vous pouvez trouver le projet initial de ce step dans Instructions/step1-premiere-fonction/initial.

Pour commencer nous alons écrire le test de la fonction q qui raccourci l'écriture du quantile. 

Rappelez vous que le quantile 0 est le le minimum de la liste. Cela facilitera votre test.

Pensez à importer le module incanter.stats pour la fonction quantile. 

Le quantile renvoie un décimal qu'il faudra arrondir pour pouvoir le comparer avec la valeur minimale de la série qui est un entier. 

Vous aurez besoin de round qui se trouve dans [org.clojure/clojure-contrib "1.2.0"]. Il faudra déclarer la dépendance et importer le module

<br>
**-Solution-**

Dans project.clj
<pre><code>  :dependencies [[org.clojure/clojure "1.5.1"]
		 [incanter "1.5.4"]
		 [org.clojure/clojure-contrib "1.2.0"]])
</code></pre>

Dans core.clj
<pre><code>(ns hoincanter.core
  (:use [incanter.stats :only [quantile]]))

(defn q [p serie] (quantile serie :probs [p])) 
</code></pre>

Dans core_test.clj
<pre><code>(ns hoincanter.core-test
  (:use  [midje.sweet])
  (:require [clojure.test :refer :all]
            [hoincanter.core :refer :all])
  (:use [clojure.contrib.generic.math-functions :only [round]]))

(fact "quantile 0 equals min of the list"
      (round (first (q 0 [3 2 1]))) => 1 )
</code></pre>



Vérifiez que vous pouvez utiliser la fonction q dans le REPL. Vous devrez importer le module hoincanter.core.

<pre><code>$ lein repl

user=> (use 'hoincanter.core)
user=> (q 0 [3 2 1])
</code></pre>