package bank;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BankServer extends Thread{

    private int port;

    public BankServer(int port) {
        this.port = port;
    }


    @Override
    public void run() {

        System.out.println("Server starting...");
        ServerSocket serverSocket=null;

        try {
            serverSocket=new ServerSocket(this.port);
        } catch (IOException e) {
            System.err.println("This port is couldn't be assigned to this server");
            return;
        }

        System.out.println("Server started and waiting for connections...");

        while (true){

            Socket socket=null;

            try {
                socket=serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Connection is broken");
                continue;
            }

            System.out.println("Server Acquired new connection on "+socket);


            new ServerWorker(socket).start();
        }


    }

    public static void main(String[] args) {

        int port=Integer.parseInt(System.getenv("SERVER_PORT"));

        BankServer server=new BankServer(port);

        server.start();


    }
}


//getenv("BANKER_PORT"));
//getenv("BANKER_NAME");
//getenv("SERVER_PORT"));