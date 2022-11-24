import javax.sound.midi.Soundbank;

public class MyList {
    public Node sentinelHead;

    MyList(){
        sentinelHead = new Node(0,0);
    }

    public synchronized void addNode(Node node){
        boolean foundSameExponent = false;
        Node current;
        Node prev = sentinelHead;
        for(current = sentinelHead.next ; current != null && node.exponent >= current.exponent; current = current.next) {
            if(node.exponent == current.exponent){
                foundSameExponent = true;
                current.coefficient += node.coefficient;
                if(current.coefficient == 0){
                    // delete the node
                    deleteNode(current);
                    break;
                }
            }
            prev = current;
        }
        if(!foundSameExponent) {
            node.next = prev.next;
            prev.next = node;
        }
    }

    private synchronized void deleteNode(Node toDelete) {
        Node prev = sentinelHead;
        Node current = prev;
        while(current != toDelete){
            prev = current;
            current = current.next;
        }
        prev.next = current.next;
    }

    public void printNodes() {
        for (Node current = sentinelHead.next; current != null; current = current.next) {
            System.out.println(current.coefficient + " * x ^" + current.exponent);
        }
    }
}
