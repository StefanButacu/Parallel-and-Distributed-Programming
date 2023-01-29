public class Consumer extends Thread{


    Band band;

    public Consumer(Band band) {
        this.band = band;
    }

    @Override
    public void run() {
        for(int i = 0 ; i < 100; i++){
            band.preia();
            try {
                Thread.sleep(8);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        band.setIsConsumersFinish(true);
    }

}
