import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        String filename1 = "test_files/test1.txt";
        String filename2 = "test_files/test2.txt";
        FileUtils.generateFileWithRealArray(10, filename1);
        double[] elems = FileUtils.readArrayFromFile(filename1);
        Arrays.stream(elems).forEach(System.out::println);
        System.out.println(FileUtils.areFilesEqual(filename1, filename2));
        FileUtils.generateFileWithRealMatrix(10, 10, "test_files/matrix10x10.txt");
        FileUtils.generateFileWithRealMatrix(3, 3, "test_files/w3x3.txt");
        FileUtils.generateFileWithRealMatrix(5, 5, "test_files/w5x5.txt");
    }
}