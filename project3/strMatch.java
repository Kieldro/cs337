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
time grep Project GutenBerg sampleSource.txt = 29.039 s

*/
import java.io.*;		// for File
import java.util.*;		// for scanner

public class strMatch{
	final static boolean DEBUG = true;
	static File patternFile;
	static File sourceFile;
	static DataInputStream sourceInputStream;
	static File outFile;
	static PrintWriter out = null;
	static final int TWENTYFIVE_MiB = 25 * 1000;//(int)Math.pow(2, 20);
	static byte[] b = new byte[TWENTYFIVE_MiB];
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

		long endTime, elapsedTime, startTime = System.currentTimeMillis();
		try {
			patternFile = new File(args[0]);
			sourceFile = new File(args[1]);

			outFile = new File(args[2]);
			out = new PrintWriter(new FileWriter(outFile));
			boolean found = false;
			String result = "";



			// input
			Scanner sc = new Scanner(patternFile);
			sc.useDelimiter("&(\n&)?");

			while(sc.hasNext() ){		// run for each pattern
				//read up to first 25 mb into b
				//if(DEBUG) System.out.println("b.length = " + b.length);
			
				// find pattern
				String pattern = sc.next();
				if(DEBUG) System.out.println("pattern: \"" + pattern + '"');

				output(Algorithm.BF, pattern);
				output(Algorithm.RK, pattern);
				output(Algorithm.KMP, pattern);
//				output(Algorithm.BM, pattern);
			}
		}finally {
			out.close();
			
			endTime = System.currentTimeMillis();
			elapsedTime = endTime - startTime;
			System.out.println("main() elapsedTime: " + elapsedTime + " ms");
		}
	}

	static boolean readBytes() throws Exception {
		b = new byte[TWENTYFIVE_MiB];
		numBytesRead = sourceInputStream.read(b,0,TWENTYFIVE_MiB);
		// can no longer read any more bytes.
		// I create a new input stream so that it's ready for the next call
		// and to reset the numBytesRead to a positive value.  	
		if (numBytesRead==-1)
			return false;
		return true;
	}

	static boolean readBytes(int offset, byte[] c) throws Exception {
		b = new byte[offset + TWENTYFIVE_MiB];  
		for (int i =0; i < offset; i++){
			//copy in values from passed in array
			b[i] = c[i];
		}
		numBytesRead = sourceInputStream.read(b,offset,TWENTYFIVE_MiB) + offset;
		if (numBytesRead==-1) //can no longer read any more bytes.  I create a new input stream so that it's ready for the next call and to reset the numBytesRead to a positive value.  	
			return false;
		return true;
	}

	static void output(Algorithm alg, String pattern) throws Exception{
		boolean found = false;
		long end, elapsed, start = System.currentTimeMillis();
		//we need to reset the bytes read in for each call.
		FileInputStream sif = new FileInputStream(sourceFile);
		sourceInputStream = new DataInputStream(sif);
		readBytes();

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

		String result = found ? "PASSED" : "FAILED";
		System.out.println(alg.str + " " + result + ": " + pattern);

		end = System.currentTimeMillis();
		elapsed = end - start;
		System.out.println(alg.str + " elapsed: " + elapsed + " ms");
	}

	// Brute Force method
	public static boolean BF(String pattern) throws Exception{
		final int pLen = pattern.length();
		String s = "$"; // initialized to dummy char that will be removed later
		char newChar = 0;
		//if(DEBUG) System.out.println("newChar:    \"" + newChar + '"');
		assert(numBytesRead >= pLen) : "Pattern too large for file.";

		// initialize s
		int z =0;
		while(s.length() < pLen) {
			s += (char)b[z];
			z++;
		}
	
		
		assert(b.length > 0): "Char array b is empty";
		for(int i = pLen; i < numBytesRead; ++i){
			//if(DEBUG) System.out.println("s:    \"" + s + '"');
			newChar = (char)b[i];
			s += newChar;		// update substrings
			s = s.substring(1, pLen+1);
			for(int j = 0; j < pLen; ++j){
				if(s.charAt(j) != pattern.charAt(j) )
					break;		// don't check rest of string
				if(j == pLen-1 )		// all chars matched
					return true;
			}

			if ((i==numBytesRead-1) && readBytes() ) //BACKGUARD
				i=-1;
		}

		return false;
	}

	// Rabin-Karp
	public static boolean RK(String pattern) throws Exception{
		final int pLen = pattern.length();
		boolean hashFunc = true;		// set to true for simple hash function
		String s = "$"; // initialized to dummy char that will be removed later
		int patternHash = hashFunc ? hash(pattern): hashBase(pattern);
		int sHash = 0;
		char newChar = 0;
		//if(DEBUG) System.out.println("newChar:    \"" + newChar + '"');
		//if(DEBUG) System.out.println("hash(pattern):    \"" + patternHash + '"');
		assert(numBytesRead >= pLen) : "Pattern too large for file.";

		// initialize s
		int z=0;
		while(s.length() < pLen) {
			s += (char)b[z];
			z++;
		}
		sHash = hashFunc ? hash(s): hashBase(s);

		for(int i = pLen; i < numBytesRead; ++i){
			if (b[i]==0)
				break; //FRONTGUARD

			//if(DEBUG) System.out.println("s:    \"" + s + '"');
			newChar = (char)b[i];
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
		
			if ((i==numBytesRead-1) && readBytes()) //BACKGUARD
				i=-1;
		}

		return false;
	}

	// initial hash: using simple algorithm.
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

	// initial hash: base 256
	static int hashBase(String s){
		int result = 0;

		for(int i = 0; i < s.length(); ++i)
			result += s.charAt(i) * Math.pow(256, s.length()-i );

		result %= 7;		// mod by prime to reduce hash buckets
		return result;
	}

	// update hash
	static int hashBase(String s, int prevHash, char newChar){
		int result = prevHash;

		result -= s.charAt(0) * Math.pow(256, s.length() );
		result *= 256;
		result += newChar;

		return result;
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

	public static boolean KMP(String p) throws Exception{
		final int pLen = p.length();
		int[] a = computeCores(p);
		String s = "";
		char newChar = 0;
		//if(DEBUG) System.out.println("newChar:    \"" + newChar + '"');
		assert(numBytesRead >= pLen) : "Pattern too large for file.";

		int r =0;
		for (int i = 0; i < numBytesRead; ++i) {
			if (b[i]==0)
				break;  //we add this guard.  it tells us that no byte has been read into this location, and so we are done with the array.


			newChar = (char) b[i];
			s += newChar;
			// System.out.println("i: " + i + "    s: " + newChar);
			if (s.charAt(r) == p.charAt(r) && r == p.length()-1){
				return true;
			}

			if (s.charAt(r)==p.charAt(r)) {
				r++;
			}
			else if (s.charAt(r)!=p.charAt(r) && (r==0)) {
				s="";
			}
			else if (s.charAt(r)!=p.charAt(r) && r>0) {
				r=a[s.length()-1];
				s = s.substring(s.length()- a[s.length()-1]);
			}


			if ((i==numBytesRead-1) && readBytes())
				i=-1;   //this resets us in the for loop.  we set it to -1 b/c ++i will increment it back to 0.
		}

		return false;
	}
	
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

	static boolean BM(String pattern) throws Exception{
		char[] p = pattern.toCharArray();
		//precomputations
		int[] rt = badSymbol(pattern);
		int[] f = computeCores(pattern);//1-indexed
		int[] s = goodSuffix (pattern, f);

		//SUFFIX ARRAY NEEDED

		int j = p.length;
		int l = 0;
		int old_l=0;

		while (l<=(numBytesRead-p.length)) { //do i need to subtract the length of p??
			while (/*(l+j-1) < numBytesRead &&*/ j>0 && p[j-1] == b[l+j-1]) {
				j--;
			}
			if (j==0)
				return true;
			else {
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
