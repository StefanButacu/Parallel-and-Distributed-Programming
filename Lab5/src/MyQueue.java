import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyQueue {
    final Integer MAX_SIZE = 5;

    final Node[] items = new Node[MAX_SIZE];

    final Lock lock = new ReentrantLock();
    final Condition notFull = lock.newCondition();
    final Condition notEmpty = lock.newCondition();
    int putptr, takeptr, count;


    public MyQueue(){

    }
    public  void add(Node node) throws InterruptedException {
        lock.lock();
        try {
            while (count == items.length)
                notFull.await();
            items[putptr] = node;
            if (++putptr == items.length) putptr = 0;
            ++count;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public Node poll() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0)
                notEmpty.await();
            Node x = items[takeptr];
            if (++takeptr == items.length) takeptr = 0;
            --count;
            notFull.signal();
            return x;
        } finally {
            lock.unlock();
        }

     }

    public void stop(Integer noOfWorkers) throws InterruptedException {
        for (int i = 0; i < noOfWorkers; ++i) {
            this.add(null);
        }

   }

}
