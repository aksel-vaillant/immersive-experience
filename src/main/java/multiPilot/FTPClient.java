package multiPilot;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class FTPClient {
    private Socket clientSocket;

    private BufferedReader in;
    private PrintWriter out;

    private InputStream is;
    private OutputStream os;

    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    // On Windows
    //private static final String DEFAULT_DIRECTION_FOLDER = "src\\main\\resources\\CLIENT_DIR\\";
    // On Linux
    private static final String DEFAULT_DIRECTION_FOLDER = "resources/CLIENT_DIR/";


    public void startConnection(String name, int port) throws IOException {
        clientSocket = new Socket(name, port);

        is = clientSocket.getInputStream();
        os = clientSocket.getOutputStream();

        out = new PrintWriter(os, true);
        in = new BufferedReader(new InputStreamReader(is));

        dataInputStream = new DataInputStream(is);
        dataOutputStream = new DataOutputStream(os);
    }

    public void stopConnection() throws IOException {
        is.close();
        os.close();

        in.close();
        out.close();

        clientSocket.close();
    }

    private void sendFile() throws Exception{
        // Name of the file
        String nameFile = in.readLine();
        System.out.println("Sending the file " + nameFile + " to client.");

        // Opening the file feed
        File file = new File(DEFAULT_DIRECTION_FOLDER + nameFile);
        FileInputStream fileInputStream = new FileInputStream(file);

        // Sending file size
        dataOutputStream.writeLong(file.length());
        System.out.println("Size of the file " + file.length() + " bytes");

        // Breaking file into chunks
        int bytes = 0;
        byte[] buffer = new byte[4*1024];
        while ((bytes=fileInputStream.read(buffer))!=-1){
            dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
        }
        fileInputStream.close();
        System.out.println("The file has been sent successfully.");
    }

    private void saveFile() throws Exception{
        System.out.println("Receiving a file request from the client.");
        String nameFile = in.readLine();

        // Creating the file
        File file = new File(DEFAULT_DIRECTION_FOLDER + nameFile);
        System.out.println("Saving the file " + nameFile + " in progress.");
        FileOutputStream fileOutputStream = new FileOutputStream(file);

        // Read file size
        long sizeFile = dataInputStream.readLong();
        System.out.println("Size of the file " + sizeFile + " bytes");

        int bytes = 0;
        byte[] buffer = new byte[4*1024];
        while (sizeFile > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, sizeFile))) != -1) {
            fileOutputStream.write(buffer,0,bytes);
            sizeFile -= bytes;      // read upto file size
        }

        fileOutputStream.close();
        System.out.println("The file has been saved successfully.");
    }

    private void playFile() throws Exception{
        System.out.println("Receiving a file request from the server.");
        String pathFile = DEFAULT_DIRECTION_FOLDER + in.readLine();
        String fullCommand = "omxplayer --display 2 " + pathFile;

        System.out.println("Gonna start to play video");

        Process play = Runtime.getRuntime().exec(fullCommand);
        play.waitFor();

        System.out.println("Have a nice day! ;)");
    }

    private void displayFile() throws Exception{
        System.out.println("Receiving a file request from the server.");
        String pathFile = DEFAULT_DIRECTION_FOLDER + in.readLine();
        String fullCommand = "feh -qrYzFD120 --zoom fill " + pathFile;

        System.out.println("Gonna start to play video");

        Process play = Runtime.getRuntime().exec(fullCommand);
        play.waitFor();

        System.out.println("Have a nice day! ;)");
    }

    public static void main(String args[]) throws Exception {
        // Création du client FTP avec choix du nom et du port
        FTPClient client = new FTPClient();
        client.startConnection("169.254.236.142", 6846);

        while(true){
            // Gestion des commandes
            System.out.println("\nWaiting for an order from Master's PC");

            client.out.flush();
            String cmd = client.in.readLine();

            switch (cmd){
                case "LS_DIR": {
                    System.out.println("Display files in root folder of server.");
                    String[] pathnames;
                    File readerPath = new File(DEFAULT_DIRECTION_FOLDER);
                    pathnames = readerPath.list();

                    String files = "";
                    for (String pathname : pathnames) {
                        files += pathname + "\t";
                    }
                    client.out.flush();
                    client.out.println(files);
                    System.out.println(files);
                    break;
                }
                case "GET_FILE":{
                    client.sendFile();
                    break;
                }
                case "PUT_FILE":{
                    client.saveFile();
                    break;
                }
                case "PLAY_FILE":{
                    client.playFile();
                    break;
                }
                case "DISPLAY_FILE":{
                    client.displayFile();
                    break;
                }
                case "MESSAGE":{
                    client.out.flush();
                    System.out.println(client.in.readLine());
                }
                case "STOP":{
                    client.out.println("STOP");
                    client.stopConnection();
                    System.out.println("Stopping the communication with the server.");
                    System.exit(1);
                }
            }
        }
    }
}
