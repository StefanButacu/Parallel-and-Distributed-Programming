// Lab3.cpp : This file contains the 'main' function. Program execution begins and ends there.
//

/*
#include <iostream>
#include <fstream>
#include "mpi.h"
#define SIZE 1000000
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
int main()
{
	cout << "Hello  world";
	fin1 >> N_1;
	for (int i = 0; i < N_1; i++) {
		fin1 >> N1[i];
	}
	fin1.close();
	fin2 >> N_2;
	for (int i = 0; i < N_2; i++) {
		fin2 >> N2[i];
	}
	fin2.close();

	int carry = 0;
	int i = 0; 
	while (i < min(N_1, N_2)) {
		N3[i] = (N1[i] + N2[i] + carry) % 10;
		carry = (N1[i] + N2[i] + carry) / 10;
		i++;
	}
	while (i < N_1) {
		N3[i] = (N1[i] + carry) % 10;
		carry = (N1[i] + carry) / 10;
		i++;
	}
	while (i < N_2) {
		N3[i] = (N2[i] + carry) % 10;
		carry = (N2[i] + carry) / 10;
		i++;
	}
	if (carry) {
		N3[i] = 1;
	}
	N_3 = i + 1;
	// 19,975,308,642

	for (int i = 0; i < N_3; i++) {
		cout << N3[i];
	}

	return 0;
}
*/
