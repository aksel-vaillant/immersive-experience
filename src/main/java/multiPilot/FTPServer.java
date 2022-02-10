package multiPilot;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class FTPServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;

    private BufferedReader in;
    private PrintWriter out;

    private InputStream is;
    private OutputStream os;

    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    private HandlerClient handler;
    private Socket currentClientSocket;
    private static Map<String, Socket> listClient = new HashMap<>();

    private static final String DEFAULT_DIRECTION_FOLDER = "src\\main\\resources\\SERVEUR_DIR\\";

    public void start(int port) throws IOException, InterruptedException {
        System.out.println("Server initialized");

        // Searching for new clients
        handler = new HandlerClient(port);
        handler.start();

        System.out.println("Before initializing");

        while(listClient.size()!=0){
            System.out.println("Waiting for new clients");
            listClient = handler.getListClient();
            TimeUnit.SECONDS.sleep(5);
        }
        System.out.println("done");
    }

    public void stop() throws IOException {
        System.out.println("Stopping the server and all communication.");

        is.close();
        os.close();

        in.close();
        out.close();

        clientSocket.close();
        serverSocket.close();
    }

    private void saveFile() throws Exception{
        System.out.println("Type a file name to get.");
        String nameFile = readCommand();
        out.println(nameFile);

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
            sizeFile -= bytes;      // Read upto file size
        }
        fileOutputStream.close();
        System.out.println("The file has been saved successfully.");
    }

    private void sendFile() throws Exception{
        // Name of the file
        System.out.println("Type a file name to send.");
        String nameFile = readCommand();
        System.out.println("Sending the file " + nameFile + " to client.");
        out.println(nameFile);

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

    private void playFile(){
        // Name of the file
        System.out.println("Type a file name to send.");
        String nameFile = readCommand();
        System.out.println("Playing the file " + nameFile + " on client.");
        out.println(nameFile);
    }

    private void displayFile(){
        // Name of the file
        System.out.println("Type a file name to send.");
        String nameFile = readCommand();
        System.out.println("Displaying the file " + nameFile + " on client.");
        out.println(nameFile);
    }

    public String readCommand() {
        Scanner scanCmd = new Scanner(System.in);
        return scanCmd.nextLine();
    }

    public static void main(String[] args) throws Exception {
        // Création du serveur FTP avec choix du port
        FTPServer server = new FTPServer();
        server.start(6846);

        while(true){
            // Gestion des commandes
            System.out.println("\nWaiting for a request [Type commands here. More information with *help*]");


            server.out.flush();
            String cmd = server.readCommand().toUpperCase();

            // Split commands

            switch (cmd){
                case "LS":{
                    server.out.println("LS_DIR");
                    System.out.println(server.in.readLine());
                    break;
                }
                case "GET":{
                    server.out.println("GET_FILE");
                    server.saveFile();
                    break;
                }
                case "PUT": {
                    server.out.println("PUT_FILE");
                    server.sendFile();
                    break;
                }
                case "PLAY": {
                    server.out.println("PLAY_FILE");
                    server.playFile();
                    break;
                }
                case "DISPLAY": {
                    server.out.println("DISPLAY_FILE");
                    server.displayFile();
                    break;
                }
                case "SELECT": {
                    listClient = server.handler.getListClient();
                    System.out.println(listClient.entrySet());
                    break;
                }
                case "STOP":{
                    server.stop();
                    System.exit(1);
                }
                case "HELP":{
                    System.out.println("List of available commands...");
                    System.out.println("LS\t\tTo list computer files in server root folder");
                    System.out.println("GET\t\tTo get a file from server");
                    System.out.println("PUT\t\tTo send a file to server");
                    System.out.println("STOP\tTo stop communicating with server");
                    break;
                }
                default:{
                    System.out.println("Oops... An error has occurred. Consult the support with HELP if necessary.");
                }
            }
        }
    }
}
