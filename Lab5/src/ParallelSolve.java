import java.io.*;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class ParallelSolve {

    static int nrOfThreads;
    static int nrOfProducers;
    static int polynomialNumber;
    static int nrOfWorkers;

    static CyclicBarrier producersBarrier;

    public static void main(String[] args) {
        nrOfThreads = args.length > 1 ? Integer.valueOf(args[0])  : 5;
        polynomialNumber = args.length > 2 ? Integer.valueOf(args[1]) : 5;
        nrOfProducers = 2;
        nrOfWorkers = nrOfThreads - nrOfProducers;
        MyQueue myQueue = new MyQueue();
        Thread[] readers = new Thread[nrOfProducers];
        producersBarrier = new CyclicBarrier(nrOfProducers);
        int rest = polynomialNumber % nrOfProducers;
        int start = 0;
        int end = polynomialNumber / nrOfProducers;
        for(int i = 0; i < nrOfProducers; i++) {
            if( rest > 0){
                end++;
                rest--;
            }
            readers[i] = new Thread(new Producer(myQueue, nrOfWorkers, polynomialNumber, producersBarrier, start, end));
            start = end;
            end += polynomialNumber / nrOfProducers;
        }

        Thread[] workers = new Thread[nrOfWorkers];
        MyList result = new MyList();
        for(int i = 0 ; i < nrOfWorkers; i++) {
            workers[i] = new Thread(new Worker(myQueue, result));
        }
        long startTime = System.nanoTime();
        for(int i = 0; i < nrOfProducers; i++) {
            readers[i].start();
        }
        for(int i = 0 ; i < nrOfWorkers; i++) {
            workers[i].start();
        }

        try {
            for(int i = 0; i < nrOfProducers; i++) {
                readers[i].join();
            }
            for(int i = 0 ; i <nrOfWorkers; i++) {
                workers[i].join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        List<Node> resultNodes = result.getResultSum();
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("Lab5/resources/polynomPar.out"))){
            for(Node node: resultNodes){
                bufferedWriter.write(node.coefficient + " " + node.exponent + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        long endTime = System.nanoTime();
        System.out.println((double)(endTime - startTime)/1E6);//ms
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

        int nrOfWorkers;
        MyQueue myQueue;
        int polynomialNumber;
        CyclicBarrier producerBarrier;
        int start;
        int end;
        public Producer(MyQueue myQueue, int nrOfWorkers, int polynomialNumber, CyclicBarrier producerBarrier, int start, int end) {
            this.myQueue = myQueue;
            this.nrOfWorkers = nrOfWorkers;
            this.polynomialNumber = polynomialNumber;
            this.producerBarrier = producerBarrier;
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            for(int nr = start; nr < end; nr++) {

                // each producer should read a number of files
                String filename = "Lab5/resources/polynom[" + nr + "].in";
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.equals(""))
                            continue;
                        String[] numbers = line.split(" ");
                        Integer coef = Integer.valueOf(numbers[0]);
                        Integer exp = Integer.valueOf(numbers[1]);
                        Node node = new Node(coef, exp);
                        myQueue.add(node);
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                // barrier
                producerBarrier.await();
                myQueue.stop(nrOfWorkers);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (BrokenBarrierException e) {
                throw new RuntimeException(e);
            }

        }
    }
}

