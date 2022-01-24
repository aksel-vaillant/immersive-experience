package mono;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FTPServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;

    private BufferedReader in;
    private PrintWriter out;

    private InputStream is;
    private OutputStream os;

    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    private static final String DEFAULT_DIRECTION_FOLDER = "src\\main\\resources\\SERVEUR_DIR\\";

    public void start(int port) throws IOException {
        System.out.println("Starting the server.");

        serverSocket = new ServerSocket(port);
        clientSocket = serverSocket.accept();

        is = clientSocket.getInputStream();
        os = clientSocket.getOutputStream();

        in = new BufferedReader(new InputStreamReader(is));
        out = new PrintWriter(os, true);

        dataInputStream = new DataInputStream(is);
        dataOutputStream = new DataOutputStream(os);
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

    public static void main(String[] args) throws Exception {
        // Création du serveur FTP avec choix du port
        FTPServer server = new FTPServer();
        server.start(6846);

        while(true){
            // Gestion des 3 commandes et l'arrêt du serveur
            System.out.println("\nWaiting for a request from client");

            server.out.flush();
            String cmd = server.in.readLine();

            switch (cmd){
                case "GET_FILE":{
                    server.sendFile();
                    break;
                }
                case "PUT_FILE":{
                    server.saveFile();
                    break;
                }
                case "LS_DIR":{
                    System.out.println("Display files in root folder of server.");
                    String [] pathnames;
                    File readerPath = new File(DEFAULT_DIRECTION_FOLDER);
                    pathnames = readerPath.list();

                    String files = "";
                    for (String pathname : pathnames) {
                        files += pathname + "\t";
                    }
                    server.out.flush();
                    server.out.println(files);
                    System.out.println(files);
                    break;
                }
                case "STOP":{
                    server.stop();
                    System.exit(1);
                }
            }
        }
    }
}
