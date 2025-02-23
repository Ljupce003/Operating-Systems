package tcp;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//go stavame workerot threaded za da mozeme da se izvrsuva pararelno poveke workera i klientite da ne cekaat eden po drug za red
public class ServerWorker extends Thread{
    private Socket workersocket;

    public ServerWorker(Socket workersocket) {
        this.workersocket = workersocket;
    }

    @Override
    public void run() {
        //Buffered Reader/Writer za da moze da cita streamovi od socketite i plus isto moze da raboti i so csv file
        BufferedReader reader=null;
        BufferedWriter writer=null;

        try {
            reader=new BufferedReader(new InputStreamReader(workersocket.getInputStream()));
            writer=new BufferedWriter(new OutputStreamWriter(workersocket.getOutputStream()));

            //reader requestot go ima
            WebRequest request=WebRequest.of(reader);


            //writerot praka response

            writer.write("HTTP/1.1 OK 200\n");
            writer.write("Content-Type: text/html\n");
            writer.write("\n");

            writer.write("Hello Ljupce .<br>\n");
            writer.write("You contacted me with a "+request.command+" command and from "+request.headers.get("User-Agent")+"<br>");

        } catch (IOException e) {
            e.printStackTrace();
        }

        finally {
            //tuka finalno proveruvame i zatvarame reader/writer i go osloboduvame socketot
                try {
                    //if(writer!=null) {
                        writer.flush();
                        writer.close();
                    //}
                    //if(reader!=null){
                        reader.close();
                    //}
                    this.workersocket.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        static class WebRequest{
        String url;
        String version;
        String command;
        Map<String,String> headers;


        public WebRequest(String url, String version, String command, Map<String, String> headers) {
            this.url = url;
            this.version = version;
            this.command = command;
            this.headers = headers;
        }

        static WebRequest of(BufferedReader reader) throws IOException {
            List<String> lines=new ArrayList<>();
            String lin= reader.readLine();
            while (!lin.isEmpty()){
                lines.add(lin);
                lin=reader.readLine();
            }

            String[] parts=lines.get(0).split("\\s+");
            String command=parts[0];
            String url=parts[1];
            String version=parts[2];
            Map<String,String> headers=new HashMap<>();
            for (int i = 1; i < lines.size(); i++) {
                String[] header_parts=lines.get(i).split(":");
                String header=header_parts[0];
                String content=header_parts[1];
                headers.put(header,content);
            }

            return new WebRequest(url,version,command,headers);

        }
    }
}
