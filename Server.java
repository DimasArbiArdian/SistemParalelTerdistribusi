import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server {
    private static final int PORT = 12345;
    private static ArrayList<PrintWriter> clientWriters = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Server telah dimulai.");
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter writer;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);
                clientWriters.add(writer);

                System.out.println("Client " + socket.getInetAddress() + " terhubung.");

                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println("Pesan dari " + socket.getInetAddress() + ": " + message);
                    broadcastMessage(socket.getInetAddress() + ": " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                clientWriters.remove(writer);
                System.out.println("Client " + socket.getInetAddress() + " terputus.");
            }
        }
    }

    private static void broadcastMessage(String message) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String timestamp = dateFormat.format(new Date());
        message = timestamp + " " + message;
        for (PrintWriter clientWriter : clientWriters) {
            clientWriter.println(message);
        }
    }
}
