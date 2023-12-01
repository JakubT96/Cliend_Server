package JakubT96.Client_Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class Server {

    private ServerSocket serverSocket;
    private ArrayList<ClientHandler> clients;

    public static void main(String[] args) {
        Server server = new Server();
    }
/*
Prvně spustit server, poté klienta.
Z klienta odeslat zprávu --> měla by se  zobrazit na serveru.
Server by měl zprávu poslat z5.
 */
    public Server() {
        try {
            this.serverSocket = new ServerSocket(8080); // vytvoření a spuštění serveru na http://localhost:8080
            System.out.print("Spuštění serveru proběhlo úspěšně.\nČekám na připojení klienta...\n");
            this.clients = new ArrayList<>();

            clients();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clients() {
        Thread acceptThread = new Thread(new Runnable() {
            public void run() {
                while (true) {  // cyklus pro vypisování zpráv od klienta
                    try {
                        Socket clientSocket = serverSocket.accept();
                        PrintWriter writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
                        ClientHandler clientHandler = new ClientHandler(clientSocket, writer);
                        synchronized (clients) {
                            clients.add(clientHandler);
                        }
                        System.out.println("Klient" + clientHandler.getAddress() + " se připojil.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        acceptThread.start();

        while (true) {
            synchronized (clients) {
                Iterator<ClientHandler> iterator = clients.iterator();
                while (iterator.hasNext()) {
                    ClientHandler clientHandler = iterator.next();
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(clientHandler.getSocket().getInputStream()));
                        if (reader.ready()) {
                            String message = reader.readLine();
                            System.out.println("Přijata zpráva od klienta " + clientHandler.getAddress() + " : " + message);
                            broadcastMessage(message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        iterator.remove(); // Bezpečně odstraní klienta z iterátoru
                    }
                }
            }
        }
    }

    private void broadcastMessage(String message) {
        synchronized (clients) {
            Iterator<ClientHandler> iterator = clients.iterator();
            while (iterator.hasNext()) {
                ClientHandler clientHandler = iterator.next();
                clientHandler.getWriter().println(message);
                clientHandler.getWriter().flush();
            }
        }
    }

    private static class ClientHandler {
        private Socket socket;
        private PrintWriter writer;

        public ClientHandler(Socket socket, PrintWriter writer) {
            this.socket = socket;
            this.writer = writer;
        }

        public Socket getSocket() {
            return this.socket;
        }

        public PrintWriter getWriter() {
            return this.writer;
        }

        public String getAddress(){
            return this.socket.getInetAddress().toString();
        }
    }
}
