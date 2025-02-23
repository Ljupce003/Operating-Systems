package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDP_Server extends Thread {


    private byte[] buffer;
    private int port;

    public UDP_Server(int port) {

        this.buffer=new byte[256];
        this.port=port;
    }

    @Override
    public void run() {
        System.out.println("UDP Server starting");

        DatagramSocket datagram_s= null;
        try {
            datagram_s = new DatagramSocket(this.port);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        DatagramPacket datagram_p=null;

        boolean canType=false;

        System.out.println("UDP Server started...\n");

        while (true) {
            datagram_p=new DatagramPacket(buffer, buffer.length);
            try {
                datagram_s.receive(datagram_p); //method blocks until a packet is received
            } catch (IOException e) {
                e.printStackTrace();
            }

            String message=getPacket_Message(datagram_p);

            System.out.println(message);
            if(message.equals("login")){
                if(canType){
                    sendMessage("Already logged in",datagram_s,datagram_p);
                }
                else {
                    canType=true;
                    sendMessage("logged in",datagram_s,datagram_p);
                }
            }else if(message.equals("logout")){
                if(canType){
                sendMessage("logged out",datagram_s,datagram_p);
                    System.out.println("Connection closed\n");
                    canType=false;
                //break;
                }
                else {
                    sendMessage("You aren't logged in",datagram_s,datagram_p);}
            }else {
                if(canType){
                    sendMessage("echo- "+message,datagram_s,datagram_p);
                }else {
                    sendMessage("You aren't logged in",datagram_s,datagram_p);
                }
            }


        }


    }

    public static String printMess(DatagramPacket packet,String who){
        System.out.println("Packet Received to "+who+"..");
        System.out.println("--------------------------------------------------------------------------");
        String message = new String(packet.getData(),0,packet.getLength());
        System.out.println("Message received by " + packet.getAddress().getHostAddress() + " on port " + packet.getPort());
        System.out.println("Message is: \n" + message);
        System.out.println("--------------------------------------------------------------------------\n");
        return message;
    }

    public static String getPacket_Message(DatagramPacket packet){
        return new String(packet.getData(),0,packet.getLength());
    }

    public static void sendMessage(String response, DatagramSocket socket, InetAddress address , int port){
        byte [] buffer=response.getBytes();
        DatagramPacket packet=new DatagramPacket(buffer, buffer.length,address,port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            System.err.println("Failed to send message \n"+e.getMessage());
        }

    }

    public static void sendMessage(String response, DatagramSocket socket, DatagramPacket old_packet){
        byte [] buffer=response.getBytes();
        DatagramPacket packet=new DatagramPacket(buffer, buffer.length,old_packet.getAddress(),old_packet.getPort());
        try {
            socket.send(packet);
        } catch (IOException e) {
            System.err.println("Failed to send message \n"+e.getMessage());
        }
    }

    public static void main(String[] args)  {

        int port=Integer.parseInt(System.getenv("UDP_SERVER_PORT"));

        UDP_Server server=new UDP_Server(port);
        server.start();
    }
}
