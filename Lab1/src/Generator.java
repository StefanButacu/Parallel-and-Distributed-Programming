public class Generator {
    public static void main(String[] args) {
        FileUtils.generateFileWithRealMatrix(10000, 10, "matrix10000x10.txt");
        FileUtils.generateFileWithRealMatrix(10, 10000, "matrix10x10000.txt");
    }
}
