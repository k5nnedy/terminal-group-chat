import java.net.Socket;
import java.net.ServerSocket;
import java.io.*;

// Responsible for listening to new clients who wish to connect
// Creates new thread to handle these clients
public class Server {
    private ServerSocket serverSocket;
    
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("New client has joined!");

                // Communicates with each client concurrently
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }
        catch (IOException e) {


        }
    }

    // If error occurs, just shut down the serverSocket
    public void closeServerSocket() {
        try{
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException{
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            Server server = new Server(serverSocket);
            server.startServer();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
