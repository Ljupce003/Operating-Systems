public class Main {
    public static void main(String[] args) {
        String envName=System.getenv("name");
        if(envName==null){
            System.out.println("Problemm");
        }
        else{
            System.out.println("Hello "+envName);
        }
    }
}
