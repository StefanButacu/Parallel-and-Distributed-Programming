public class Solver {
    double[][] exampleMatrix = new double[][]{
            new double[] {7, 6, 5, 5, 6 , 7},
            new double[] {6, 4, 3, 3, 4, 6},
            new double[] {5, 3, 2, 2, 3, 5},
            new double[] {5, 3, 2, 2, 3, 5},
            new double[] {6, 4, 3, 3, 4, 6},
            new double[] {7, 6, 5, 5, 6 , 7}
    };
    double[][] exampleW3x3 = new double[][]{
            new double[] {0, -1, 0},
            new double[] {-1, 5,-1},
            new double[] {0, -1, 0},
    };


    public static void main(String[] args) {
        // TODO - take filenames as args
        double[][] matrix = FileUtils.readMatrixFromFile("test_in/matrix10x10.txt");
        double[][] W = FileUtils.readMatrixFromFile("test_in/w3x3.txt");
        matrix = borderMatrix(matrix);
        double[][] result = calculateMatrix(matrix, W);
        FileUtils.writeMatrixToFile(result, "test_out/out10x10.txt");
    }
    private static void printMatrix(double[][] matrix) {
        for(int i = 0 ; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length; j++){
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static double[][] calculateMatrix(double[][] matrix1, double[][] w) {
        int bigM = matrix1.length;
        int bigN = matrix1[0].length;
        double[][] result = new double[bigM-2][bigN-2];
        int k = w.length;
        int l = w[0].length;

        for(int i = 1 ; i < bigM - 1; i++){
            for(int j = 1 ; j < bigN - 1; j++){
                double s = 0;
                for(int ii = -k/2; ii <= k/2 ; ii++){
                    for(int jj = -l/2 ; jj <= l / 2; jj++){
                        s += matrix1[i + ii][j+jj] * w[ii + k/2][jj + l/2];
                    }
                }
                result[i-1][j-1] = s;
            }

        }

        return result;
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


}
