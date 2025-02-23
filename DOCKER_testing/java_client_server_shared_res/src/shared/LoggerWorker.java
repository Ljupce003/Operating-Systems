package shared;

import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LoggerWorker extends Thread {
    private Socket loggersocket;
    private File csvFile;
    private File counterfile;
    //private static Lock lock=new ReentrantLock();

    public LoggerWorker(Socket loggersocket, File csvFile, File counterfile) {
        this.loggersocket = loggersocket;
        this.csvFile = csvFile;
        this.counterfile = counterfile;
    }

    @Override
    public void run() {
        BufferedReader socket_reader=null;
        BufferedWriter csv_writer=null;
        RandomAccessFile raf=null;

        try {
            socket_reader=new BufferedReader(new InputStreamReader(this.loggersocket.getInputStream()));
            csv_writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile,true)));
            raf=new RandomAccessFile(counterfile,"rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            //lock.lock();
            incrementCounter(raf);
            //lock.unlock();

            String line=socket_reader.readLine();
            while(line!=null && !line.isEmpty()){
                csv_writer.append(line+"\n");
                line=socket_reader.readLine();
            }


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        finally {
            try {
                csv_writer.flush();
                csv_writer.close();
                socket_reader.close();
                raf.close();
                this.loggersocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    //synchronise this

    private void incrementCounter(RandomAccessFile raf) throws IOException, InterruptedException {
        Integer currentcounter=null;
        try {
            currentcounter=raf.readInt();
        } catch (IOException e) {
            System.err.println("Counterfile is null");
        }
        if(currentcounter==null){
            currentcounter=0;
        }

        currentcounter++;
        raf.seek(0);
        Random r=new Random();
        if(r.nextBoolean()) Thread.sleep(350);
        raf.writeInt(currentcounter);
        System.out.printf("Total Number of Clients until now: %d\n",currentcounter);

    }
}
