
#include <iostream>
#include <chrono>
#include <thread>
#include <vector>
#include "Source.h"
#include <functional>
#include <fstream>
#include <string>
#include "Lab1.h"
#define MMAX 10000
#define NMAX 10
using namespace std;

double inputMatrix[MMAX][NMAX];
double result[MMAX][NMAX];
double w[5][5];
int k, l;
int M, N;
ifstream Min("matrix.txt");
ifstream Win("w.txt");
void calculateMatrix(double inputMatrix[][NMAX], double w[][5], double result[][NMAX]) {

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
    for (int i = 0; i < k; i++) {
        for (int j = 0; j < l; j++) {
            Win >> w[i][j];
        }
    }
    Win.close();

}
void threadCalculate(double inputMatrix[][NMAX], double w[][5], double result[][NMAX], int startLine, int endLine) {
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
void calculateMatrixParallel(double inputMatrix[][NMAX], double w[][5], double result[][NMAX], int noThreads) {
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
		cout << durata;
	}
    else {

        auto startTime = chrono::high_resolution_clock::now();
        calculateMatrixParallel(inputMatrix, w, result, noThreads);
        auto endTime = chrono::high_resolution_clock::now();
        double durata = chrono::duration<double, milli>(endTime - startTime).count();
        cout << durata;
    }

}
