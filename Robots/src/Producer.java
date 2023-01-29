public class Producer extends Thread{

    Band band;

    public Producer(Band band) {
        this.band = band;
    }

    @Override
    public void run() {
        for(int i = 0 ; i < 100; i++){
            band.pune();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        band.setIsProducersFinish(true);
    }
}
