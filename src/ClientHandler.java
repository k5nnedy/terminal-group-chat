import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.io.*;

public class ClientHandler implements Runnable {

    // static arraylist for every instance of this class to keep track of each client
    // needed in order to use for loop to send message to each client
    private static ArrayList<ClientHandler> clientHandlerList = new ArrayList<>();
	private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    

    public ClientHandler(Socket socket) {
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // reader
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // sender
            this.username = bufferedReader.readLine(); // reads from stream until enter key is pressed. meaning it reads one line
            clientHandlerList.add(this);
            broadcastMessage("SERVER "+ username + " has entered the chat.");

        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }
    
    @Override
	public void run() {

	}

}
