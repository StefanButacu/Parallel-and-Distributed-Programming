#include <iostream>
#include <chrono>
#include <thread>
#include <vector>
#include <functional>
#include <fstream>
#include <string>
using namespace std;

double** inputMatrix;
double** result;
double** w;
int k, l;
int M, N;
ifstream fin;
ifstream Min("matrix.txt");
ifstream Win("w.txt");
ofstream rout("r.txt");
void calculateMatrix(double** inputMatrix, double** w, double** &result) {

    for (int i = 0; i < M; i++) {
        for (int j = 0; j < N; j++) {
            double s = 0;
            for (int ii = -k / 2; ii <= k / 2; ii++) {
                for (int jj = -l / 2; jj <= l / 2; jj++) {
                    int newI = i + ii;
                    int newJ = j + jj;
                    if (newI < 0)
                        newI = 0;
                    if (newJ < 0)
                        newJ = 0;
                    if (newI >= M)
                        newI = M - 1;
                    if (newJ >= N)
                        newJ = N - 1;
                    s += inputMatrix[newI][newJ] * w[ii + k / 2][jj + l / 2];
                }
            }
            result[i][j] = s;
        }
    }

}
void readInputMatrix() {
    Min >> M;
    Min >> N;
    inputMatrix = new double* [M];
    for (int i = 0; i < M; i++) {
        inputMatrix[i] = new double[N];
    }
    result = new double* [M];
    for (int i = 0; i < M; i++) {
        result[i] = new double[N];
    }
    for (int i = 0; i < M; i++) {
        for (int j = 0; j < N; j++) {
            Min >> inputMatrix[i][j];
        }
    }

    Min.close();

}
void readWMatrix() {
    Win >> k;
    Win >> l;
    w = new double* [k];
    for (int i = 0; i < k; i++) {
        w[i] = new double[l];
    }

    for (int i = 0; i < k; i++) {
        for (int j = 0; j < l; j++) {
            Win >> w[i][j];
        }
    }
    Win.close();

}
void printMatrix(double** result, int M, int N) {
    rout << M << endl << N << endl;
    for (int i = 0; i < M; i++) {
        for (int j = 0; j < N; j++) {
            rout << result[i][j] << endl;
        }
    }
    rout.close();
}
void threadCalculate(double** inputMatrix, double** w, double** result, int startLine, int endLine) {
    for (int i = startLine; i < endLine; i++) {
        for (int j = 0; j < N; j++) {
            double s = 0;
            for (int ii = -k / 2; ii <= k / 2; ii++) {
                for (int jj = -l / 2; jj <= l / 2; jj++) {
                    int newI = i + ii;
                    int newJ = j + jj;
                    if (newI < 0)
                        newI = 0;
                    if (newJ < 0)
                        newJ = 0;
                    if (newI >= M)
                        newI = M - 1;
                    if (newJ >= N)
                        newJ = N - 1;
                    s += inputMatrix[newI][newJ] * w[ii + k / 2][jj + l / 2];
                }
            }
            result[i][j] = s;
        }
    }

}
void calculateMatrixParallel(double** inputMatrix, double** w, double** result, int noThreads) {
    thread* threads = new thread[noThreads];
    int size = M;
    int rest = size % noThreads;
    int start = 0;
    int end = size / noThreads;
    for (int i = 0; i < noThreads; i++) {
        if (rest > 0) {
            end++;
            rest--;
        }
        threads[i] = thread(threadCalculate, inputMatrix, w, result, start, end);
        start = end;
        end += size / noThreads;
    }
    for (int i = 0; i < noThreads; i++) {
        threads[i].join();
    }

}
int main(int argc, char** argv) {
    int noThreads = 0;
    if (argc > 1) {
        noThreads = stoi(argv[1]);
    }

    readInputMatrix();
    readWMatrix();
    if (noThreads == 0) {
        auto startTime = chrono::high_resolution_clock::now();
        calculateMatrix(inputMatrix, w, result);
        auto endTime = chrono::high_resolution_clock::now();
        double durata = chrono::duration<double, milli>(endTime - startTime).count();
        printMatrix(result, M, N);

        for (int i = 0; i < M; ++i)
            delete[] inputMatrix[i];
        delete[] inputMatrix;

        for (int i = 0; i < k; ++i)
            delete[] w[i];
        delete[] w;
        for (int i = 0; i < M; ++i)
            delete[] result[i];
        delete[] result;

        cout << durata;
    }
    else {
        auto startTime = chrono::high_resolution_clock::now();
        calculateMatrixParallel(inputMatrix, w, result, noThreads);
        auto endTime = chrono::high_resolution_clock::now();
        double durata = chrono::duration<double, milli>(endTime - startTime).count();
        cout << durata;
        printMatrix(result, M, N);
        for (int i = 0; i < M; ++i)
            delete[] inputMatrix[i];
        delete[] inputMatrix;

        for (int i = 0; i < k; ++i)
            delete[] w[i];
        delete[] w;


        for (int i = 0; i < M; ++i)
            delete[] result[i];
        delete[] result;
    }
    return 0;
}