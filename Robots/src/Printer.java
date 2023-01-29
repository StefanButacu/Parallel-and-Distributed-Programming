import java.util.concurrent.atomic.AtomicBoolean;

public class Printer extends Thread{

    private final AtomicBoolean finished;
    private final Band.BandRegistry bandRegistry;
    private int oldLength;


    public Printer(AtomicBoolean finished, Band.BandRegistry bandRegistry) {
        this.finished = finished;
        this.bandRegistry = bandRegistry;
        oldLength = 0;
    }

    @Override
    public void run() {
        while (!finished.get()) {
            printNewAdds();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void printNewAdds() {

        bandRegistry.lock.lock();
        System.out.println("Printing....");
        bandRegistry.list.forEach(System.out::println);
        System.out.println("End Printing....");

        bandRegistry.lock.unlock();
    }
}
