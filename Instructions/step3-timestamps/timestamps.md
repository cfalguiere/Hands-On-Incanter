Calculer les timestamps
================

Si vous avez sauté les steps précédents vous pouvez trouver le projet initial de ce step dans Instructions/step3-timestamps/initial.

Le fichier contient des dates sous forme de chaîne. Dans IncanteR nous avons besoin de timestampss en millisecondes.

Nous allons utilier clj-time qui est une librairie de manipulation des dates. C'est un wrapper sur Joda Time

[clj-time](https://github.com/clj-time/clj-time)

1 - Essais dans le REPL
-----------
<br>

Ajoutez [clj-time "0.6.0"] dans le projet.clj

Dans le REPL importez clj-time.core, clj-time.format et clj-time.coerce. 

La fonction date-time vous permet de créer une date et to-long de la convertie en long.

<pre><code>user=> (use '(clj-time core coerce))
user=> (def d (date-time 2013 12 19 8 0 1 004))
#'user/d
user=> d
#<DateTime 2013-02-01T14:05:00.000Z>
user=> (to-long d)
1359727500000
</code></pre>

Est ce que ça marche sur notre fichier. Malheureusement pas. le format est presque ISO 8601 mais pas tout à fait.

<pre><code>user=> (to-long "2013-12-19 08:00:01,004")
nil
</code></pre>

Il faut donc définir un formatteur spécifique et utiliser la fonction parse.

<pre><code>user=> (def custom-formatter (formatter "yyyy-MM-dd HH:mm:ss,SSS"))
#'user/custom-formatter
user=> (parse custom-formatter "2013-12-19 08:00:01,004")
#<DateTime 2013-12-19T08:00:01.004Z>
</code></pre>

2 - Modifiez les fonctions pour calculer le timestamp
--------------
<br>

Revenons maintenant dans le code pour utiliser tout ça.

Tout d'abord, ajoutez le custom-formatter et codez la fonction str-to-ts sur le modèle de str-to-long. Cette fonction parse une chaîne contenant la date et calcule le timestamps en millisecondes.

<br>
**-Solution-**
<pre><code>(defn str-to-ts
  "convert a string into a numeric timestamp"
  [x]
  {:pre  [(string? x)]}
  (coerce/to-long  (format/parse custom-formatter x)))
</code></pre>

Modifiez les fonction existantes pour que le dataset ait maintenant 4 colonnes, la dernière est une colonne ts qui contient le timestamp. Ajoutez également les tests correspondants.

<br>
**-Solution-**
<pre><code>(defn build-reading
  "re-seq returns a vector consisting of the line and each group. This function filters out the line and converts duration into a long"
  [[s t l d]]
  [t l (str-to-long d) (str-to-ts t) ] )
  
(defn readings-to-dataset
  "build a dataset from a list of readings"
  [readings]
  {:pre [(seq? readings)] } 
  (dataset [:timestamp :servicename :duration :ts] readings))

</code></pre>

Une fois ces fonctions disponibles vous pouvez visualiser vos données avec le timestamps et enfin le time-series-plot. 

Vous trouverez l'extrait du fichier utilisé pour les tests unitaires dans le répetoire resources et un fichier .log plus complet dans le répertoire logs.

Assurez vous que vous avez bien importé incanter.charts.

<pre><code>user=> (view (load-dataset "resources/sample.log"))
user=> (view (time-series-plot :ts :duration  :data (load-dataset "resources/sample.log")))
</code></pre>

C'est un peu plus intéressant avec plus de données.

<pre><code>user=> (view (load-dataset "../logs/TIME_MONITOR_2013-12-20.log"))
</code></pre>

