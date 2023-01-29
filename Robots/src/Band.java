import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Band {

    public static class BandRegistry{
        public List<Node> list = new ArrayList<>();
        public Lock lock = new ReentrantLock();

        public void addNode(Node node){
            list.add(node);
        }

        public static class Node{
            long threadId;
            String tip;
            int count;

            public Node(long threadId, String tip, int count) {
                this.threadId = threadId;
                this.tip = tip;
                this.count = count;
            }

            @Override
            public String toString() {
                return "Node{" +
                        "threadId=" + threadId +
                        ", tip='" + tip + '\'' +
                        ", count=" + count +
                        '}';
            }
        }
    }

    Lock lock = new ReentrantLock();
    Condition notFull = lock.newCondition();
    Condition notEmpty = lock.newCondition();

    AtomicBoolean isConsumersFinish;
    CyclicBarrier consumersBarrier;


    AtomicBoolean isProducersFinish;
    CyclicBarrier producersBarrier;
    BandRegistry bandRegistry;
    public int MAX_SIZE;

    public int count;

    public Band(int MAX_SIZE, int producers, int consumers) {
        this.MAX_SIZE = MAX_SIZE;
        isConsumersFinish = new AtomicBoolean(false);
        consumersBarrier = new CyclicBarrier(consumers);
        isProducersFinish = new AtomicBoolean(false);
        producersBarrier = new CyclicBarrier(producers);
        bandRegistry = new BandRegistry();
    }


    public void pune(){
        lock.lock();
        if(count + 4 > MAX_SIZE && isConsumersFinish.get()){
            System.out.println("Can never add more");
        }
        while(count + 4 > MAX_SIZE){
            System.out.println(Thread.currentThread().getName() + " Queue is full size: " + count );
            try {
                if(!isConsumersFinish.get()) {
                    notFull.await();
                }
                else {
                    break;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
        if(count + 4 <= MAX_SIZE) {
            count += 4;
            bandRegistry.lock.lock();
            bandRegistry.addNode(new BandRegistry.Node(Thread.currentThread().getId(), "depune", count));
            bandRegistry.lock.unlock();
            notEmpty.signal();

        }
        lock.unlock();

    }


    public void preia(){
        lock.lock();
        if(count - 3 <= 0 && isProducersFinish.get()){
            System.out.println("Can never remove more");
        }
        while(count - 3 < 0){
            System.out.println(Thread.currentThread().getName() + " Queue is empty size: " + count );
            try {
                if(!isProducersFinish.get()) {
                    notEmpty.await();
                }
                else{
                    break;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
        if(count - 3 >= 0) {
            count -= 3;
            bandRegistry.lock.lock();
            bandRegistry.addNode(new BandRegistry.Node(Thread.currentThread().getId(), "preia", count));
            bandRegistry.lock.unlock();

            notFull.signal();

        }
        else {
            System.out.println("cant remove");
        }

        lock.unlock();
    }

    public void setIsConsumersFinish(boolean value){
        try {
            consumersBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
        isConsumersFinish.compareAndSet(!value, value);
    }

    public void setIsProducersFinish(boolean value){
        try {
            producersBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
        isProducersFinish.compareAndSet(!value, value);
    }
}
