package bank;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ServerWorker extends Thread{

    private Socket serversocket;

    public ServerWorker(Socket serversocket) {
        this.serversocket = serversocket;
    }

    @Override
    public void run() {

        BufferedReader reader=null;
        BufferedWriter writer=null;
        try {
            reader=new BufferedReader(new InputStreamReader(serversocket.getInputStream()));
            writer=new BufferedWriter(new OutputStreamWriter(serversocket.getOutputStream()));

            double code=proccessRequest(reader);

            String response;

            if(code<0){
                switch ((int) code) {
                    case -1 : response="successful deposit";
                        break;
                    case -2 : response="successful withdraw";
                        break;
                    case -10 : response="unsuccessful deposit";
                        break;
                    case -11 : response="unsuccessful withdraw - no money";
                        break;
                    case -12 : response="unsuccessful withdraw - unknown username";
                        break;
                    case -15 : response="wrong input value";
                        break;
                    default: response="unavailable transaction due to banker connection error";
                        break;
                }
            }
            else {
                response="Your balance is "+code;
            }



            writer.write(response+"\n");
            writer.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        finally {
            deconstructEverything(reader,writer,serversocket);
        }

    }






    public static void deconstructEverything(BufferedReader reader, BufferedWriter writer, Socket socket){
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

    private double proccessRequest(BufferedReader reader)  {
        int bankerport=Integer.parseInt(System.getenv("BANKER_PORT"));
        String bankername=System.getenv("BANKER_NAME");
        Double code=-16.0;
        Socket socket=null;
        BufferedWriter writer=null;
        BufferedReader Banker_reader=null;
        try {
            socket=new Socket(InetAddress.getByName(bankername),bankerport);
            writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            Banker_reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message=reader.readLine();
            writer.write(message+"\n");
            writer.flush();

            String response=Banker_reader.readLine();
            code=Double.parseDouble(response);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Uncomplete operation to send data to banker");
        }
        finally {
            if(Banker_reader!=null) {
                try {
                    Banker_reader.close();
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


        return code;
    }
}
