import java.io.*;
import java.util.Random;

public class FileUtils {

    public static double[] readArrayFromFile(String fileName){
        double[] elems = null;
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            Integer nrOfElems = Integer.valueOf(bufferedReader.readLine());
            elems = new double[nrOfElems];
            for(int i = 0 ; i < nrOfElems; i++){
                Double elem = Double.valueOf(bufferedReader.readLine());
                elems[i] = elem;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return elems;
    }

    public static double[][] readMatrixFromFile(String fileName){
        double[][] elems = null;
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            Integer M = Integer.valueOf(bufferedReader.readLine());
            Integer N = Integer.valueOf(bufferedReader.readLine());
            elems = new double[M][N];
            for(int i = 0 ; i < M; i++){
                for(int j = 0 ; j < N; j++) {
                    Double elem = Double.valueOf(bufferedReader.readLine());
                    elems[i][j] = elem;
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return elems;
    }

    public static boolean areFilesEqual(String filename1, String filename2){
        try (BufferedReader bf1 = new BufferedReader(new FileReader(filename1));
             BufferedReader bf2 = new BufferedReader(new FileReader(filename2))) {
            String line1 = "", line2 = "";
            while ((line1 = bf1.readLine()) != null) {
                line2 = bf2.readLine();
                if (line2 == null || !line1.equals(line2)) {
                    return false;
                }
            }
            if (bf2.readLine() == null) {
                return true;
            }
            else {
                return false;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void generateFileWithRealArray(Integer numberOfElements, String filename){
        Random rand = new Random();
        createEmptyFile(filename);
        try( FileWriter fileWriter = new FileWriter(filename) ) {
            fileWriter.write(String.valueOf(numberOfElements));
            fileWriter.write("\n");
            for (int i = 0; i < numberOfElements; i++) {
                double doubleNumber = rand.nextDouble();
                fileWriter.write(String.valueOf(doubleNumber));
                fileWriter.write("\n");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void generateFileWithRealMatrix(Integer M, Integer N, String filename){
        Random rand = new Random();
        createEmptyFile(filename);
        try( FileWriter fileWriter = new FileWriter(filename) ) {
            fileWriter.write(String.valueOf(M));
            fileWriter.write("\n");
            fileWriter.write(String.valueOf(N));
            fileWriter.write("\n");
            for (int i = 0; i < M; i++) {
                for(int j = 0; j < N ; j++){
                    double doubleNumber = rand.nextDouble();
                    fileWriter.write(String.valueOf(doubleNumber));
                    fileWriter.write("\n");
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void writeMatrixToFile(double[][] result, String filename) {
        createEmptyFile(filename);
        Integer M = result.length;
        Integer N = result[0].length;
        try( FileWriter fileWriter = new FileWriter(filename) ) {
            fileWriter.write(String.valueOf(M));
            fileWriter.write("\n");
            fileWriter.write(String.valueOf(N));
            fileWriter.write("\n");
            for (int i = 0; i < M; i++) {
                for(int j = 0; j < N; j++) {
                    fileWriter.write(String.valueOf(result[i][j]));
                    fileWriter.write("\n");
                }
            }
        }catch (IOException e){
            e.printStackTrace();
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
