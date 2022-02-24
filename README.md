# Subversion - Submersion  

Realized by Aksel Vaillant  

Computer science student at [ENSIM](http://ensim.univ-lemans.fr/fr/index.html) - Le Mans Université (FRANCE)   

Under the direction of Ms. Catherine Cléder

## Some context  

From a goverment-region plan, Anne-Laure Fortin Tournès and Anna Street worked on [Performing Water](https://performingwater.org) and propose series of webinars designed to bring scholars, activists, and artists together in exploring how water shapes our politics and our thinking practices.  

As a group of students from the engineering school of Le Mans - [ENSIM](http://ensim.univ-lemans.fr/fr/index.html), we have chosen for our 4th year's project to create an immersive and interactive exhibition of [M. deCaires Taylor works](https://www.underwatersculpture.com). 

More information about our exhibition show here.  

Furthermore, my part consist to develop something able to synchronize video and audio streams from Raspberry Pi cards and play differents media files, whatever the extension, on video projectors and speakers. 

I will try to explain how I developped and created this program right below this part.

## What you will find here...
I worked on different packages to see my progress but mainly to propose solutions throught "templates" for my future projects and those in needs.

Through mono package, you will find a client-server architecture able to transfer files. It means you can get files from a client and put specific files on client.

And with monoPilot, you'll be able to work with 2 unix libraries nammed [omxplayer](https://github.com/popcornmix/omxplayer) (command-line video player for the Raspberry Pi) and [feh](https://feh.finalrewind.org) (image viewer) which means, from a command-line on server, you can display a picture or play a video on a client. 

To finish, in multiPilot, you can control Raspberry Pi cards from a single one and pilot them from a server.  

## Downloading

`git clone https://github.com/aksel-vaillant/immersive-experience.git`

## Materials

- [x] 1 Raspberry Pi card as server or you can use a PC as well.    
- [x] 1 ethernet cable for the server.     
- [x] X Raspberry Pi cards as clients.   
- [x] 1 pair of hdmi (the output. ie a video-projector, a screen, etc) and ethernet cables for each client.       
- [x] 1 Hub to connect your clients and server all together in a proper local network.

## Step 1 : Making a FCP server/client able to send and save files 

![img.png](img.png)

### How to use it  
To make the program work, you must first fill the DEFAULT_DIRECTION_FOLDER which contains the server and client files, and add a double backslash at the end.  

It is an attribute which is located in the 2 classes : FTPClient and FTPServer.

Example test of the location of the resource files for the client :

    private final String DEFAULT_DIRECTION_FOLDER = "...\\src\\main\\resources\\CLIENT_DIR\\";

First, you have to run FTPServer.main, and then, the FTPClient.main.  
You can try mono package with a video file or a simple picture to transfer a file throught this architecture. 

Ressource : https://heptadecane.medium.com/file-transfer-via-java-sockets-e8d4f30703a5

## Step 2 : Display media files and pilot a Raspberry Pi card 

The aim of this part is to connect a server with a couple of Raspeberry Pi cards to display different contents such as picture or video. 

Once all files setup, I will be able to display and run those contents while piloting those cards with omxplayer (unix library)

<em>in development</em>

## FAQ 




