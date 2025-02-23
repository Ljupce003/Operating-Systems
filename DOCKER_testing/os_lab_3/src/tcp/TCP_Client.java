package tcp;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import java.util.Random;


public class TCP_Client extends Thread{
    private String servername;
    private int server_port;

    public TCP_Client(String servername, int server_port) {
        this.servername = servername;
        this.server_port = server_port;
    }

    private boolean loggedin=false;


    @Override
    public void run() {
        Socket socket=null;


        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            socket=new Socket(InetAddress.getByName(servername),server_port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));



            while (true){



                writer.write(randMess()+"\n");
                writer.flush();

                String line=reader.readLine();
                System.out.println("Server: "+line);
                if(line.contains("Logged out")){
                    break;
                }



            }






        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        finally {

            try {
                if(reader!=null)reader.close();
                if(writer!=null){
                    writer.flush();
                    writer.close();
                }
                if(socket!=null)socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private String randMess(){
        String log="login";
        String[] arr={"pero","hello","hii how are you","this is client","what is oging on","I am captain america","petre shilegov,","getcounter","logout"};


        Random r=new Random();

        int num=r.nextInt(1,3);
        if(num==1) {
            if (!loggedin) {
                loggedin = true;
                return log;
            }
            else {
                if(r.nextInt(0,3)==1)return log;
                else return arr[r.nextInt(0,9)];
            }
        }
        else return arr[r.nextInt(0,9)];

    }

    public static void main(String[] args) {

        int port=Integer.parseInt(System.getenv("SERVER_PORT"));
        String servername=System.getenv("SERVER_NAME");



        TCP_Client client=new TCP_Client(servername,port);
        client.start();


    }
}
