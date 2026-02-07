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
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // sender
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // reader
            this.username = bufferedReader.readLine(); // reads from stream until username is read
            clientHandlerList.add(this);
            broadcastMessage("SERVER: "+ username + " has entered the chat.");

        } catch (IOException e) {
            // cloeses all of these when an exception happens
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    // everything on this method is ran on a seperate thread
    // on one thread we wait for messages, on the other one we working w/ the rest of the program
    // this prevents the program from being stuck on only waiting for messages from clients
    @Override
	public void run() {
        String messageFromClient;

        // while connected to a client, listen for messages
        while (socket.isConnected()) {
            try {
                // ran on a seperate thread so program doesn't stop here (blocking)
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);

            } catch (IOException e) {
                closeAll(socket, bufferedReader, bufferedWriter);
                // client disconnects, we break out of the while loop
                break;
            }
        }

	}

    public void broadcastMessage(String message) {
        for(ClientHandler clientHandler : clientHandlerList) {
            try {
                // sends message to everyone else but client
                if (!clientHandler.username.equals(username)) {
                    clientHandler.bufferedWriter.write(message);
                    clientHandler.bufferedWriter.newLine();
                    // flushes buffer b/c messages most likely won't fill buffer everytime
                    clientHandler.bufferedWriter.flush();

                }
            } catch (IOException e) {
                closeAll(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClientHandler() {
        clientHandlerList.remove(this);
        broadcastMessage("SERVER: " + username + " left");
    }

    //seperate method because of repeated if statements and nested try-catch's
    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        // buffers close the rest of the streams because they are the outermost wrapper
        // sockets close their i/o streams
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
