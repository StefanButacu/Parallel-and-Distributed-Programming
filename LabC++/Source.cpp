/*
#include <iostream>
#include <chrono>
#include <thread>
#include <vector>
#include "Source.h"
#include <functional>

using namespace std;

#define NMAX 100000
vector<double>a(NMAX);
vector<double>b(NMAX);
vector<double>c(NMAX);
vector<double>cPar(NMAX);


void suma(vector<double>& a, vector<double>& b, vector<double>& c, int start, int end, function<double(double, double)> f) {
	for (int i = start; i < end; i++) {
		c[i] = f(a[i], b[i]);
	}
}

bool areEqual(vector<double>& a, vector<double>& b) {
	for (int i = 0; i < a.size(); i++) {
		if (a[i] != b[i])
			return false;
	
	}
	return true;
}


void addLinearParallel(vector<double>& a, vector<double>& b, vector<double>& c, int length, int noThreads) {
	thread* threads = new thread[noThreads];
	int size = length;
	int rest = size % noThreads;
	int start = 0;
	int end = size / noThreads;
	for (int i = 0; i < noThreads; i++) {
		if (rest > 0) {
			end++;
			rest--;
		}
		threads[i] = thread(suma, ref(a), ref(b), ref(c), start, end, [=](int xx, int yy) {return sqrt(pow(xx, 3) + pow(yy, 3));});
		start = end;
		end += size / noThreads;
	}
	for (int i = 0; i < noThreads; i++) {
		threads[i].join();
	}

}
int main() {


	int noThreads = 4;
	for (int i = 0; i < NMAX; i++) {
		a[i] = i + 1 ;
		b[i] = i - 1;
	}

	auto startTime = chrono::high_resolution_clock::now();

	suma(a, b, c,0, NMAX, [=](int xx, int yy) {return sqrt(pow(xx,3) + pow(yy,3));});

	auto endTime = chrono::high_resolution_clock::now();

	double durata = chrono::duration<double, milli>(endTime - startTime).count();

	cout << "Timp secvential: " << durata << " ms\n";

	startTime = chrono::high_resolution_clock::now();
	addLinearParallel(a, b, cPar, NMAX, noThreads);
	endTime = chrono::high_resolution_clock::now();
	durata = chrono::duration<double, milli>(endTime - startTime).count();
	string msgEqual = areEqual(c, cPar) ? "are equal -- " : "not equal-- ";
	cout << msgEqual <<  "Timp paralel linear: " << durata << " ms\n";

	return 0;

}
*/