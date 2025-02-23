package tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCP_Server extends Thread{
    private int port;
    private String counter_file_path;

    public TCP_Server(int port,String Counter_file_path) {
        this.port = port;
        this.counter_file_path=Counter_file_path;
    }

    @Override
    public void run() {
        System.out.println("Server starting...");
        ServerSocket serverSocket=null;
        try {
            serverSocket=new ServerSocket(this.port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Server started...");

        Socket socket=null;
        while (true){

            try {
                socket=serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Server accepted connection on socket "+socket+"...");

            new Server_Worker(socket,this.counter_file_path).start();
        }


    }

    public static void main(String[] args) {

        int port=Integer.parseInt(System.getenv("SERVER_PORT"));
        String Counter_path=System.getenv("COUNTER_FILE");

        TCP_Server server=new TCP_Server(port,Counter_path);
        server.start();
    }
}


