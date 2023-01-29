import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    public static void main(String[] args) {
        int p = 3;
        int c = 2;
        int MAX_SIZE = 66;

        Band band = new Band(MAX_SIZE, p, c);
        Thread[] producers = new Thread[p];
        Thread[] consumers = new Thread[c];

        for(int i = 0 ; i < p ; i++){
            producers[i] = new Producer(band);
            producers[i].start();
        }
        for(int i = 0 ; i < c ; i++){
            consumers[i] = new Consumer(band);
            consumers[i].start();
        }

        AtomicBoolean finished = new AtomicBoolean(false);

        Thread printer = new Printer(finished, band.bandRegistry);
        printer.start();

        for(int i = 0 ; i < p ; i++){
            try {
                producers[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        for(int i = 0 ; i < c ; i++){
            try {
                consumers[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        finished.set(true);
        try {
            printer.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//        printer.start();
        System.out.println(band.count);


    }
}