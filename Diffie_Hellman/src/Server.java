import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket server;
    // lista klientów
    private final Map<Socket, Long> clientsList = new HashMap<Socket, Long>();


    private Server(int port) {
        try {
            server = new ServerSocket(port);
            System.out.println("Server socket created on port " + port);
        } catch (IOException e) {
            System.out.println("Could not listen on port " + port);
            e.printStackTrace();
        }

        listen();
    }

    private void listen() {
        while (true) {
            try {
                Socket client = server.accept();
                System.out.println("Client connected");
                new ServerThread(client, clientsList).start();
            } catch (IOException ex) {
                System.out.println("I/O error" + ex);
            }
        }
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        new Server(port);
    }

    private static class ServerThread extends Thread {
        private final Socket socket;
        private final Map<Socket, Long> clientsList;
        private final long p = 67; // liczba pierwsza
        private final long g = 7; // modulo n
        private final long b = 9; // sekret serwera
        private long S; // sekret wyliczony na podstawie A
        private long B; // wartosc wysylana do clienta
        private long A; // wartosc odbierana od klienta

        ServerThread(Socket s, Map<Socket, Long> clientsList) {
            this.socket = s;
            this.clientsList = clientsList;
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                boolean canSendB = false;
                boolean canCalculateS = false;

                while (true) {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                    if (canSendB) {
                        B = (long) (Math.pow(g, b)) % p;
                        System.out.println("Server B: " + B);
                        JSONObject key = new JSONObject();
                        key.put("B", B);
                        out.println(key);
                        canSendB = false; // wartosc B wysylana tylko raz
                    }

                    if (canCalculateS) {
                        S = (long) (Math.pow(A, b)) % p;
                        clientsList.put(socket, S);
                        System.out.println("Server S: " + S);
                        canCalculateS = false; // tylko raz wysylamy S;
                    }

                    String input = in.readLine();
                    if (input == null) {
                        break;
                    }

                    System.out.println("from client: " + input); // odczyt danych od klienta
                    JSONObject object = new JSONObject(input);

                    // wysylanie kluczy do klienta
                    if (object.has("request") && object.getString("request").equals("keys")) {
                        JSONObject keys = new JSONObject();
                        keys.put("p", p);
                        keys.put("g", g);
                        out.println(keys);
                        canSendB = true; // server teraz moze wyslac B do klienta

                        // odbior A od klienta
                    } else if (object.has("A")) {
                        A = object.getLong("A");
                        System.out.println("A from client: " + A);
                        canCalculateS = true; // mozna obliczyc S

                        // rodzaj szyfrowania
                    } else if (object.has("encryption")) {
                        // cezar, xor or none
                        // TO DO

                    } else if (object.has("msg")) {
                        // wysylamy wiadomosc do wszystkich klientow
                        for (Socket s : clientsList.keySet()) {
                            out = new PrintWriter(s.getOutputStream(), true);
                            out.println(input);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}