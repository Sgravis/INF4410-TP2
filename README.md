# INF4410-TP2

1- Build du projet via la commande ant dans le dossier racine.
2- se déplacer dans le répertoire bin/ (cd bin/)
3- Lancer la commande "rmiregistry PORT &" remplacer PORT par le port souhaité (Par défaut, utiliser le port 5000)
4- Revenir dans le dossier racine : "cd .."
5- Lancer le serveur avec la commande : "./server CAPACITÉ TAUX-MALVEILLANCE"
	Remplacer CAPACITÉ par la valeur voulue
	Remplacer TAUX-MALVEILLANCE par une valeur entre 0 et 100
5-bis(optionnel)- Si le numéro de port du rmiregistry est modifié (différent de 5000), il faut modifier le fichier source "src/server/Server.java de la manière suivante : 
	Ligne 78, remplacer l'argument de la fonction LocateRegistry.getRegistry(PORT-DU-RMIREGISTRY)
	Ne pas oublier de build à nouveau
6- Répéter l'opération pour avoir le nombre de serveurs voulu (Utiliser des postes différents à chaque fois via SSH)
7- Modifier le fichier "src/client/Servers.txt" pour ajouter tous les serveurs sous la forme :
	"AdresseIP:PORT:CAPACITÉ"
8- lancer le client avec la commande "./client NOM-FICHIER-OPERATIONS MODE"
	Remplacer NOM-FICHIER-OPERATIONS par le nom d'un fichier se trouvant dans le dossier Operations à la racine du projet
	Remplacer MODE par 0 pour exécuter le programme en mode sécurisé et 1 pour exécuter le programme en mode non-sécurisé


Exemple d'utilisation : 

Mode sécurisé 2 serveurs avec capacité 3:
1- ant dans le fichier racine
2- se connecter en ssh au deuxième poste
3- lancer le rmiregistry dans le repertoire bin (rmiregistry 5000 &) sur les deux postes
4- lancer les deux serveurs (dans le dossier racine : ./server 3 0)
5- Modifier le fichier src/client/Servers.txt de la maniere suivante : 
	IP_1:5000:3
	IP_2:5000:3 
IP_1 et IP_2 correspondent respectivement aux adresses IP des postes 1 et 2
6- lancer le client (dans le dossier racine : ./client FICHIER_OPERATION 0) en remplacant FICHIER_OPERATION par le fichier voulu


Mode non sécurisé avec 3 serveurs de capacité 5 dont 1 avec un taux de malveillance de 50% (Les deux autres sont de bonne foi):
1- ant dans le fichier racine
2- se connecter en ssh au deuxième poste
3- se connecter en ssh au troisième poste
4- lancer le rmiregistry dans le repertoire bin (rmiregistry 5000 &) sur les trois postes
5- lancer deux serveurs (dans le dossier racine : ./server 5 0)
6- lancer le troisième serveur (dans le dossier racine : ./server 5 50)
7- Modifier le fichier src/client/Servers.txt de la maniere suivante :
        IP_1:5000:5
        IP_2:5000:5
	IP_3:5000:5
8- lancer le client (dans le dossier racine : ./client FICHIER_OPERATION 1) en remplacant FICHIER_OPERATION par le fichier voulu


Pour les autres tests, il suffit de modifier les paramètres des deux exemples précédents avec les valeurs voulues.
