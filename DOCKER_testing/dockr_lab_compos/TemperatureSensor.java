import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class TemperatureSensor {


    public static void main(String[] args) throws InterruptedException, IOException {

        File temperatureFile = new File("temperature/temperature.txt");

        if(temperatureFile.createNewFile()){
            LocalTime time=LocalTime.now();
            System.out.println("Temperature file created at "+time.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }

        BufferedWriter writer=null;

        writer = new BufferedWriter(new FileWriter(temperatureFile));
        writer.write("");
        writer.flush();
        writer.close();


        while (true){
            writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temperatureFile,true)));
            Random r = new Random();
            for (int i = 0; i < 5; i++) {
                writer.append(String.valueOf(r.nextInt(5,50)));
                writer.append(" ");
            }
            writer.append("\n");

            LocalTime time=LocalTime.now();
            System.out.println("Reading temperatures cycle at "+time.format(DateTimeFormatter.ofPattern("HH:mm:ss")));

            writer.flush();
            writer.close();



            Thread.sleep(30000);
        }






    }
}
