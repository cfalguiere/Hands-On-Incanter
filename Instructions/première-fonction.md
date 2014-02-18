# Première fonction

Pour commencer nous alons écrire le test de la fonction q qui raccourci l'écriture du quantile. Rappelez vous que le quantile 0 est le le minimum de la liste et pensez à importer le module stats.

<pre><code>(defn q [p serie] (quantile serie :probs [p])) 

(fact "quantile 0 equals min of the list"
      (round (first (q 0 [3 2 1]))) => 1 )
</code></pre>

Vous devez importer incanter.stats et clojure.contrib.generic.math-functions qui se trouve dans [org.clojure/clojure-contrib "1.2.0"].

Vérifiez que vous pouvez utiliser la fonction q dans le REPL. Vous devez importer le module hoincanter.core.
