package bank;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Banker extends Thread{

    private int port;
    String filepath;
    private Map<String,Double> ledger;


    public Banker(int port,String filepath) throws IOException {
        this.port = port;
        File lodgerfile=new File(filepath);
        this.filepath=filepath;
        if(lodgerfile.createNewFile()) System.err.println("Ledger file not located.Creating at:"+filepath);
        this.ledger=new HashMap<>();

        BufferedReader fileReader=null;
        try {
            fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(lodgerfile)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
        String line=fileReader.readLine();
        while (line!=null && !line.isEmpty()){
            String []parts=line.split("=");
            this.ledger.put(parts[0],Double.parseDouble(parts[1]));
            line=fileReader.readLine();
        }
        fileReader.close();


    }



    @Override
    public void run() {




        //System.out.println("Banker starting");
        ServerSocket serverSocket=null;

        try {
            serverSocket=new ServerSocket(this.port);
        } catch (IOException e) {
            System.err.println("This port is couldn't be assigned to this banker");
            return;
        }

        System.out.println("Banker started and waiting for commands...");


        while (true){
            Socket socket=null;

            try {
                socket=serverSocket.accept();
                //System.out.println("Banker acquired new connection ");


                new BankerHelper(socket,ledger).start();


            } catch (IOException e) {
                throw new RuntimeException(e);
            }



            finally {
                File lodgerfile=new File(this.filepath);
                BufferedWriter file_saver=null;
                try {
                    file_saver=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(lodgerfile)));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                try {
                    for (Map.Entry<String, Double> entry : this.ledger.entrySet()) {
                        file_saver.write(entry.getKey()+"="+entry.getValue()+"\n");
                        file_saver.flush();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }


    }




    public static void main(String[] args) throws IOException {
        int port=Integer.parseInt(System.getenv("BANKER_PORT"));

        String file_path=System.getenv("LEDGER_FILEPATH");

        Banker banker=new Banker(port,file_path);
        banker.start();
    }
}

//.getenv("BANKER_PORT"));
//getenv("LEDGER_FILEPATH");