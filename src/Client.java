import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private String username;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    // takes socket for communication w/ server or clHandler
    // takes username to represent the client
    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // sender
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // reader
            this.username = username;
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }
    //sends message to clHandler
    public void sendMessage() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            //allows cl to continue to send message after connection is established to the server
            Scanner scanner = new Scanner(System.in);

            while(socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(username +": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    //new thread used for listening for messages from other users
    // each client has a seperate thread waiting for a message and once received, it is printed out on their console
    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromGroupChat;

                while (socket.isConnected()) {
                    try{
                    messageFromGroupChat = bufferedReader.readLine();
                    System.out.println(messageFromGroupChat);
                    } catch (IOException e) {
                        closeAll(socket, bufferedReader, bufferedWriter);
                    }

                }
            } 
        }).start();
    }

    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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

    public static void main(String[] args) throws IOException{

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Username to connect to the Group Chat: ");
        String clientusername = scanner.nextLine();
        Socket socket = new Socket("localhost",1234);
        Client client = new Client(socket, clientusername);

        // both are "blocking" methods since they are running until user disconnects
        // multithreaded to run concurrently
        client.listenForMessage();
        client.sendMessage();
    }
    
}
