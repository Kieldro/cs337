/*
Ian Buitrago: Slip days used for this project: 0  Slip days used (total): 1
John Richter: Slip days used for this project: 0  Slip days used (total): 0
CS 337
project 3
4/30/2012

Pair programming log (> 90% paired)
4/16 4:30 - 5:30p  Ian, John 2 hr
4/17 6 - 7p  John, 1 hr
4/17 7 - 10p  Ian, John, 6 hr
4/18 9:30p - 10:30p Ian, John, 2 hrs

Total time 11 hrs, 10 hrs of pair programing

*/
import java.io.*;		// for File
import java.util.*;		// for scanner

public class RSA{
	final static boolean DEBUG = true;
	public static void main(String[] args)// throws Exception
	{
		String arg = args[0];
		File inputFile = new File(args[1]);
		BF(inputFile);
	}

	public static void BF()
	{
		if(DEBUG) System.out.println("test");
		// input
		Scanner sc = new Scanner(file);
		n = sc.nextLong();
		sc.close();
	}

	public static long exponentiation(long a, long b, long n){
		long c = 1;
		while(b > 0){
			if(b % 2 == 1)
				c = ((c*a) % n);
			a = (a*a) % n;
			b = b / 2;
		}
		return c % n;
	}

	// Returns true if x is prime.
	public static boolean isPrime(long x){
		if(x % 2 == 0) return false;
		for(int i = 3; i*i <= x; i += 2)
			if(x%i == 0) return false;
		return true;
	}
}
