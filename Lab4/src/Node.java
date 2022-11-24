public class Node {
    public int coefficient;
    public int exponent;

    public Node next;
    public Node() {
    }

    public Node(int coefficient, int exponent) {
        this.coefficient = coefficient;
        this.exponent = exponent;
    }
}
