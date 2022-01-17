# Making a FCP server/client able to send and save files
Made by Aksel Vaillant with Maven and IntelliJ with a JDK 1.8

![img.png](img.png)

La configuration nommée TP permet de lancer à la fois le main de FTPServer, dans un premier temps, et ensuite,
le main de FTPClient.

## Programme
Pour faire fonctionner le programme, il faut préalablement remplir le DEFAULT_DIRECTION_FOLDER 
qui contient les fichiers serveur et client, et ajouter un double backslash à la fin (\\), 
c'est un attribut qui se situe dans les 2 classes : FTPClient et FTPServer. 

Exemple test de l'emplacement des fichiers ressources pour le client :

    private final String DEFAULT_DIRECTION_FOLDER = "...\\src\\main\\resources\\CLIENT_DIR\\";

Dans le main de FTPServer, il faut ainsi commencer à lancer le serveur. Les diverses commandes s'effectuent 
correctement avec le client.

Egalement, le choix de ne pas vouloir split la commande en plusieurs sous parties est voulu pour
une expérience plus compréhensible.

