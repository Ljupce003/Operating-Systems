import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;

public class Client extends Thread{
    private String servername;
    private int serverport;

    public Client(String servername, int serverport) {
        this.servername = servername;
        this.serverport = serverport;
    }

    @Override
    public void run() {

        //Scanner sc=new Scanner(System.in);

        Socket socket=null;
        BufferedReader reader=null;
        BufferedWriter writer=null;
        try {
            socket=new Socket(InetAddress.getByName(servername),serverport);
            reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String req=randomMess();

            writer.write(req+"\n");
            writer.flush();

            String response_from_server=reader.readLine();
            System.out.println(response_from_server);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        finally {
            deconstructEverything(reader,writer,socket);
        }



    }

    private static void deconstructEverything(BufferedReader reader,BufferedWriter writer,Socket socket){
        if(reader!=null) {
            try {
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if(writer!=null){
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        if(socket!=null) {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String randomMess() {
        Random r=new Random();

        String mess;
        int n=r.nextInt(0,15);
        if(n<9){
            //if(r.nextBoolean())
                mess="Petar withdraw 500";              //+r.nextDouble(150,1000);
            //else mess="Petar balance";
        }
        else mess="Petar deposit 500";              //+r.nextDouble(0,1000);

        return mess;
    }

    public static void main(String[] args) {
        int serverPort=Integer.parseInt(System.getenv("SERVER_PORT"));
        String serverName=System.getenv("SERVER_NAME");
        Client client=new Client(serverName,serverPort);
        client.start();
    }
}

//getenv("SERVER_PORT"));
//getenv("SERVER_NAME");