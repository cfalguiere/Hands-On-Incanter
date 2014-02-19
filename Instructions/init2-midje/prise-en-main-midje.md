Prise en main de Midje
===========

Nous allons mettre en place midje et apprendre la syntaxe des tests.

1 - installation de Midje
------------------
<br>

Si vous avez déjà lein et midje. Cette étape n'est pas nécessaire.

Ouvrez le fichier profiles.clj qui se trouve dans le répertoire .lein dans votre home et s'il est vide ajoutez la ligne suivante. Sinon insérez le plugin lein-midje au bon endroit.
 
<pre><code>{:user {:plugins [[lein-midje "3.0.0"]]}}
</code></pre>

Si vous tapez
<pre><code>$ lein</code></pre> ous avez maintenant une tâche midje disponible.

2 - Ajout de Midje dans le projet
--------------
<br>

Ouvrer le fichier project.clj et ajoutez la dépendance envers Midje. Elle n'est pas utile au run-time mais seulement pour le développement. N'oubliez pas de mettre à jour les dépendances.

<pre><code> :profiles {:dev {:dependencies [[midje "1.5.1"]]}}
</code></pre>
  


3 - Premier test
-------------
<br>

Le template de projet contient un fichier core.clj dans src et core_test.clj dans test.

Ouvrez le fichier core_test.clj. Ajoutez l'import de midje.sweet et un test Midje très simple puis lancez le test

<pre><code>(ns hoincanter.core-test
  (:use  [midje.sweet])
  (:require [clojure.test :refer :all]
            [hoincanter.core :refer :all]))

(fact "dummy test"
      (+ 1 1) => 3)
</code></pre>
        
<pre><code> $ lein midje 
</code></pre>


Les deux tests échouent. Tout va bien !

Relancez un REPL et lancez les tests en suivant les instructions ci-dessous 

<pre><code>$ lein repl
</code></pre>

<pre><code>user=> (use 'midje.repl)
user=> (autotest)
</code></pre>

Corrigez les deux tests et sauvez. Regardez ce qui se passe dans le REPL. 
