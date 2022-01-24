package mono;

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

    private final String DEFAULT_DIRECTION_FOLDER = "src\\main\\resources\\CLIENT_DIR\\";

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
        System.out.println("Type a file name to send.");
        String nameFile = readCommand();
        System.out.println("Sending the file " + nameFile + " to server.");
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

    public String readCommand() {
        Scanner scanCmd = new Scanner(System.in);
        return scanCmd.nextLine();
    }

    public static void main(String args[]) throws Exception {
        // Création du client FTP avec choix du nom et du port
        FTPClient client = new FTPClient();
        client.startConnection("localhost", 6846);

        while(true){
            // Gestion des commandes
            System.out.println("Waiting for a request [Type commands here. More information with *help*]");

            client.out.flush();
            String cmd = client.readCommand().toUpperCase();

            switch (cmd){
                case "LS":{
                    client.out.println("LS_DIR");
                    System.out.println(client.in.readLine());
                    break;
                }
                case "GET":{
                    client.out.println("GET_FILE");
                    client.saveFile();
                    break;
                }
                case "PUT":{
                    client.out.println("PUT_FILE");
                    client.sendFile();
                    break;
                }
                case "STOP":{
                    client.out.println("STOP");
                    client.stopConnection();
                    System.out.println("Stopping the communication with the server.");
                    System.exit(1);
                }
                case "HELP":{
                    System.out.println("List of available commands...");
                    System.out.println("LS\t\tTo list computer files in server root folder");
                    System.out.println("GET\t\tTo get a file from server");
                    System.out.println("PUT\t\tTo send a file to server");
                    System.out.println("STOP\t\tTo stop communicating with server");
                    break;
                }
                default:{
                    System.out.println("Oops... An error has occurred. Consult the support with HELP if necessary.");
                }
            }
        }
    }
}
