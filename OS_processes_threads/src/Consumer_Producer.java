import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Consumer_Producer {
    static Semaphore accessBuffer;
    static Semaphore lock;
    static Semaphore Acheck;
    static int numchecks=0;

    public static void init(){
        accessBuffer=new Semaphore(1);
        lock=new Semaphore(1);
        Acheck=new Semaphore(10);
    }

    class Producer extends Thread{
        int buffer;
        public Producer(int buffer) {
            this.buffer = buffer;

        }
        public Producer() {
            this.buffer = 0;

        }
        public void execute() throws InterruptedException {
            accessBuffer.acquire();
            buffer++;
            accessBuffer.release();
        }
    }

    class Consumer extends Thread{

        public Consumer() {
        }

        public void execute(int buffer) throws InterruptedException {

        }


    }


}

