package multiPilot;

import java.io.*;
import java.net.Socket;

public class FTPClient {
    private Socket clientSocket;

    private BufferedReader in;
    private PrintWriter out;

    private InputStream is;
    private OutputStream os;

    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public void startConnection(String name, int port) throws IOException {
        clientSocket = new Socket(name, port);

        is = clientSocket.getInputStream();
        os = clientSocket.getOutputStream();

        out = new PrintWriter(os, true);
        in = new BufferedReader(new InputStreamReader(is));

        dataInputStream = new DataInputStream(is);
        dataOutputStream = new DataOutputStream(os);
    }

    public static void main(String[] args) throws IOException {
        FTPClient client = new FTPClient();
        client.startConnection("127.0.0.1", 6846);

        while(true){
            System.out.println(client.in.readLine());
        }
    }
}
