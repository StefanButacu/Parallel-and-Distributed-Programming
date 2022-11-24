import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyQueue {
    Queue<Node> q;
    final Lock lock = new ReentrantLock();
    final Condition consumeCond = lock.newCondition();

    public MyQueue(){
        q = new LinkedList<>();
    }
    public synchronized void add(Node n){
        q.add(n);
        notifyAll();
    }

    public synchronized Node poll() throws InterruptedException {
        while (q.isEmpty())
            wait();
        Node node = q.remove();
        notifyAll();
        return node;
    }

    public synchronized void stop(Integer noOfWorkers){
        for (int i = 0; i < noOfWorkers; ++i) {
            q.add(null);
        }

   }

}
