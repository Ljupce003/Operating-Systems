package shared;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerLogger extends Thread{
    private int logger_port;
    private String csvfile_path;
    private String counterfile_path;

    public ServerLogger(int logger_port, String csvfile_path, String counterfile_path) {
        this.logger_port = logger_port;
        this.csvfile_path = csvfile_path;
        this.counterfile_path = counterfile_path;
    }

    @Override
    public void run() {
        System.out.println("Server logger starting");
        ServerSocket serverSocket=null;
        try {
            serverSocket=new ServerSocket(logger_port);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }

        System.out.println("Server logger started");

        Socket socket=null;
        while (true){
            try {
                socket=serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Port is unavailable for the logger");
            }
            System.out.println("New connection to logger");
            new LoggerWorker(socket,new File(csvfile_path),new File(counterfile_path)).start();
        }




    }


    public static void main(String[] args) {
        String loggerport=System.getenv("LOGGER_PORT");
        if(loggerport==null || loggerport.isEmpty()){
            throw new RuntimeException("The logger port is missing");
        }
        String csvfile_path=System.getenv("CSV_FILE_PATH");
        String counterfile_path=System.getenv("COUNTER_FILE_PATH");

        ServerLogger logger=new ServerLogger(Integer.parseInt(loggerport),csvfile_path,counterfile_path);
        logger.start();


    }
}
