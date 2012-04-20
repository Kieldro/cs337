/*
Ian Buitrago: Slip days used for this project: 0  Slip days used (total): 1
John Richter: Slip days used for this project: 0  Slip days used (total): 2
CS 337
project 3
4/30/2012

Pair programming log (> 90% paired)
4/16 1 - 3p  Ian 2 hrs
4/17 1 - 3p  Ian, John 4 hr
4/20 10 - 11a Ian 1 hr

Total time 11 hrs, 8 hrs of pair programing

NOTES:
run with: source runTest.sh

*/
import java.io.*;		// for File
import java.util.*;		// for scanner

public class strMatch{
	final static boolean DEBUG = !true;
	static File patternFile;
	static File sourceFile;
	static File outFile;
	
	public static void main(String[] args) throws Exception
	{
		patternFile = new File(args[0]);
		sourceFile = new File(args[1]);
		outFile = new File(args[2]);
		boolean found = false;
		String result = "";
		
		// input
		Scanner sc = new Scanner(patternFile);
		sc.useDelimiter("&\n?&?");
		
		while(sc.hasNext() ){		// run for each pattern
			// find pattern
			String pattern = sc.next();
			//if(DEBUG) System.out.println("pattern: \"" + pattern + '"');
			
			output("BF", pattern);
			output("RK", pattern);
//			output("KMP", pattern);
//			output("BM", pattern);
		}
		
		sc.close();
	}
	
	static void output(String alg, String pattern) throws Exception{
		boolean found = false;
		
		if(pattern.equals(""))		// empty string pattern
			found = true;
		else if(sourceFile.length() < pattern.length() )		// source text too small
			found = false;
		else if(alg.equals("BF") )
			found = BF(pattern);
		else if(alg.equals("RK") )
			found = RK(pattern);
		else if(alg.equals("KMP") )
			found = KMP(pattern);
		else if(alg.equals("BM") )
			found = BM(pattern);
			
		String result = found ? "PASSED" : "FAILED";
		System.out.println(alg + " " + result + ": " + pattern);
	}
	
	// Brute Force method
	public static boolean BF(String pattern) throws Exception{
		final int pLen = pattern.length();
		BufferedReader in = new BufferedReader(new FileReader(sourceFile));
		String s = "$"; // initialized to dummy char that will be removed later
		char newChar = 0;
		//if(DEBUG) System.out.println("newChar:    \"" + newChar + '"');
		assert(sourceFile.length() >= pLen) : "Pattern too large for file.";
		
		// initialize s
		while(s.length() < pLen) {
			s += (char)in.read();
		}
		
		for(int i = pLen; i < sourceFile.length()-pLen; ++i){
			if(DEBUG) System.out.println("s:    \"" + s + '"');
			newChar = (char)in.read();
			s += newChar;		// update substrings
			s = s.substring(1, pLen+1);
			for(int j = 0; j < pLen; ++j){
				if(s.charAt(j) != pattern.charAt(j) )
					break;		// don't check rest of string
				if(j == pLen-1 )		// all chars matched
					return true;
			}
		}
		
		in.close();
		
		return false;
	}
	
	// Rabin-Karp
	public static boolean RK(String pattern) throws Exception{
		final int pLen = pattern.length();
		BufferedReader in = new BufferedReader(new FileReader(sourceFile));
		String s = "$"; // initialized to dummy char that will be removed later
		int patternHash = hash(pattern);
		int sHash = 0;
		char newChar = 0;
		//if(DEBUG) System.out.println("newChar:    \"" + newChar + '"');
		if(DEBUG) System.out.println("hash(pattern):    \"" + patternHash + '"');
		assert(sourceFile.length() >= pLen) : "Pattern too large for file.";
		
		// initialize s
		while(s.length() < pLen) {
			s += (char)in.read();
		}
		sHash = hash(s);
		
		for(int i = pLen; i < sourceFile.length()-pLen; ++i){
			if(DEBUG) System.out.println("s:    \"" + s + '"');
			newChar = (char)in.read();
			sHash = hash(s, sHash, newChar);		// update hash
			s += newChar;		// update substrings
			s = s.substring(1, pLen+1);
			
			//if(DEBUG) System.out.println("sHash = " + sHash);
			if (patternHash == sHash ){
				//if(DEBUG) System.out.println("hashes matched: " + patternHash + " == " + sHash);
				for(int j = 0; j < pLen; ++j){
					if(s.charAt(j) != pattern.charAt(j) )
						break;		// don't check rest of string
					if(j == pLen-1 )		// all chars matched
						return true;
				}
			}
		}
		
		in.close();
		
		return false;
	}
	
	// initial hash: using simple algorithm. Use base 256?
	static int hash(String s){
		int result = 0;
		
		for(int i = 0; i < s.length(); ++i)
			result += s.charAt(i);
		
		return result;
	}
	
	// update hash
	static int hash(String s, int prevHash, char newChar){
		int result = prevHash;
		
		result -= s.charAt(0);
		result += newChar;
		
		return result;
	}
	
	public static boolean KMP(String pattern) throws Exception{
		System.out.println("KMP not implemented!");
		return false;
	}
	
	public static boolean BM(String pattern) throws Exception{
		System.out.println("BM not implemented!");
		return false;
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
