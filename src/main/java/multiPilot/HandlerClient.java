package multiPilot;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HandlerClient extends Thread{

    private static ServerSocket serverSocket;
    private static Socket selectedClient;

    private BufferedReader in;
    private PrintWriter out;

    private InputStream is;
    private OutputStream os;

    private static Map<String, Socket> listClient = new HashMap<>();

    HandlerClient(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        run();
    }

    public void run(){
        while(true){
            try{
                Socket clientSocket = serverSocket.accept();

                if(!listClient.containsValue(clientSocket)){
                    System.out.println("New client\t" + clientSocket);
                    listClient.put(String.valueOf(listClient.size()+1),clientSocket);
                }else{
                    // Send a message welcome back to the client!
                    System.out.println("Welcome back! :3");
                }

                //selectClient("1", "Petit message pour toi bichon");
                //selectClient("2", "C'est de toute beauteyyy");
            }
            catch (IOException e){
                System.out.println("A lil error happened in the handler client.");
                e.printStackTrace();
            }
        }
    }

    public Map<String, Socket> getListClient(){
        return listClient;
    }

    public void selectClient(String idClient, String message) throws IOException {
        selectedClient = listClient.getOrDefault(idClient, selectedClient);

        is = selectedClient.getInputStream();
        os = selectedClient.getOutputStream();

        in = new BufferedReader(new InputStreamReader(is));
        out = new PrintWriter(os, true);

        out.println(message);
    }
}
