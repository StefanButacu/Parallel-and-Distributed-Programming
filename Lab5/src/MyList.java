import java.util.ArrayList;
import java.util.List;

public class MyList {
    public Node sentinelHead;
    public Node sentinelTail;

    MyList(){
        sentinelHead = new Node(0,0);
        sentinelTail = new Node(0,0);
        sentinelHead.next = sentinelTail;
    }

    public void addNode(Node node){
        Node prev = sentinelHead;
        prev.lock();
        Node current = sentinelHead.next;
        current.lock();
        while( current != sentinelTail) {
            if (node.exponent == current.exponent) {
                current.coefficient += node.coefficient;
                if (current.coefficient == 0) {
                    // delete the node
                    current.next.lock();
                    prev.next = current.next;
                    current.next.unlock();
                }
                break;
            }
            else if ( current.exponent > node.exponent){
                node.next = current;
                prev.next = node;
                break;
            }
            prev.unlock();
            prev = current;
            current = current.next;
            current.lock();
        }
        if (current == sentinelTail) {
            node.next = sentinelTail;
            prev.next = node;
        }
        prev.lock.unlock();
        current.lock.unlock();
    }

    public List<Node> getResultSum() {
        List<Node> result = new ArrayList<>();
        for (Node current = sentinelHead.next; current != sentinelTail; current = current.next) {
            result.add(current);
        }
        return result;
    }
}
