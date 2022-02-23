package multiPilot;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class FTPServer {
    private ServerSocket serverSocket;
    private Socket selectedClientSocket;

    private BufferedReader in;
    private PrintWriter out;

    private InputStream is;
    private OutputStream os;

    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    private static Map<Integer, Socket> listClient = new HashMap<>();

    private static final String DEFAULT_DIRECTION_FOLDER = "src\\main\\resources\\SERVEUR_DIR\\";

    public void start(int port) throws IOException {
        System.out.println("Starting the server.");
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(1000 * 10);
        while(listClient.isEmpty()){
            searchClient();
        }
        selectClient(1);
    }

    public void stop() throws IOException {
        System.out.println("Stopping the server and all communication.");

        is.close();
        os.close();

        in.close();
        out.close();

        // Close each clients
        for(int i = 1; i < listClient.size() + 1; i++){
            selectedClientSocket = listClient.get(i);
            sendMessage("Close the server");
            selectedClientSocket.close();
        }
        
        serverSocket.close();
    }

    private void searchClient(){
        System.out.println("Seeking for clients. [Wait approximately 10s]");
        while(true){
            try{
                Socket clientSocket = serverSocket.accept();

                if(!listClient.containsValue(clientSocket)){
                    //System.out.println("New client\t" + clientSocket);
                    listClient.put(listClient.size()+1,clientSocket);
                    //System.out.println(listClient.entrySet());
                }else{
                    // Send a message welcome back to the client!
                    System.out.println("Welcome back! :3");
                }
            }
            catch (SocketTimeoutException e){
                System.out.println("Stop searching.");
                return;
            }
            catch (IOException e){
                System.out.println("A lil error happened in the handler client.");
                e.printStackTrace();
            }
        }
    }

    private void selectClient(int idClient) throws IOException {
        selectedClientSocket = listClient.getOrDefault(idClient, selectedClientSocket);

        is = selectedClientSocket.getInputStream();
        os = selectedClientSocket.getOutputStream();

        in = new BufferedReader(new InputStreamReader(is));
        out = new PrintWriter(os, true);

        dataInputStream = new DataInputStream(is);
        dataOutputStream = new DataOutputStream(os);
    }

    private void sendMessage(String message){
        out.println(message);
    }

    private String readMessage() throws IOException {
        return in.readLine();
    }

    private String readCommand() {
        Scanner scanCmd = new Scanner(System.in);
        return scanCmd.nextLine();
    }

    private void saveFile(String nameFile) throws Exception{
        sendMessage("GET_FILE");
        sendMessage(nameFile);

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

    private void sendFile(String nameFile) throws Exception{
        sendMessage("PUT_FILE");
        sendMessage(nameFile);

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

    private void playFile(int idClient, String nameFile) throws Exception {
        selectClient(idClient);

        sendMessage("LS_DIR");
        if(!readMessage().contains(nameFile)){
            sendFile(nameFile);
        }

        sendMessage("PLAY_FILE");
        sendMessage(nameFile);
    }

    private void displayFile(int idClient, String nameFile) throws Exception {
        selectClient(idClient);

        sendMessage("LS_DIR");
        if(!readMessage().contains(nameFile)){
            sendFile(nameFile);
        }

        sendMessage("DISPLAY_FILE");
        sendMessage(nameFile);
    }

    public static void main(String[] args) throws Exception {
        // Création du serveur FTP avec choix du port
        FTPServer server = new FTPServer();
        server.start(6846);
        System.out.println("At the moment, there's " + listClient.size() + " client(s) connected.");

        while(true){
            // Gestion des commandes
            System.out.println("\nWaiting for a request [Type commands here. More information with *help*]");
            server.out.flush();
            String[] cmds = server.readCommand().split("\\s+");

            // Split commands
            switch (cmds[0].toUpperCase()){
                case "SELECT": {
                    if(cmds.length!=2) break;
                    int selectedClientId = Integer.parseInt(cmds[1]);
                    server.selectClient(selectedClientId);
                    break;
                }
                case "SEARCH":{
                    int size = listClient.size();
                    server.searchClient();
                    if(listClient.size() > size) System.out.println("There's " + (listClient.size() - size) + " new client(s).");
                    break;
                }
                case "LS":{
                    server.sendMessage("LS_DIR");
                    System.out.println(server.readMessage());
                    break;
                }
                case "GET":{
                    if(cmds.length!=2){
                        System.out.println("You need 2 arguments to use this function : the function's name itself and the name of file you wanna get from the client.");
                        break;
                    }
                    String nameFile = cmds[1];
                    server.saveFile(nameFile);
                    break;
                }
                case "PUT": {
                    if(cmds.length!=2){
                        System.out.println("You need 2 arguments to use this function : the function's name itself and the name of file you wanna put on the client.");
                        break;
                    }
                    String nameFile = cmds[1];
                    server.sendFile(nameFile);
                    break;
                }
                case "PLAY": {
                    try {
                        //server.sendMessage("PLAY_FILE");
                        if(cmds.length!=3){
                            System.out.println("You need 3 arguments to use this function : the function's name itself, the id of your client and the name file.");
                            break;
                        }
                        int selectedClientId = Integer.parseInt(cmds[1]);
                        String nameFile = cmds[2];
                        server.playFile(selectedClientId, nameFile);
                    }
                    catch (Exception e){
                        System.out.println("An error occured. You can use play function like this : play 1 jason.mov");
                    }
                    break;
                }
                case "DISPLAY": {
                    try{
                        //server.sendMessage("DISPLAY_FILE");
                        if(cmds.length!=3){
                            System.out.println("You need 3 arguments to use this function : the function's name itself, the id of your client and the name file.");
                            break;
                        }
                        int selectedClientId = Integer.parseInt(cmds[1]);
                        String nameFile = cmds[2];
                        server.displayFile(selectedClientId, nameFile);
                    }catch (Exception e){
                        System.out.println("An error occured. You can use display function like this : display 1 jason.mov");
                    }
                    break;
                }
                case "EX":{
                    server.playFile(1, "jason.mov");
                    server.playFile(2, "jason.mov");

                    /*for(int i = 1; i<3; i++){
                        server.selectClient(i);
                        server.sendMessage("Ceci est un message pour toi n*" + i);
                    }*/

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
