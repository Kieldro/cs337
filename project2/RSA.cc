/*
Ian Buitrago
C++
3-8-2012

run with:
g++ -o RSA RSA.cc ; RSA key 47 59
*/


#include <iostream>		// for string and cout
#include <cstdlib>		// for atol()

using namespace std;

// prototypes
void keyGen(long p, long q);

// globals
const static bool DEBUG = true;

int main(int argc, char* argv[]){
	
	if (DEBUG) cout << "argc: " << argc << endl;
	for(int i = 0; i < argc; ++i){
		;if (DEBUG) cout << "argv: " << argv[i] << endl;
		
	}
	
	string arg = argv[1];
	if (arg == "key"){
		long p = atol( argv[2] );
		long q = atol( argv[3] );
		if (DEBUG) cout << "p: " << p << endl;
		if (DEBUG) cout << "q: " << q << endl;
		keyGen(p, q);
	}else
		cout << "invalid arguement: " << arg << endl;
	
	
	
	
	return 0;
}

void keyGen(long p, long q){
	long n = p * q;
	long e = 7;		// pick a small prime?
	long d = 0;
	
	if (DEBUG) cout << "n, e, d " << endl;
	cout << n << " " << e << " "  << d << endl;
}
