/*
Ian Buitrago: Slip days used for this project: 0  Slip days used (total): 1
John Richter: Slip days used for this project: 0  Slip days used (total): 2
CS 337
project 3
4/30/2012

Pair programming log (> 90% paired)
4/16 1 - 3p  Ian 2 hr
4/17 6 - 7p  John, 1 hr
4/17 7 - 10p  Ian, John, 6 hr
4/18 9:30p - 10:30p Ian, John, 2 hrs

Total time 11 hrs, 10 hrs of pair programing

NOTES:
run with: source runTest.sh

*/
import java.io.*;		// for File
import java.util.*;		// for scanner

public class strMatch{
	final static boolean DEBUG = true;
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
		
		while(sc.hasNext() ){
			// find pattern
			sc.useDelimiter("&\n?&?");
			String pattern = sc.next();
			//if(DEBUG) System.out.println("pattern: \"" + pattern + '"');
			
			//if(DEBUG) System.out.println("pattern[-1]: \"" + (int)pattern.charAt(pattern.length()-1) + '"');
		
		/*	if (sourceFile.length() < pattern.length() )		// text is too small
				found = false;
	*/	
/*			found = BF(pattern);
			result = found ? "PASSED" : "FAILED";
			System.out.println("BF " + result + ": " + pattern);
*/	
			found = RK(pattern);
			result = found ? "PASSED" : "FAILED";
			System.out.println("RK " + result + ": " + pattern);
//			KMP();
	//		BM();
		}
		
		sc.close();
	}
	
	// Brute Force method
	public static boolean BF(String pattern) throws Exception
	{
		Scanner sc = new Scanner(sourceFile);
		String text = sc.nextLine();
		//if(DEBUG) System.out.println("text:    \"" + text + '"');
		
		
		
		for(int i = 0; i < sourceFile.length(); ++i){
			for(int j = 0; j < pattern.length() && i + j < text.length(); ++j){
				if(text.charAt(i + j) != pattern.charAt(j) )
					break;
				if(j == pattern.length()-1 )		// all chars matched
					return true;
			}
		}
		
		sc.close();
		
		return false;
	}
	
	// Rabin-Karp
	public static boolean RK(String pattern) throws Exception
	{
		final int pLen = pattern.length();
		BufferedReader in = new BufferedReader(new FileReader(sourceFile));
		String s = "";
		int patternHash = hash(pattern);
		int sHash = 0;
		char newChar = 0;
		//if(DEBUG) System.out.println("newChar:    \"" + newChar + '"');
		//if(DEBUG) System.out.println("hash(pattern):    \"" + patternHash + '"');
		assert(sourceFile.length() >= pLen) : "Pattern too large for file.";
		
		// initialize s
		while(s.length() < pLen-1) {
			s += (char)in.read();
		}
		sHash = hash(s);
		
		for(int i = pLen; i < sourceFile.length()-pLen; ++i){
			if(DEBUG) System.out.println("s:    \"" + s + '"');
			newChar = (char)in.read();
			s += newChar;
			s = s.substring(1, pLen);
			sHash = hash(s, sHash, newChar);		// update hash
			
			if (patternHash == sHash )
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
	
	// initial hash
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
	
	public static void KMP(){
		
		
	}
	
	public static void BM(){
		
		
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
