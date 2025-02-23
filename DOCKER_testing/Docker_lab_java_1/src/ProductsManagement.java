//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.io.*;

public class ProductsManagement {

    public static void main(String[] args) throws InterruptedException {
        //String path = "/var/www/html/products.txt";
        String path =System.getenv("FILE_PATH");
        BufferedReader reader = null;
        String line = null;

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            while ((line = reader.readLine())!=null) {
                String[] cells = line.split(";");
                if (cells.length != 3) {
                    throw new RuntimeException("Invalid row!");
                }
                System.out.printf("Product Name: %s\n", cells[0]);
                System.out.printf("Product Price: %s\n", cells[1]);
                System.out.printf("Product Quantity: %s\n", cells[2]);
                System.out.println("\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        while (true){
//            Thread.sleep(4000);
//        }
    }
}