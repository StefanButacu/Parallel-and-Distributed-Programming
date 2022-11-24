public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        // MONOM (COEF, EXP);
        MyList list = new MyList();
        Node n1 = new Node(1, 5);
        Node n2 = new Node(1, 4);
        Node n3 = new Node(10, 3);
        Node n4 = new Node(10, 2);
        Node n5 = new Node(10, 1);
        Node n6 = new Node(-10, 1);
        Node n7 = new Node(20, 3);
        list.addNode(n1);
        list.addNode(n2);
        list.addNode(n3);
        list.addNode(n4);
        list.addNode(n5);
        list.addNode(n6);
        list.addNode(n7);
        list.printNodes();
    }
}