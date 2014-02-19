Hands On IncanteR
======================

Vous venez de livrer une application. Vous avez quelques soucis de performance mais fort heureusement les développeurs ont pensé à mettre des logs pour tracer les temps de réponse des appels de services.

Ce hands-on est dérivé de scripts réellement utilisés et collectés sous https://github.com/cfalguiere/Organa. Ce projet a pour but de fournir une boite à outils pour le traitement de fichiers de logs de temps de réponse.

Ce hands-on nécessite leiningen et un éditeur.

Materiel du cours
-----------------------
<br>

Si vous avez Git vous pouvez cloner le repository

<pre><code>$ git clone https://github.com/cfalguiere/Hands-On-Incanter/tree/master
</code></pre>


Si vous n'avez pas git, vous pouvez aller sur [Repository](https://github.com/cfalguiere/Hands-On-Incanter/tree/master)
utiliser le bouton "Download ZIP" dans la colonne de droite.

Ce dossier contient 

- Presentations : la présentation montrée en introduction
- Instructions : les instructions pour réaliser le hands on
- elements : les fichiers nécessaires à chaque step
- logs : des fichiers de log à utiliser dans le projet
- solutions : les projets lein finis pour chaque step

Les chemins utilisés dans les instructions supposent que vous avez crée votre projet lein dans ce répertoire.


Prise en main d'IncanteR
-----------------------
<br>

[Prise en main IncanteR](prise-en-main-incanter.md)

### Documentation
- [Incanter](http://incanter.org/)
- [Getting Started](https://github.com/liebke/incanter/wiki)
- [Documentation](http://data-sorcery.org/contents/)
- [Accès direct à l'API](http://liebke.github.io/incanter/)

Prise en main de Midje
-----------------------
<br>

Nous allons utiliser Midje au lieu de Clojure.test.

[Prise en main Midje](prise-en-main-midje.md)

### Documentation
[Documentation](https://github.com/marick/Midje/wiki)



Step1 - Première fonction
-----------------------
<br>
Pour commencer nous alons écrire le test de la fonction q qui raccourci l'écriture du quantile.

Vous trouverez les instructions dans 
[Première fonction](premiere-fonction.html)


Step 2 - Lire le log
-----------------------
<br>
Le log n'est pas en csv à l'origine. Nous allons écrire quelques fonctions qui lisent et parsent le log dans son format d'origine.

Vous trouverez les instructions dans 
[Lire le log](lire-le-log.thml)


Step 3 - Calculer les timestamps
-----------------------
<br>
Le fichier contient des dates sous forme de chaîne. Dans IncanteR nous avons besoin de timestampss en millisecondes.

Nous allons utilier clj-time qui est un wrapper sur Joda Time

Vous trouverez les instructions dans 
[Calculer les timestamps](timestamps.html)


Step 4 - Des charts améliorés
-----------------------
<br>
Maintenant que nous avons des timestamps nous allons pouvoir afficher des séries chronologiques. Nous allons aussi voir comment configurer les charts.

Vous trouverez les instructions dans 
[Des charts améliorés](perf-charts.html)

Step 5 - Un summary amélioré
-----------------------
<br>
La fonction summary d'IncanteR affche un résumé assez basique. Nous allons construire un summary plus adapté à nos besoins.

Vous trouverez les instructions dans 
[Un summary amélioré](stats-summary.html)


Step 6 - main
-----------------------
<br>
Nous allons assembler tout ça pour faire un programme.

Vous trouverez les instructions dans 
[Assemblage](main.html)


