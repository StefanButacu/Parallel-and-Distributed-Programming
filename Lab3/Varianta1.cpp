#include <iostream>
#include <fstream>
#include "mpi.h"
#define SIZE 10000
using namespace std;



ifstream fin1("E:\\PPD\\LAB\\PPD-LAB\\Parallel-and-Distributed-Programming-C++\\Lab\\Parallel-and-Distributed-Programming\\Lab3\\Numar1.txt");
ifstream fin2("E:\\PPD\\LAB\\PPD-LAB\\Parallel-and-Distributed-Programming-C++\\Lab\\Parallel-and-Distributed-Programming\\Lab3\\Numar2.txt");
ifstream fout("E:\\PPD\\LAB\\PPD-LAB\\Parallel-and-Distributed-Programming-C++\\Lab\\Parallel-and-Distributed-Programming\\Lab3\\Numar3.txt");

int N_1;
int N_2;
int N_3;
int N1[SIZE];
int N2[SIZE];
int N3[SIZE];

/*
void generateFile(string filePath, int n)
{
	ofstream g(filePath);
	g << n << endl;
	srand(time(0));
	for (int i = 0; i < n; i++) {
		g << rand() % 10 << ' ';
	}
	g.close();
}
*/
int main(int argc, char ** argv) {
	MPI_Init(&argc, &argv);        // starts MPI
	int rank, numProcs;
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	MPI_Comm_size(MPI_COMM_WORLD, &numProcs);


	fin1 >> N_1;
	fin2 >> N_2;
	
	int N = max(N_1, N_2);
	N += (numProcs -1 ) - N % (numProcs - 1);

	int chunkLen = N / (numProcs - 1);
	int currentP = 1;
	if (rank == 0) {

		for (int p = 0; p < N; p += chunkLen) {

			for (int i = 0; i < chunkLen; i++) {
				if (i + p < N_1) {
					fin1 >> N1[i + p];
				}
				else {
					N1[i + p] = 0;
				}
				if (i + p < N_2) {
					fin2 >> N2[i + p];
				}
				else
				{
					N2[i + p] = 0;
				}
			}

			MPI_Send(N1 + p, chunkLen, MPI_INT, currentP, 11111, MPI_COMM_WORLD);
			MPI_Send(N2 + p, chunkLen, MPI_INT, currentP, 11111, MPI_COMM_WORLD);
			currentP++;
		}
		int carry = 0;
		MPI_Send(&carry, 1, MPI_INT, 1, 11112, MPI_COMM_WORLD);

		int endCarry = 0;
		MPI_Status status;
		MPI_Recv(&endCarry, 1, MPI_INT, numProcs - 1, 11112, MPI_COMM_WORLD, &status);
		currentP = 1;
		for (int p = 0; p < N; p += chunkLen) {
			MPI_Recv(N3 + p, chunkLen, MPI_INT, currentP, 113, MPI_COMM_WORLD, &status);
			currentP++;
		}
		if (endCarry == 1) {
			N++;
			N3[N] = 1;
			endCarry = 0;
		}
		cout << "Result: ";
		for (int i = 0; i < N; i++) {
			cout << N3[i] << ' ';
		}
	}
	else
	{
		MPI_Status* statuses = new MPI_Status[numProcs];

		MPI_Recv(N1, chunkLen, MPI_INT, 0, 11111, MPI_COMM_WORLD, statuses + rank);
		MPI_Recv(N2, chunkLen, MPI_INT, 0, 11111, MPI_COMM_WORLD, statuses + rank);	
		int carry = 0;
		MPI_Recv(&carry, 1, MPI_INT, rank - 1, 11112, MPI_COMM_WORLD, statuses + rank);
	
		for(int i = 0 ; i < chunkLen; i++){
			N3[i] = (N1[i] + N2[i] + carry) % 10;
			carry = (N1[i] + N2[i] + carry) / 10;
		}
		cout << rank;
		cout << "N3: ";
		for (int i = 0; i < chunkLen; i++) {
			cout << N3[i] << ' ';
		}
		MPI_Send(&carry, 1, MPI_INT, (rank + 1) % numProcs, 11112, MPI_COMM_WORLD);
		
		MPI_Send(N3, chunkLen, MPI_INT, 0, 113, MPI_COMM_WORLD);
	}


	MPI_Finalize();
}