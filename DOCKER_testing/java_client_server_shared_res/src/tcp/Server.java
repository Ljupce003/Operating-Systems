package tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//go stavame serverot da raboti vo thread zatoa sto da ne go blokirame main metodata ako vo nea ima i drugi aktivnosti osven serverot i istiot da si rabote independent
public class Server extends Thread{
    //vo server samo cuvame na koja porta raboti
    private int port;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        System.out.println("Server starting");

        ServerSocket serverSocket=null;

        try {
            serverSocket=new ServerSocket(this.port);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println("Port unavailable");
            return;
        }

        System.out.println("Server started and awaiting connections...");
        System.out.println("Server address is "+serverSocket.getInetAddress().getHostAddress());


        while (true){
            Socket workersocket=null;
            try {
                workersocket=serverSocket.accept();

                System.out.println("Connected to a new Connection of port "+this.port);
            } catch (IOException e) {
                e.printStackTrace();
            }

            new ServerWorker(workersocket).start();
        }

    }


    public static void main(String[] args) {
        int port=Integer.parseInt(System.getenv("SERVER_PORT"));
        Server server=new Server(port);
        server.start();
    }
}
