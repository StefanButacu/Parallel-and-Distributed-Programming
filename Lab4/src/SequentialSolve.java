import java.io.*;
import java.util.List;

public class SequentialSolve {

    static MyList resultList = new MyList();
    static int polynomialNumber = 5;

    public static void main(String[] args) {
        long start = System.nanoTime();

        for(int nr = 0; nr < polynomialNumber; nr++) {
            String filename = "Lab4/resources/polynom[" + nr + "].in";
//            String filename = "polynom[" + nr + "].in";
            try(BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
                String line;
                while( (line = bufferedReader.readLine()) != null){
                    if(line.equals(""))
                        continue;
                    String[] numbers = line.split(" ");
                    Integer coef = Integer.valueOf(numbers[0]);
                    Integer exp = Integer.valueOf(numbers[1]);
                    Node node = new Node(coef, exp);
                    resultList.addNode(node);
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        List<Node> resultNodes = resultList.getResultSum();
        long end = System.nanoTime();
        System.out.println((double)(end - start)/1E6);//ms
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("Lab4/resources/polynomSeq.out"))){
//        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("polynomSeq.out"))){
            for(Node node: resultNodes){
                bufferedWriter.write(node.coefficient + " " + node.exponent + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
