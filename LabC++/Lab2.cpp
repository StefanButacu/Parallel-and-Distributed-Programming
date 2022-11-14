#include <iostream>
#include <chrono>
#include <thread>
#include <vector>
#include <functional>
#include <fstream>
#include <string>
#include "Lab2.h"
#include <mutex>
#include <condition_variable>
using namespace std;

double** inputMatrix;
double** w;

int k, l;
int M, N;
ifstream fin;

ifstream Min("matrix.txt");
ifstream Win("w.txt");
ofstream rout("r.txt");

class my_barrier
{
public:
    my_barrier(int count) : thread_count(count), counter(0), waiting(0)
    {}

    void wait()
    {
        //fence mechanism
        std::unique_lock<std::mutex> lk(m);
        ++counter;
        ++waiting;
        cv.wait(lk, [&] {return counter >= thread_count;});
        cv.notify_one();
        --waiting;
        if (waiting == 0)
        {  //reset barrier
            counter = 0;
        }
        lk.unlock();
    }

private:
    std::mutex m;
    std::condition_variable cv;
    int counter;
    int waiting;
    int thread_count;
};

void readInputMatrix() {
    Min >> M;
    Min >> N;
    inputMatrix = new double* [M];
    for (int i = 0; i < M; i++) {
        inputMatrix[i] = new double[N];
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
    rout.precision(16);
    rout << M << endl << N << endl;
    for (int i = 0; i < M; i++) {
        for (int j = 0; j < N; j++) {
            rout << result[i][j] << endl;
        }
    }
    rout.close();
}

void thread_run(double** &inputMatrix,int M, int N, double** w, int startLine, int endLine, my_barrier& barrier) {
    // copy array 

    double** auxiliaryMemory = new double* [endLine - startLine + 1 + 2 * (k / 2)];
    for (int i = 0; i < endLine - startLine + 1 + 2 * (k / 2); i++) {
        auxiliaryMemory[i] = new double[N];
    }

    for (int i = startLine - k / 2; i <= endLine + k / 2; i++) {
        for (int j = 0; j < N; j++) {
            auxiliaryMemory[i - startLine + k / 2][j] = inputMatrix[i][j];
        }
    }
    // wait
    barrier.wait();
    // 
    for (int i = k / 2; i <= endLine - startLine + k / 2; i++) {
        for (int j = k / 2; j < N - k / 2; j++) {
            double s = 0;
            for (int ii = -k / 2; ii <= k / 2; ii++) {
                for (int jj = -l / 2; jj <= l / 2; jj++) {
                    int newI = i + ii;
                    int newJ = j + jj;
                    s += auxiliaryMemory[newI][newJ] * w[ii + k / 2][jj + l / 2];
                }
            }
            inputMatrix[startLine + i - k / 2][j] = s;
        }
    }
}

void calculateMatrixParallel(double** inputMatrix,int M, int N, double** w, int noThreads) {
    thread* threads = new thread[noThreads];
    int size = M - 2 * (k / 2);
    int rest = size % noThreads;
    int start = k/2;
    int end = size / noThreads + start - 1;
    my_barrier barrier(noThreads);
    for (int i = 0; i < noThreads; i++) {
        if (rest > 0) {
            end++;
            rest--;
        }
        threads[i] = thread(thread_run, ref(inputMatrix),M,N, w, start, end,ref(barrier));

        start = end;
        end += size / noThreads;
    }
    for (int i = 0; i < noThreads; i++) {
        threads[i].join();
    }

}

double** borderMatrix(double** inputMatrix, int M, int N ) {

    double** borderedMatrix = new double* [M + 2];
    for (int i = 0; i < M + 2; i++) {
        borderedMatrix[i] = new double[N + 2];
    }
    for (int j = 0; j < N; j++) {
        borderedMatrix[0][j + 1] = inputMatrix[0][j];
        borderedMatrix[M + 1][j + 1] = inputMatrix[M - 1][j];
    }
    for (int i = 0; i < M; i++) {
        borderedMatrix[i + 1][0] = inputMatrix[i][0];
        borderedMatrix[i + 1][N + 1] = inputMatrix[i][N - 1];
    }

    for (int i = 0; i < M; i++) {
        for (int j = 0; j < N; j++) {
            borderedMatrix[i + 1][j + 1] = inputMatrix[i][j];
        }
    }
    borderedMatrix[0][0] = inputMatrix[0][0];
    borderedMatrix[0][N + 1] = inputMatrix[0][N - 1];
    borderedMatrix[M + 1][0] = inputMatrix[M - 1][0];
    borderedMatrix[M + 1][N + 1] = inputMatrix[M - 1][N - 1];
    return borderedMatrix;
}
int main(int argc, char** argv) {
    int noThreads = 2;
    if (argc > 1) {
        noThreads = stoi(argv[1]);
    }

    int newM = 0;
    int newN = 0;
    readInputMatrix();
    readWMatrix();
    if (k == 3) {
        inputMatrix = borderMatrix(inputMatrix, M, N);
        newM = M + 2; 
        newN = N + 2;
    }
    else if (k == 5) {
        inputMatrix = borderMatrix(borderMatrix(inputMatrix, M, N), M + 2,N + 2);
        newM = M + 4;
        newN = N + 4; 
    }

    auto startTime = chrono::high_resolution_clock::now();
    calculateMatrixParallel(inputMatrix, newM, newN, w, noThreads);
    auto endTime = chrono::high_resolution_clock::now();
    double durata = chrono::duration<double, milli>(endTime - startTime).count();
    cout << durata;
    for (int i = 0; i < newM; ++i)
        delete[] inputMatrix[i];
    delete[] inputMatrix;

    for (int i = 0; i < k; ++i)
        delete[] w[i];
    delete[] w;
    
    return 0;
}