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
4/21 1 - 11p  Ian, John 20 hrs

Total time 31 hrs, 28 hrs of pair programing

NOTES:
run with: source runTest.sh
time grep -m 1 '78:25 Man did eat angels' bible.txt

turnin --submit sarat project3 strMatch.java emails.txt readme.txt report.pdf
issues:
	- case: when pattern is longer than chunk size
	- 2 empty patterns
*/
import java.io.*;		// for File
import java.util.*;		// for scanner

public class strMatch{
	final static boolean DEBUG = !true;
	final static boolean TIME = true;		// set to true for performance info
	static File patternFile;
	static File sourceFile;
	static DataInputStream sourceInputStream;
	static File outFile;
	static PrintWriter out = null;
	static final int NUM_BYTES = 25 * (int)Math.pow(2, 20);		// 25 MiB
	static byte[] b;
	static int numComparisons;
	static int numBytesRead; //tells us how far to read into the byte array.

	public static enum Algorithm{
		BF("BF"), RK("RK"), KMP("KMP"), BM("BM");

		String str;

		Algorithm(String s){
			str = s;
		}
	}

	public static void main(String[] args) throws Exception
	{   
		double endTime, elapsedTime, startTime = System.currentTimeMillis();
		boolean[] results = new boolean[4];
		
		try {
			patternFile = new File(args[0]);
			sourceFile = new File(args[1]);
			outFile = new File(args[2]);
			//if(DEBUG) System.out.println("sourceFile.length(): " + (int)sourceFile.length());
			out = new PrintWriter(new FileWriter(outFile));
			b = new byte[NUM_BYTES];//(int)sourceFile.length()];

			// input patterns
			Scanner sc = new Scanner(patternFile);
			sc.useDelimiter("&(\n&)?(\n)?");

			while(sc.hasNext() ){		// run for each pattern
				//if(DEBUG) System.out.println("b.length = " + b.length);
			
				// find pattern
				String pattern = sc.next();
				if(DEBUG) System.out.println("pattern,   \"" + pattern + '"');
				
				// run algorithms
				int rLen = 0;
				results[rLen] = output(Algorithm.BF, pattern); ++rLen;
				results[rLen] = output(Algorithm.RK, pattern); ++rLen;
				results[rLen] = output(Algorithm.KMP, pattern); ++rLen;
				results[rLen] = output(Algorithm.BM, pattern); ++rLen;
				if(DEBUG) System.out.println();
				
				// check that all algorithms returned same result
				boolean sameValue = true;
				for(int i = 0; sameValue && i < rLen-1; ++i)
					sameValue = sameValue && (results[i] == results[i+1]); 
				assert (sameValue):
					"Algorithms returning different results for pattern: " + pattern;
			}
		}finally {
			out.close();
			
			endTime = System.currentTimeMillis();
			elapsedTime = endTime - startTime;
			elapsedTime /= 1000;	// convert from ms to seconds
			if(TIME) System.out.println("main() elapsedTime: " + elapsedTime + " sec");
		}
	}

	static boolean output(Algorithm alg, String pattern) throws Exception{
		numComparisons =0;
		boolean found = false;
		double end, elapsed, start = System.currentTimeMillis();
		//we need to reset the bytes read in for each call.
		sourceInputStream = new DataInputStream(new FileInputStream(sourceFile) );
		readBytes();
		//if(DEBUG) System.out.println("b:         \"" + new String(b) + '"');

		if(pattern.length() == 0)		// empty string pattern
			found = true;
		else if(sourceFile.length() < pattern.length() )		// source text too small
			found = false;
		else if(alg == Algorithm.BF )
			found = BF(pattern);
		else if(alg == Algorithm.RK)
			found = RK(pattern);
		else if(alg == Algorithm.KMP)
			found = KMP(pattern);
		else if(alg == Algorithm.BM)
			found = BM(pattern);
		System.out.println("numComparisons: " + numComparisons);
		String result = found ? "MATCHED" : "FAILED";
		//if(DEBUG)System.out.println(alg.str + " " + result + ": " + pattern);
		out.println(alg.str + " " + result + ": " + pattern);

		end = System.currentTimeMillis();
		elapsed = (end - start);
		elapsed /= 1000;		// convert from ms to seconds
		if(TIME) System.out.println(alg.str + ", " + elapsed + ", sec");
		
		return found;
	}

	// Input functions
	static boolean readBytes() throws Exception {
		b = new byte[NUM_BYTES];
		numBytesRead = sourceInputStream.read(b, 0, NUM_BYTES);
		// can no longer read any more bytes.
		// I create a new input stream so that it's ready for the next call
		// and to reset the numBytesRead to a positive value.  	
		if (numBytesRead==-1)
			return false;
		return true;
	}

	static boolean readBytes(int offset, byte[] c) throws Exception {
		b = new byte[offset + NUM_BYTES];  
		for (int i =0; i < offset; i++){
			//copy in values from passed in array
			b[i] = c[i];
		}
		numBytesRead = sourceInputStream.read(b, offset, NUM_BYTES) + offset;
		if (numBytesRead==-1) //can no longer read any more bytes.  I create a new input stream so that it's ready for the next call and to reset the numBytesRead to a positive value.  	
			return false;
		return true;
	}

	// Brute Force method
	public static boolean BF(String pattern) throws Exception{
		final int P_LEN = pattern.length();
		StringBuilder alignment = new StringBuilder("$"); // initialized to dummy char that will be removed later
		char newChar = 0;
		long collisions = 0;
		assert(numBytesRead >= P_LEN) : "Pattern too large for file.";

		// initialize s
		for(int i = 0; alignment.length() < P_LEN; ++i)
			alignment.append((char)b[i]);		// StringBuidler
		
		//if(DEBUG) System.out.println("initalign: \"" + alignment + '"');
		assert(b.length > 0): "Char array b is empty";
		for(int i = P_LEN-1; i < numBytesRead; ++i){
			assert(newChar <= 127):
				"Text contains characters with ascii values > 127: "+(int)newChar;
			newChar = (char)b[i];
			//if(DEBUG) System.out.println("newChar:   \"" + newChar + '"');
			alignment.deleteCharAt(0);
			alignment.append(newChar);		// StringBuidler
			//if(DEBUG) System.out.println("alignment: \"" + alignment + '"');
			for(int j = 0; j < P_LEN; ++j){
				++numComparisons;
				if(alignment.charAt(j) != pattern.charAt(j) )
					break;		// don't check rest of string
				if(j == P_LEN-1 ){		// all chars matched
					return true;
				}
			}

			if ((i==numBytesRead-1) && readBytes() ) //BACKGUARD
				i=-1;
		}

		return false;
	}

	// Rabin-Karp
	public static boolean RK(String pattern) throws Exception{
		final int P_LEN = pattern.length();
		StringBuilder alignment = new StringBuilder("$"); // initialized to dummy char that will be removed later
		char newChar = 0;
		long collisions = 0;
		boolean result = false;
		//if(DEBUG) System.out.println("newChar:    \"" + newChar + '"');
		//if(DEBUG) System.out.println("hash(pattern):    \"" + patternHash + '"');
		assert(numBytesRead >= P_LEN) : "Pattern too large for file.";
		// initialize s
		for(int i = 0; alignment.length() < P_LEN; ++i)
			alignment.append((char)b[i]);
		
		int patternHash = hash(new StringBuilder(pattern) );
		int alignHash = hash(alignment);
		
		outLoop:
		for(int i = P_LEN-1; i < numBytesRead; ++i){
			//if(DEBUG) System.out.println("s:    \"" + s + '"');
			newChar = (char)b[i];
			alignHash = hash(alignment, alignHash, newChar);
			alignment.deleteCharAt(0);
			alignment.append(newChar);
			
			//if(DEBUG) System.out.println("alignHash = " + alignHash);
			if (patternHash == alignHash ){
				++collisions;
				//if(DEBUG) System.out.println("hashes matched: " + patternHash + " == " + alignHash);
				for(int j = 0; j < P_LEN; ++j){
					++numComparisons;
					if(alignment.charAt(j) != pattern.charAt(j) )
						break;		// don't check rest of string
					if(j == P_LEN-1 ){		// all chars matched
						result = true;
						break outLoop;
					}
				}
			}
		
			if ((i==numBytesRead-1) && readBytes() ) //BACKGUARD
				i=-1;
		}

		if(DEBUG) System.out.println("RK, collisions, " + collisions);
		return result;
	}

	// initial hash: using simple algorithm.
	static int hash(StringBuilder s){
		int result = 0;
		
		for(int i = 0; i < s.length(); ++i)
			result += s.charAt(i);

		return result;
	}

	// update hash
	static int hash(StringBuilder s, int prevHash, char newChar){
		int result = prevHash;

		result -= s.charAt(0);
		result += newChar;
		//result %= 257;
		
		return result;
	}

	// initial hash: base 256
	static int hashBase(StringBuilder s){
		int result = 0;
		int mod = 257;	//31, 127, 518

		for(int i = 0; i < s.length(); ++i)
			result = result + (int)((s.charAt(i) * Math.pow(256, s.length()-i) ) % mod);

		//result %= mod;		// mod by prime to reduce hash buckets
		return result;
	}

	// update hash
	static int hashBase(StringBuilder s, int prevHash, char newChar){
		int result = prevHash;
		int mod = 257;
		
		result -= (s.charAt(0) * Math.pow(256, s.length()) ) % mod;
		result *= 256;
		result += newChar;

		return result;
	}

	public static int[] computeCores(String pattern) {
		int m = pattern.length();
		char[] p = new char[m +1];
		int[] f = new int[m+1];

		for (int i = 0; i < pattern.length(); ++i) {
			p[i+1] = pattern.charAt(i);
		}
		f[0] = 0;
		f[1] = 0;

		for (int j = 2; j<=m; ++j) {
			int k = f[j-1];
			while (k>0 && p[j] != p[k+1]) {
				k = f[k];
			}
			if (k==0 && p[j] != p[k+1])
				f[j] =0;
			else
				f[j] = k +1;

		}
		return f;
	}


	// public static boolean KMP(String pattern) throws Exception{
	public static boolean KMP(String pattern) throws Exception{
		final int P_LEN = pattern.length();
		int[] a = computeCores(pattern);
		StringBuilder alignment = new StringBuilder();
		char newChar = 0;
		//if(DEBUG) System.out.println("newChar:    \"" + newChar + '"');
		assert(numBytesRead >= P_LEN) : "Pattern too large for file.";
	
		int r =0;
		for (int i = 0; i < numBytesRead; ++i) {
			if (b[i]==0)
				break;
	
			newChar = (char) b[i];
			alignment.append(newChar);
			// System.out.println("i: " + i + "    alignment: " + alignment);
			if (r == P_LEN-1 && alignment.charAt(r) == pattern.charAt(r) ){
				return true;
			}
	
			if (alignment.charAt(r) == pattern.charAt(r) ) {
				r++;
				numComparisons++;
			}
			else if (r == 0) {
				numComparisons++;
				alignment = new StringBuilder();
			}
			else if (r > 0) {
				numComparisons++;
				r = a[alignment.length()-1];
				alignment.deleteCharAt(0);
			}
	
			if ((i == numBytesRead-1) && readBytes() )
				i =- 1;   //this resets us in the for loop.  we set it to -1 b/c ++i will increment it back to 0.
		}
	
		return false;
	}
	
	

	// bad symbol heuristic
	static int[] badSymbol(String pattern) {
		char[] p = pattern.toCharArray();
		int[] rt = new int[128];
		for (int i=0; i < rt.length; ++i) {
			rt[i]=-1;
		}	

		for (int i =0; i < p.length; ++i) {
			rt[(int)p[i]] =  i;
		}

		return rt;
	}
	
	// good suffix heuristic
	static int[] goodSuffix(String pattern, int[] f) {
		char[] p = pattern.toCharArray();
		int initVal= p.length - f[p.length];
		int[] s = new int[p.length + 1];

		//initialize array to the |p|-|c(p)|
		for (int i = 0 ; i< s.length; ++i) {
			s[i] = initVal;
		}
		s[0] =1; //it's always 1 for epsilon case
		int coreLength = 0;
		int formula = 0;
		for (int i = 1; i<s.length; ++i) {
			//get core of string.
			coreLength = f[i];
			formula = i-coreLength; //|v| - |c(v)|
			// System.out.println("formula: " + formula);
			if (formula<s[coreLength])
				s[coreLength] = formula;
		}
		return s;
	}

	// Boyer-Moore
	static boolean BM(String pattern) throws Exception{
		char[] p = pattern.toCharArray();
		//precomputations
		int[] rt = badSymbol(pattern);
		int[] f = computeCores(pattern);		//1-indexed
		int[] s = goodSuffix (pattern, f);

		int j = p.length;
		int l = 0;
		int old_l=0;

		while (l<=(numBytesRead-p.length)) {
			j = p.length;
			while (/*(l+j-1) < numBytesRead &&*/ j>0 && p[j-1] == b[l+j-1]) {
				numComparisons++;
				j--;
			}
			if (j==0)
				return true;
			else {
				numComparisons++;
				old_l=l;
				l += Math.max(j-1-rt[(int)b[l+j-1]], s[p.length-j]);
			}

			//the following is our guard for reading in more bytes. It's complicated by the fact 
			//that I might need to keep characters from the previous byte of arrays before reading in more. 
			if (l==numBytesRead && readBytes()) {
				l=0;
				j= p.length;
			}
			else if (l >(numBytesRead-p.length) && l<numBytesRead) {
				int offset = numBytesRead-l;
				byte[] c = new byte[offset];
				for (int i =0; i < offset; ++i) {
					c[i] = b[l];
					l++;
				}
				if (readBytes(offset, c) ) { //reset the values
					l=0;
					j = p.length;
				}
			}
		}
		return false;
	}
}
