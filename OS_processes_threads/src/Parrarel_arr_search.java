import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class SharedArray {
    private int [] array;
    private int size;
    private int totalOccurences;
    private int maxPerThread;
    private int threadsWorking;

    //Semaphore semaphore;    // semafor za sunkronizacija na site tredovi da zavrsat

    //Lock lock= new ReentrantLock(); // lock za spodelena promenliva

    static Lock lock=new ReentrantLock();
    static Semaphore semaphore;
    public SharedArray(int[] array, int size) {
        this.array = array;
        this.size = size;
        semaphore=new Semaphore(0);

        //inicijalizacija na niza
    }

    // setirame kolku paralelni nitki ke rabotat
    public void setThreadsWorking(int threadsWorking) {
        this.threadsWorking = threadsWorking;

    }

    //koga nitkata ke zavrsi so broenje:
    public void threadDone(int threadFound){
        lock.lock();
        totalOccurences+=threadFound;//se updatira pojavuvanjeto na baraniot broj i mora da se zakluci zatoa sto e spodelen resurs
        if(maxPerThread<threadFound){
            maxPerThread=threadFound;
        }
        semaphore.release();
        lock.unlock();
    }

    public int getTotalOccurences() throws InterruptedException {
        semaphore.acquire(threadsWorking);
        semaphore.release(threadsWorking);
        return totalOccurences;             // sega sme sigurni deka site zavrsile so broenje
    }


    public int checkMax() throws InterruptedException {
        semaphore.acquire(threadsWorking);
        semaphore.release(threadsWorking);
        return maxPerThread;
    }

    public int[] getArray() {
        return array;       // nema potreba od zaklucuvanje- nizata ne se menuva
    }
}

class ParallelSearchThread extends Thread {

    SharedArray arr;
    int start;
    int end;
    int searching;

    static Lock lockk=new ReentrantLock();

    //inicijalizacija
    public ParallelSearchThread(SharedArray arr, int start, int end, int searching){
        this.arr=arr;
        this.start=start;
        this.end=end;
        this.searching=searching;
    }

    @Override
    public void run() {
        super.run();

        int localcount=0;

        int [] localArr=arr.getArray();

        for (int i = start; i < end ; i++) {
            if(localArr[i]==searching)localcount++;
        }

        arr.threadDone(localcount);


        try {
            if(arr.checkMax()==localcount){
                System.out.println("I found the max occurences");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
}

public class Parrarel_arr_search {



    public static void main(String[] args) throws InterruptedException {

        Random rand= new Random();

        int n=10000;
        int [] ar=new int[n];
        for(int i=0;i<n;i++){
            ar[i]= rand.nextInt(150);
        }

        SharedArray sa= new SharedArray(ar,n);      //inicijalizacija na spodelen resurs
        int threads=5;
        sa.setThreadsWorking(threads);

        int searchFor=137;

        ArrayList<ParallelSearchThread> threadList= new ArrayList<ParallelSearchThread>();
        int chunk=n/threads;        // golemina na parce od nizata so koe ke raboti sekoj thread

        for(int i=0;i<threads;i++) {
            ParallelSearchThread pt = new ParallelSearchThread(sa, chunk*i , chunk*i+chunk, searchFor);
            // kreiraj i startuvaj nov thread
            threadList.add(pt);
            pt.start();
        }

        try {
            System.out.println("Total found: "+sa.getTotalOccurences());    //ova ke ceka site da zavrsat so broenje
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(int i=0;i<threads;i++) {

            threadList.get(i).join();
        }


    }
}
