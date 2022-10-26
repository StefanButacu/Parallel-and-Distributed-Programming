import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Solver {
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
    static String fileMatrix10x10= "test_in/matrix10x10.txt";
    static String fileMatrix1000x1000= "test_in/matrix1000x1000.txt";
    static String fileMatrix1000x10= "test_in/matrix1000x10.txt";
    static String fileMatrix10x1000= "test_in/matrix10x1000.txt";
    static String fileW3x3= "test_in/w3x3.txt";
    static String fileW5x5= "test_in/w5x5.txt";

    static String matrix=".\\matrix.txt";
    static String w=".\\w.txt";

    public static void main(String[] args) {
        int noThreads = 0;
        if(args.length > 1)
            noThreads = Integer.valueOf(args[0]);
        double[][] inputMatrix = readMatrixFromFile(matrix);
        double[][] W = readMatrixFromFile(w);
//        double[][] inputMatrix = exampleMatrix;
//        double[][] W = exampleW3x3;
        int M = inputMatrix.length;
        int N = inputMatrix[0].length;
        double[][] result = new double[M][N];
        if( noThreads == 0) {
            long start = System.nanoTime();
            calculateMatrix(inputMatrix, W, result);
            long end = System.nanoTime();
            System.out.println((double)(end - start)/1E6);//ms
     }
        else{
            long start = System.nanoTime();
            calculateMatrixParallel(inputMatrix, W, result, noThreads);
            long end = System.nanoTime();
            System.out.println((double)(end - start)/1E6);//ms
        }
    }

    private static void calculateMatrixParallel(double[][] inputMatrix, double[][] w, double[][] result, int noThreads) {
        Thread[] threads = new Thread[noThreads];
        int size = inputMatrix.length;
        int rest = size % noThreads;
        int start = 0;
        int end = size / noThreads;
        for (int i = 0; i < noThreads; i++){
            if (rest > 0) {
                end++;
                rest--;
            }
            threads[i] = new MyThread(inputMatrix,w,result, start,end);
            threads[i].start();
            start = end;
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

    private static void calculateMatrix(double[][] inputMatrix, double[][] w, double[][] result) {
        int M = inputMatrix.length;
        int N = inputMatrix[0].length;
        int k = w.length;
        int l = w[0].length;
        for(int i = 0 ; i < M ; i++){
            for(int j = 0 ; j < N; j++){
                double s = 0;
                for(int ii = -k/2; ii <= k/2 ; ii++){
                    for(int jj = -l/2 ; jj <= l / 2; jj++){
                        int newI = i + ii;
                        int newJ = j + jj;
                        if(newI < 0)
                            newI = 0;
                        if(newJ < 0)
                            newJ = 0;
                        if(newI >= M)
                            newI = M-1;
                        if(newJ >= N)
                            newJ = N - 1;
                        s+= inputMatrix[newI][newJ] * w[ii + k/2][jj+l/2];
                    }
                }
            result[i][j] = s;
            }
        }

    }
    public static class MyThread extends Thread{

        double[][] inputMatrix;
        double[][] w;
        double[][] result;
        int startLine;
        int endLine;

        public MyThread(double[][] inputMatrix, double[][] w, double[][] result, int startLine, int endLine){
            super();
            this.inputMatrix = inputMatrix;
            this.w = w;
            this.result = result;
            this.startLine = startLine;
            this.endLine = endLine;
        }

        @Override
        public void run(){
            int k = w.length;
            int l = w[0].length;
            int N = inputMatrix[0].length;
            int M = inputMatrix.length;
            for(int i = startLine ; i < endLine ; i++){
                for(int j = 0 ; j < N; j++){
                    double s = 0;
                    for(int ii = -k/2; ii <= k/2 ; ii++){
                        for(int jj = -l/2 ; jj <= l / 2; jj++){
                            int newI = i + ii;
                            int newJ = j + jj;
                            if(newI < 0)
                                newI = 0;
                            if(newJ < 0)
                                newJ = 0;
                            if(newI >= M)
                                newI = M-1;
                            if(newJ >= N)
                                newJ = N - 1;
                            s+= inputMatrix[newI][newJ] * w[ii + k/2][jj+l/2];
                        }
                    }
                    result[i][j] = s;
                }
            }
        }

    }
    /**
     *
     * @param matrix1 - dimension M x N
     * @return matrix2 bordered  (M + 2) x (N + 2)
     */
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

    private static void printMatrix(double[][] matrix) {
        for(int i = 0 ; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length; j++){
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
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
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return elems;
    }
}
