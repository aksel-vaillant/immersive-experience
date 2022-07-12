# Tutorials for the Raspberry Pi Card 

## Tutorial 1 - How to upgrade my Rasbian OS on my Raspberry Pi card? 

### Solution 1 

1. Once you started your Rasbperry Pi card, open a terminal
2. Before starting, check the last version of your Rasbian
   > uname -a
3. If you don't have the last version, update packages
    > sudo apt-get update
4. Update also every packages on your card and confirm with Y and ENTER.
    > sudo apt-get dist-upgrade 

Now, all your librairies are downloading and installing. It can take some time...


5. Enter the following command to upgrade your Raspbian OS
   > sudo rpi-update
6. To finish, you have to restart your Raspberry card
   > sudo reboot

### Solution 2

1. Once you started your Rasbperry Pi card, open a terminal
2. Open the rapsberry configuration menu
   > sudo raspi-config
3. Select the menu 8 - upgrade - and then press ENTER

## Tutorial 2 - How to play video in command line? 

Before turning on your card, plug your video outputs on your HDMI inputs.
We gonna use the library nammed **omxplayer**. Check the README for the configuration.

The only thing to know here is the configuration.   
To select your HDMI output
 - n = 2 for the HDMI 0
 - n = 7 for the HDMI 1

> omxplayer --display n pathToFile

Now, you are able to play your video on your HDMI output.    
You can also add --loop to play the video until an interruption.

More ressource here : https://www.raspberrypi.com/documentation/computers/os.html#the-omxplayer-application

## Tutorial 3 - How to start my java program automatically when it turns on?

We gonna use the systemd from unix. 

More ressource here : https://www.digitalocean.com/community/tutorials/how-to-use-systemctl-to-manage-systemd-services-and-units

Before starting, be sure your .service file is in the good folder.   
And if you want to make it quick, here a few commands to know in the order
1. >sudo systemctl daemon-reload
2. >sudo systemctl enable nameService.service
3. >sudo systemctl start nameService.service
4. >sudo systemctl status nameService.service

=> If it's green, it means your service is executing itself correctly. Now, you can test it!    
=> If it's red, write the following command

5. >sudo systemctl reload-or-restart       
   
Then, enter the 4th command again.     
If it's still red, check your .service and the location of it. 

If you still have a problem, don't hesitate to message me.
