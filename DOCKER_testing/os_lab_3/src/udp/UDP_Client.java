package udp;

import java.io.IOException;
import java.net.*;
import java.util.Random;

public class UDP_Client extends Thread{

    private String servername;
    private int serverport;
    private byte[] buffer;


    public UDP_Client(String servername, int serverport) {
        this.servername = servername;
        this.serverport = serverport;
        this.buffer=new byte[256];

    }

    boolean loggedin=false;

    @Override
    public void run() {

        DatagramSocket datagram_s=null;
        try {
            datagram_s=new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }



        while (true){
            String message=randMess();
            try {
                UDP_Server.sendMessage(message,datagram_s,InetAddress.getByName(servername),serverport);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }

            String response= getResponse(datagram_s);

            if(response.equals("logged out")){
                System.out.println("UDP Server: "+response);
                return;
            }else {
                System.out.println("UDP Server: "+response);
            }
        }
    }

    public static String getResponse(DatagramSocket datagramSocket,String who) {
        byte [] buffer=new byte[256];
        DatagramPacket packet=new DatagramPacket(buffer, buffer.length);

        try {
            datagramSocket.receive(packet);
        } catch (IOException e) {
            System.err.println("Failed to send message \n"+e.getMessage());
            return "";
        }
        String response=new String(packet.getData(),0,packet.getLength());
        System.out.println("Packet Received to "+who+"...");
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("Message thru UDP received by "+packet.getAddress().getHostAddress()+" on port "+packet.getPort());
        System.out.println("Message is: \n"+response);
        System.out.println("--------------------------------------------------------------------------\n");
        return response;
    }

    public static String getResponse(DatagramSocket datagramSocket) {
        byte [] buffer=new byte[256];
        DatagramPacket packet=new DatagramPacket(buffer, buffer.length);

        try {
            datagramSocket.receive(packet);
        } catch (IOException e) {
            System.err.println("Failed to send message \n"+e.getMessage());
            return "";
        }
        String response=new String(packet.getData(),0,packet.getLength());


        return response;
    }


    private String randMess(){
        String log="login";
        String[] arr={"pero","hello","hii how are you","this is client","what is going on","I am captain america","petre Shilegov,","getcounter","logout"};

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
        String servername=System.getenv("UDP_SERVER_NAME");

        int port=Integer.parseInt(System.getenv("UDP_SERVER_PORT"));


        UDP_Client client=new UDP_Client(servername,port);
        client.start();
    }
}
