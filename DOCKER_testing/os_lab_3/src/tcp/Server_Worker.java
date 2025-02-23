package tcp;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Semaphore;


public class Server_Worker extends Thread{
    private Socket serversocket;

    private String Counter_file_path;

    private static Semaphore mutex=new Semaphore(1);

    public Server_Worker(Socket serversocket,String counter_file_path) {
        this.serversocket = serversocket;
        this.Counter_file_path=counter_file_path;
    }

    @Override
    public void run() {

        boolean logged=false;
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            reader = new BufferedReader(new InputStreamReader(serversocket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(serversocket.getOutputStream()));

            while (true){


                Integer count=null;

                String message=reader.readLine();
                if(message!=null && !message.isEmpty()){
                    count=updateLogCount();
                }
                if(logged){
                    if(message.equals("login")){writer.write("Already logged in\n");}
                    else if(message.equals("logout")){
                        writer.write("Logged out\n");
                        break;
                    }
                    else if(message.equals("getcounter")){
                        writer.write(count+"\n");
                    }
                    else writer.write("echo: "+message+"\n");
                }
                else {
                    if(message.equals("login")){
                        logged=true;
                        writer.write("Logged in\n");
                    }
                    else writer.write("You aren't logged in\n");
                }

                writer.flush();

            }


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


        finally {
            try {
                if(writer!=null){
                    writer.flush();
                    writer.close();
                }
                if(reader!=null)reader.close();

                this.serversocket.close();
                System.out.println("Server connection is now closed on socket "+this.serversocket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private int updateLogCount() throws IOException, InterruptedException {
        RandomAccessFile raf;
        try {
            raf=new RandomAccessFile(Counter_file_path,"rw");
        } catch (FileNotFoundException e) {
            System.err.println("Counter file not found");
            return -1;
        }

        Integer counter = null;

        mutex.acquire();
        try {
            counter = raf.readInt();
        } catch (IOException e) {
            System.err.println("Counter file is null.Creating new file");
        }
        if (counter == null) {
            counter = 0;
        }
        counter++;
        raf.seek(0);
        raf.writeInt(counter);
        raf.close();
        mutex.release();

        return counter;
    }

}
