import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ParallelSolve {

    public static void main(String[] args) {
        String filename = "Lab4/resources/polynom[0].in";
        int n = 5;
        MyQueue myQueue = new MyQueue();
        Thread reader = new Thread(new Producer(filename, myQueue, n-1));
        Thread[] workers = new Thread[n-1];
        MyList result = new MyList();
        for(int i = 0 ; i < n-1; i++) {
            workers[i] = new Thread(new Worker(myQueue, result));
        }

        reader.start();
        for(int i = 0 ; i < n-1; i++) {
            workers[i].start();
        }

        try {
            reader.join();
            for(int i = 0 ; i < n-1; i++) {
                workers[i].join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        result.getResultSum();
    }
    static class Worker implements Runnable{

        MyQueue myQueue;
        MyList result;

        public Worker(MyQueue myQueue, MyList result) {
            this.myQueue = myQueue;
            this.result = result;
        }

        @Override
        public void run() {
            System.out.println("Thread started " + Thread.currentThread().getId());
            Node node = null;
            try {
                node = myQueue.poll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (node != null) {
                result.addNode(node);
                try {
                    node = myQueue.poll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    static class Producer implements Runnable{

        String filename;
        int nrOfWorkers;
        MyQueue myQueue;
        public Producer(String filename, MyQueue myQueue, int nrOfWorkers) {
            this.filename = filename;
            this.myQueue = myQueue;
            this.nrOfWorkers = nrOfWorkers;
        }

        @Override
        public void run() {
            try(BufferedReader bufferedReader = new BufferedReader(new BufferedReader(new FileReader(filename)))) {
                String line;
                while( (line = bufferedReader.readLine()) != null){
                    if(line.equals(""))
                        continue;
                    String[] numbers = line.split(" ");
                    Integer coef = Integer.valueOf(numbers[0]);
                    Integer exp = Integer.valueOf(numbers[1]);
                    Node node = new Node(coef, exp);
                    myQueue.add(node);
                }
                myQueue.stop(nrOfWorkers);

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

