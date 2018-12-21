Projet NoSQL : Redis

Nous avons Simulé un call center avec Redis en JAVA :

Appels avec certaines propriétés
  Identifiant
  Heure d’appel
  Numéro d’origine
  Statut (Non affecté, Non pris en compte, En cours, Terminé) 
  Durée
  Opérateur qui le traite
  Texte descriptif

Opérateurs avec certaines propriétés
  Identifiant
  Nom
  Prénom
  
Fonctiones and results od the code : 
1. Ajout du nouvel appel et d'opérateur

===========================Calls==============================
call1：[1, 15:15, 0666666668, finished, 100s, i want eating., 2]
call2：[2, 18:55, 0666556668, treating, 250s, i'd like to meet Mr.Wang., 2]
call3：[3, 21:01, 0666559968, no affected, 110s, How are you?]
call4：[4, 23:51, 0612345968, no affected, 156s, Can we work together?]
call5：[5, 13:51, 0612345678, no answered, 20s]

=========================Operators============================
op1：[1, Jack, LOUIS]
op2：[2, Rose, JEAN]
op3：[3, Micheal, OLIVIER ]

2. Ensemble des appels en cours, à affecter

======================Calls being treated=====================
call2[2, 18:55, 0666556668, treating, 250s, i'd like to meet Mr.Wang., 2]

======================Calls not affected======================
call3[3, 21:01, 0666559968, no affected, 110s, How are you?]
call4[4, 23:51, 0612345968, no affected, 156s, Can we work together?]

3. Affectation d’un nouvel appel

======================Affecte a call==========================
call3：[3, 21:01, 0666559968, treating, 110s, How are you?, 1]

4. Ensemble des appels en cours de traitement, par opérateur

=========Calls being treated and its operator's name==========
call2[2, 18:55, 0666556668, treating, 250s, i'd like to meet Mr.Wang., 2] operator name : Rose
call3[3, 21:01, 0666559968, treating, 110s, How are you?, 1] operator name : Jack

