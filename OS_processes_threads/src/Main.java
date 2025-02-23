import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Heavy extends Thread{
    private long count;

    public Heavy(long count) {
        this.count = count;
    }

    @Override
    public void run() {
        count=getCount();
    }

    public long getCount() {
        return ++count;
    }
}

class IntWrapper {
    private int a;

    public IntWrapper(int a) {
        this.a = a;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }
}

class ExampleT extends Thread{
    //Semaphore semaphore=new Semaphore(1);
    //private static Lock locker=new ReentrantLock();
    private IntWrapper integerW;
    private int privateInt;

    public int publicInt=0;

    public ExampleT(IntWrapper integer, int privateInt) {
        this.integerW=integer;
        this.privateInt = privateInt;
    }

    public void PrivateIntINCR(){
        this.privateInt++;
    }

    public void PublicIntINCR(){
        this.publicInt++;
    }

    public void IntegerIncr() throws InterruptedException {

        int a=integerW.getA();

        double i=1;
        while (i<1000000){
            i+= 0.1;
        }

        a++;
        integerW.setA(a);


    }



    @Override
    public void run() {

        try {
            IntegerIncr();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String toString() {
        return String.format("%12s %6d %2d %2d",getName(),integerW.getA(),privateInt,publicInt);
    }

}



public class Main {

    static int dims;

    static Lock lock;

    static Lock maxlock;

    static int threadnum=0;

    //static int matrixDim =300;

    static Semaphore threadfinish;
    static Semaphore findmax;

    static Semaphore threadstart;

    /*
    public static char[][] generateRandomCharMatrix(int rows, int cols) {
        char[][] matrix = new char[rows][cols];
        Random random = new Random();



        // Fill the matrix with random characters
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                boolean boo=random.nextBoolean();
                if(boo){
                    char c= (char) (random.nextInt(1,9)+'/');
                    matrix[i][j]=c;
                }
                else matrix[i][j] = (char) (random.nextInt(26) + 'a'); // Generates random lowercase letters
            }
        }

        return matrix;
    }


    public static void fillMatrixRandom(char [][] matrix,int size) {

        Random random = new Random();
        // Fill the matrix with random characters
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                boolean boo=random.nextBoolean();
                if(boo){
                    char c= (char) (random.nextInt(1,9)+'/');
                    matrix[i][j]=c;
                }
                else matrix[i][j] = (char) (random.nextInt(26) + 'a'); // Generates random lowercase letters
            }
        }


        //semaphore.release(size);


    }

    public static void Concatenate(String concat){

        lock.lock();
        //concatenated+=concat;
        lock.unlock();
    }



    static class RowThread extends Thread{
        String local_concatenated="";

        int row;

        char [][] sharedMatrix;


        public RowThread(int row, char[][] sharedMatrix) {
            this.row = row;
            this.sharedMatrix = sharedMatrix;
        }

        @Override
        public void run() {
            super.run();


            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            concat();


            Concatenate(local_concatenated);

            canPrint.release();
        }

        public void concat(){
            for (int i = 0; i < matrixDim; i++) {
                char c=sharedMatrix[row][i];
                if(Character.isLetter(c)){
                    local_concatenated+=c;
                }
            }



        }


    }


    */

    static class RowPthread extends Thread{

        String localconcat;

        int Rnum;

        int Cstart;
        int Cend;

        SharedMatrix sharedMatrix;


        public RowPthread(int rnum, int cstart, int cend, SharedMatrix matrix) {
            Rnum = rnum;
            Cstart = cstart;
            Cend = cend;
            this.sharedMatrix=matrix;
            this.localconcat="";
        }

        @Override
        public void run() {
            super.run();

            try {
                threadstart.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            char[][]localmatrix=sharedMatrix.getMatrix();

            for (int i = Cstart; i < Cend; i++) {
                char c=localmatrix[Rnum][i];
                if(Character.isLetter(c)){
                    localconcat+=c;
                }
            }


//            for (int i = 0; i < dims; i++) {
//                char c=localmatrix[Rnum][i];
//                if(Character.isLetter(c)){
//                    localconcat+=c;
//                }
//            }



            sharedMatrix.setConcat(localconcat);
            sharedMatrix.setLargestString(localconcat);

            threadfinish.release();

            try {
                findmax.acquire();
                if(this.localconcat.length()==sharedMatrix.getLargestString().length()){
                    System.out.println("I found the largest string "+getName() +" Local concat is: "+this.localconcat);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


        }
    }


    static class SharedMatrix{

        char[][]matrix;

        String largestString;

        String concatenated;
        public SharedMatrix() {
            this.matrix=new char[dims][dims];
            this.largestString="";
            this.concatenated="";
        }


        public  void fillMatrix(){
            Random rand = new Random();
            // Fill the matrix with random characters
            for (int i = 0; i < dims; i++) {
                for (int j = 0; j < dims; j++) {
                    boolean boo=rand.nextBoolean();
                    if(boo){
                        char c= (char) (rand.nextInt(1,9)+'/');
                        matrix[i][j]=c;
                    }
                    else matrix[i][j] = (char) (rand.nextInt(26) + 'a'); // Generates random lowercase letters
                }
            }

            threadstart.release(threadnum);

        }

        public void setLargestString(String largest) {
            maxlock.lock();
            if(this.largestString.length()<largest.length()){
                this.largestString=largest;
            }
            maxlock.unlock();

        }

        public void setConcat(String concat) {
            lock.lock();
            this.concatenated += concat;
            lock.unlock();
        }

        public char[][] getMatrix() {
            return matrix;
        }

        public String getLargestString() {
            return largestString;
        }
    }


    static void init(){
        maxlock=new ReentrantLock();
        lock=new ReentrantLock();
        findmax=new Semaphore(0);
        threadfinish=new Semaphore(0);
        threadstart=new Semaphore(0);
    }





    public static void main(String[] args) throws InterruptedException {
        long t = System.currentTimeMillis();
//        Heavy h=new Heavy(1);
//        long sum=0;
//        h.start();
//        try {
//            h.join();
//            System.out.println("Result is: "+h.getCount());
//            System.out.println((System.currentTimeMillis()-t)/1000.0);
//        } catch (InterruptedException e) {
//
//        }
//
//        IntWrapper sharedInteger = new IntWrapper(0);
//
//        for (int r=0;r<4;r++) {
//
//
//            HashSet<ExampleT> threads = new HashSet<>();
//
//
//            for (int i = 0; i < 2000; i++) {
//                ExampleT thread = new ExampleT(sharedInteger, 0);
//                thread.start();
//                threads.add(thread);
//
//            }
//
//
//            threads.forEach(th -> {
//                try {
//                    th.join();
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//
//



        Scanner sc=new Scanner(System.in);
        dims =sc.nextInt();
        sc.close();

        init();


        threadnum=dims*5;

        //threadnum=dims;




        SharedMatrix matrix=new SharedMatrix();


        matrix.fillMatrix();

        for (int i = 0; i < matrix.matrix.length; i++) {
            for (int j = 0; j < matrix.matrix[0].length; j++) {
                System.out.print(" "+matrix.matrix[i][j]);
            }
            System.out.println();
        }


        int chunk=dims/5;

        List<RowPthread> list_pthreads=new ArrayList<>();



        for (int i = 0; i < dims; i++) {
            for (int j = 0; j < 5; j++) {
                RowPthread pthread=new RowPthread(i,chunk*j,chunk*(j+1),matrix);
                list_pthreads.add(pthread);

            }
        }

        System.out.println(list_pthreads.size());




        for (RowPthread thread : list_pthreads) {
            thread.start();
        }

        threadfinish.acquire(threadnum);

        findmax.release(threadnum);



        System.out.println("\n " + (System.currentTimeMillis() - t) / 1000.0);


    }

}