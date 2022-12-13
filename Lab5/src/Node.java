import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Node {
    public int coefficient;
    public int exponent;

    public Node next;

    public Lock lock = new ReentrantLock();
    public Node() {
    }

    public Node(int coefficient, int exponent) {
        this.coefficient = coefficient;
        this.exponent = exponent;
    }

    public void lock(){
        lock.lock();
    }

    public void unlock(){
        lock.unlock();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return coefficient == node.coefficient && exponent == node.exponent;
    }
    @Override
    public int hashCode() {
        return Objects.hash(coefficient, exponent);
    }
}
