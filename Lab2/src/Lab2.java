import java.io.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Lab2 {
    static double[][] exampleMatrix = new double[][]{
            new double[] {7, 6, 5, 5, 6 , 7},
            new double[] {6, 4, 3, 3, 4, 6},
            new double[] {5, 3, 2, 2, 3, 5},
            new double[] {5, 3, 2, 2, 3, 5},
            new double[] {6, 4, 3, 3, 4, 6},
            new double[] {7, 6, 5, 5, 6 , 7}
    };
    static double[][] exampleW3x3 = new double[][]{
            new double[] {0, -1, 0},
            new double[] {-1, 5,-1},
            new double[] {0, -1, 0},
    };
    static CyclicBarrier barrier;
    static String matrixPath = "E:\\PPD\\LAB\\PPD-LAB\\Parallel-and-Distributed-Programming\\test_in\\matrix1000x1000.txt";
    static String wPath = "E:\\PPD\\LAB\\PPD-LAB\\Parallel-and-Distributed-Programming\\test_in\\w5x5.txt";

    public static void main(String[] args) {
        int noThreads = 2;
        double[][] inputMatrix =
                readMatrixFromFile(matrixPath);
        double[][] w = readMatrixFromFile(wPath);
        if(w.length == 3){
            inputMatrix = borderMatrix(inputMatrix);
        } else if (w.length == 5) {
            inputMatrix = borderMatrix(borderMatrix(inputMatrix));
        }
        long start = System.nanoTime();
        calculateMatrixParallel(inputMatrix, w, noThreads);
        long end = System.nanoTime();
        System.out.println((double)(end - start)/1E6);//ms
        // TODO - remove borders?
//        for(int i = w.length / 2 ; i < inputMatrix.length-w.length/2; i++) {
//            for(int j = w.length / 2 ; j < inputMatrix[0].length - w.length / 2; j++) {
//                System.out.print(inputMatrix[i][j] + " ");
//            }
//            System.out.println();
//        }
    }

    private static void calculateMatrixParallel(double[][] inputMatrix, double[][] W, int noThreads) {
        Thread[] threads = new Thread[noThreads];
        int size = inputMatrix.length - 2 * (W.length / 2); // 2 * 3/2 = 2*1 =4 ||   2 * 5 / 2 = 2 *2 = 4
        int rest = size % noThreads;
        int start = W.length / 2;
        int end = size / noThreads + start - 1;
        barrier = new CyclicBarrier(noThreads);
        for (int i = 0; i < noThreads; i++){
            if (rest > 0) {
                end++;
                rest--;
            }
            threads[i] = new MyThread(inputMatrix, W, start,end);
            threads[i].start();
            start = end + 1;
            end += size / noThreads;

        }
        for(int i = 0 ; i < noThreads; i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return elems;
    }
    private static double[][] borderMatrix(double[][] matrix1) {
        int M = matrix1.length;
        int N = matrix1[0].length;
        double[][] borderedMatrix = new double[M+2][N+2];
        for(int j = 0 ; j < N; j++){
            borderedMatrix[0][j+1] = matrix1[0][j];
            borderedMatrix[M+1][j+1] = matrix1[M-1][j];
        }
        for(int i = 0 ; i < M; i++){
            borderedMatrix[i+1][0] = matrix1[i][0];
            borderedMatrix[i+1][N+1] = matrix1[i][N-1];
        }
        for(int i = 0 ; i < M; i++){
            System.arraycopy(matrix1[i], 0, borderedMatrix[i + 1], 1, N);
        }
        borderedMatrix[0][0] = matrix1[0][0];
        borderedMatrix[0][N+1] = matrix1[0][N-1];
        borderedMatrix[M+1][0] = matrix1[M-1][0];
        borderedMatrix[M+1][N+1] = matrix1[M-1][N-1];
        return borderedMatrix;
    }
    public static class MyThread extends Thread{
        double[][] inputMatrix;
        double[][] w;
        double[][] auxiliaryMemory;
        int startLine;
        int endLine;

        public MyThread(double[][] inputMatrix, double[][] w, int startLine, int endLine){
            super();
            this.inputMatrix = inputMatrix;
            this.w = w;
            this.startLine = startLine;
            this.endLine = endLine;
        }

        @Override
        public void run(){
            int k = w.length;
            int l = w[0].length;
            int N = inputMatrix[0].length;
            /// do copy
            auxiliaryMemory = new double[endLine - startLine + 1 + 2 * (k / 2)][N];
            for(int i = startLine - k/2; i <= endLine + k / 2; i++) {
                for(int j = 0; j < N ; j++) {
                    auxiliaryMemory[i - startLine + k / 2 ][j] = inputMatrix[i][j];
                }
            }
            // wait
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
            for(int i = k/2 ; i <= endLine - startLine + k/2; i++){
                for(int j = k/2 ; j < N - k/2; j++){
                    double s = 0;
                    for(int ii = -k/2; ii <= k / 2 ; ii++){
                        for(int jj = -l/2 ; jj <= l / 2; jj++){
                            int newI = i + ii;
                            int newJ = j + jj;
                            s+= auxiliaryMemory[newI][newJ] * w[ii + k/2][jj+l/2];
                        }
                    }
                    inputMatrix[startLine + i - k/2][j] = s;
                }
            }
        }

    }
}