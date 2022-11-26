import java.io.*;
import java.util.List;

public class ParallelSolve {

    public static void main(String[] args) {
        int n = 5;
        int polinomialNumber = 3;
        MyQueue myQueue = new MyQueue();
        Thread reader = new Thread(new Producer(myQueue, n-1, polinomialNumber));
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
        List<Node> resultNodes = result.getResultSum();
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("Lab4/resources/polynomPar.out"))){
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

