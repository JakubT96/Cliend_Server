package JakubT96.Client_Server;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket clientSocket;
    private BufferedReader serverReader;

    public static void main(String[] args) {
        Client client = new Client();
    }
    /*
    Prvně spustit server, poté klienta.
    Z klienta odeslat zprávu --> měla by se  zobrazit na serveru.
    Server by měl zprávu poslat z5.
     */
    public Client() {
        try {
            this.clientSocket = new Socket("localhost", 8080); //spuštění na http://localhost:8080
            System.out.println("Spuštění klienta proběhlo úspěšně.");

            this.serverReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            Thread receiveThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        while (true) {
                            String message = serverReader.readLine();
                            System.out.println("Zpráva od serveru: " + message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            receiveThread.start();

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter( this.clientSocket.getOutputStream())); //vytvoření BufferedWriter
            Scanner in = new Scanner(System.in);
            while (true) {  // nekonečný cyklus pro odesílání zpráv dokud neukončím program
                String message = in.nextLine();
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
                out.write(message + "\r\n");
                out.flush();
                System.out.println("Zpráva \"" + message + "\" byla odeslána.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
