import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TemperatureMonitor {

    public static void main(String[] args) throws IOException, InterruptedException {

        Thread.sleep(1000);
        File temperatureFIle = new File("temperature/temperature.txt");

        if (!temperatureFIle.exists()) {
            System.out.println("temperature file does not exist for Monitor to read");
            System.out.println("Creating temperature file from monitor and waiting");
            Thread.sleep(5000);

        }
        File avgtemperatureFIle = new File("avgtemperature/avgtemperature.txt");

        if (!avgtemperatureFIle.exists()) {
            System.out.println("Avgtemperature file created");
        }
        BufferedReader reader = null;
        BufferedWriter writer = null;

        BufferedWriter avgwriter = null;

        avgwriter = new BufferedWriter(new FileWriter(avgtemperatureFIle));
        avgwriter.write("");
        avgwriter.flush();
        avgwriter.close();

        int retries=3;
        while (true){
            reader=new BufferedReader(new InputStreamReader(new FileInputStream(temperatureFIle)));

            avgwriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(avgtemperatureFIle,true)));

            String curr_line;
            String reading_line=reader.readLine();
            if(reading_line==null){
                reader.close();
                avgwriter.flush();
                avgwriter.close();
                System.out.println("Line empty.Waiting for renetry");
                retries--;
                if(retries==0){return;}
                Thread.sleep(5000);
                continue;
            }
            curr_line=reader.readLine();
            ArrayList<String> lines=new ArrayList<>();
            while (curr_line!=null && !curr_line.isEmpty()){
                lines.add(curr_line);
                curr_line=reader.readLine();
            }
            reader.close();

            writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temperatureFIle)));

            for (String lin : lines) {
                writer.write(lin+"\n");
            }
            writer.flush();
            writer.close();

            int n=0;
            int sum=0;
            for (String s : reading_line.split("\\s+")) {
                sum+=Integer.parseInt(s);
                n++;
            }

            LocalTime time=LocalTime.now();
            System.out.println("Writing average temperature at "+time.format(DateTimeFormatter.ofPattern("HH:mm:ss")));

            float avg=(float)sum/n;

            avgwriter.append(reading_line);
            avgwriter.append(" ");
            avgwriter.append(String.valueOf(avg));
            avgwriter.append(" ");

            float low=Float.parseFloat(System.getenv("LOW_TEMPERATURE"));
            float medium=Float.parseFloat(System.getenv("MEDIUM_TEMPERATURE"));
            float high=Float.parseFloat(System.getenv("HIGH_TEMPERATURE"));

            if(avg>=low && avg<=medium)avgwriter.append("Low");
            else if(avg>medium && avg<=high)avgwriter.append("Medium");
            else if(avg>high)avgwriter.append("High");




            avgwriter.append("\n");
            avgwriter.flush();
            avgwriter.close();


            Thread.sleep(60000);

        }
    }
}
