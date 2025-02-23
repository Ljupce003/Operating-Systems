package tcp;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class Client extends Thread{
    private int serverport;
    private String servername;

    public Client(String servername,int serverport) {
        this.serverport = serverport;
        this.servername = servername;
    }

    public Client(int serversocket) {
        this.serverport = serversocket;
    }


    @Override
    public void run() {
        BufferedReader reader=null;
        BufferedWriter writer=null;
        Socket socket=null;
        try {
            socket=new Socket(InetAddress.getByName(this.servername),serverport);

            reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            Random r=new Random();
            //writer in the client sends the request to the server
            writer.write("GET /movie"+r.nextInt(1,150)+" HTTP/1.1\n");
            writer.write("Host: Ljupce_Klientot\n");
            writer.write("\n");
            writer.flush();


            //Thread.sleep(1000);

            //reader reads the response from the server
            ArrayList<String> lines=new ArrayList<>();
            String line=reader.readLine();
            while(line!=null && !line.isEmpty()){
                lines.add(line);
                line=reader.readLine();
            }

            for (String s : lines) {
                System.out.println("Client:"+s);
            }



        } catch (IOException e) {
            e.printStackTrace();
        //} catch (InterruptedException e) {
        //    throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
                reader.close();
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public static void main(String[] args) throws InterruptedException {
        String servername=System.getenv("SERVER_NAME");
        String server_port=System.getenv("SERVER_PORT");

        if(server_port==null){
            System.err.println("Environment variable for SERVER_PORT is wrong");
            return;
        }

        //Thread.sleep(10000);

        Client client=new Client(servername,Integer.parseInt(server_port));
        client.start();

        //Thread.sleep(2000);
    }
}
