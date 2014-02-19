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

Vous avez maintenant une tâche midje disponible dans lein.

2 - Ajout de Midje dans le projet
--------------
<br>

Ouvrer le fichier project.clj et ajoutez la dépendance envers Midje. Elle n'est pas utile au run-time mais seulement pour le développement. N'oubliez pas de mettre à jour les dépendances.

<pre><code> :profiles {:dev {:dependencies [[midje "1.5.1"]]}}
</code></pre>
  
Le template de projet contient un fichier core.clj dans src et core_test.clj dans test.

3 - Premier test
-------------
<br>

Ouvrez le fichier test et ajoutez un test Midje très simple puis lancez le test

<pre><code>(ns hoincanter.core-test
(ns hoincanter.core-test
  (:use  [midje.sweet])
  (:require [clojure.test :refer :all]
            [hoincanter.core :refer :all]
	   ))

(fact "dummy test"
      (+ 1 1) => 3)
</code></pre>
        
<pre><code> $ lein midje 
</code></pre>


Les deux tests échouent. Tout va bien !

Relancez un REPL et lancez les tests en suivant les instructions ci-dessous 

<pre><code> user=> (use 'midje.repl)
user=> (autotest)
</code></pre>

Corrigez les deux tests et sauvez. Regardez ce qui se passe dans le REPL.
