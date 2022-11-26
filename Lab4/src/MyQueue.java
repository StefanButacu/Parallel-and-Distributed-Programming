import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyQueue {
    Queue<Node> q;
    final Lock lock = new ReentrantLock();
    final Condition notEmpty = lock.newCondition();

    public MyQueue(){
        q = new LinkedList<>();
    }
    public  void add(Node n){
        lock.lock();
        try{
            q.add(n);
            notEmpty.signalAll();
        }
        finally {
            lock.unlock();
        }
    }

    public Node poll() throws InterruptedException {
        lock.lock();
        try {
            while (q.isEmpty()) {
                notEmpty.await();
            }
            Node node = q.remove();
            return node;
        }
        finally{
                lock.unlock();
        }

     }

    public void stop(Integer noOfWorkers){
        for (int i = 0; i < noOfWorkers; ++i) {
            this.add(null);
        }

   }

}
