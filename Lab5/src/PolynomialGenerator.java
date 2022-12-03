import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class PolynomialGenerator {
   static int maxGrade = 1000;
    static int maxSize = 50;
   static int polynomialNumber = 10;

    public static void main(String[] args) {
        if(args.length >= 3){
            maxGrade = Integer.valueOf(args[0]);
            maxSize = Integer.valueOf(args[1]);
            polynomialNumber = Integer.valueOf(args[2]);
        }
        generatePolynomials();
    }
    static void generatePolynomials(){
        Random rand = new Random();
        for(int nr = 0; nr < polynomialNumber; nr++) {
            String filename = "Lab5/resources/polynom["+nr+"].in";
            createEmptyFile(filename);
            try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename))) {
                int randomSize = 1 + rand.nextInt(maxSize);
                for (int i = 0; i < randomSize; i++) {
                    int coef = 1 + rand.nextInt(100);
                    int exp = rand.nextInt(maxGrade + 1);
                    bufferedWriter.write(coef + " " + exp + "\n");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private static void createEmptyFile(String filename) {
        File file = new File(filename);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
