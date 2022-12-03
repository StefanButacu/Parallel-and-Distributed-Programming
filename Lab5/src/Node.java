import java.util.Objects;

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
