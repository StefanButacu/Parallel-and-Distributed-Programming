import java.io.*;
import java.util.List;

public class ParallelSolve {

    static int nrOfThreads;
    static int polynomialNumber = 5;
    public static void main(String[] args) {
        nrOfThreads = args.length > 1 ? Integer.valueOf(args[0])  : 4;
//        polynomialNumber = args.length > 2 ? Integer.valueOf(args[1]) : 3;
        MyQueue myQueue = new MyQueue();
        Thread reader = new Thread(new Producer(myQueue, nrOfThreads-1, polynomialNumber));
        Thread[] workers = new Thread[nrOfThreads-1];
        MyList result = new MyList();
        for(int i = 0 ; i < nrOfThreads-1; i++) {
            workers[i] = new Thread(new Worker(myQueue, result));
        }
        long start = System.nanoTime();
        reader.start();
        for(int i = 0 ; i < nrOfThreads-1; i++) {
            workers[i].start();
        }

        try {
            reader.join();
            for(int i = 0 ; i < nrOfThreads-1; i++) {
                workers[i].join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        List<Node> resultNodes = result.getResultSum();
        long end = System.nanoTime();
        System.out.println((double)(end - start)/1E6);//ms

        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("Lab4/resources/polynomPar.out"))){
//        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("polynomPar.out"))){
            for(Node node: resultNodes){
                bufferedWriter.write(node.coefficient + " " + node.exponent + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        public Producer(MyQueue myQueue, int nrOfWorkers, int polynomialNumber) {
            this.myQueue = myQueue;
            this.nrOfWorkers = nrOfWorkers;
            this.polynomialNumber = polynomialNumber;
        }

        @Override
        public void run() {
            for(int nr = 0; nr < polynomialNumber; nr++) {
//                String filename = "polynom[" + nr + "].in";
                String filename = "Lab4/resources/polynom[" + nr + "].in";
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
                }
            }
            myQueue.stop(nrOfWorkers);

        }
    }
}

