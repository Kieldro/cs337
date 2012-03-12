/*
Ian Buitrago: Slip days used for this project: 0  Slip days used (total): 1
Dan Jackson: Slip days used for this project: 0  Slip days used (total): 0
CS 337
project 2
3/23/2012

Pair programming log (> 90% paired)
3/8 4:30 - 5:30p  Ian, Dan 2 hr
3/9 6 - 7p  Dan, 1 hr
3/9 7 - 10p  Ian, Dan, 6 hr
3/11 9:30p - 10:30p Ian, Dan, 2 hrs

Total time 11 hrs, 10 hrs of pair programing

*/
import java.io.*;		// for File
import java.util.*;		// for scanner

public class RSA{
	final static boolean DEBUG = false;
	public static void main(String[] args) throws Exception{
		String arg = args[0];
		
		if (arg.equals("key") ){
			long p = Long.valueOf(args[1]);
			long q = Long.valueOf(args[2]);
			genKey(p, q);
			
		}else if (arg.equals("encrypt") ){
			File inputFile = new File(args[1]);
			File keyFile = new File(args[2]);
			File outputFile = new File(args[3]);
			cipher(inputFile, keyFile, outputFile, false);
			
		}else if (arg.equals("decrypt") ){
			File inputFile = new File(args[1]);
			File keyFile = new File(args[2]);
			File outputFile = new File(args[3]);
			cipher(inputFile, keyFile, outputFile, true);
			
		}else
			System.out.println("Invalid arguement: " + arg);
	}
	
	public static void cipher(File inputFile, File keyFile, File outputFile, boolean decrypt) throws Exception
	{
		if(DEBUG) System.out.println(decrypt ? "DECRYPTING..." : "ENCRYPTING...");
		long n = 0;
		long e = 0;
		long d = 0;
		long m = 0;		// message block
		
		// input RSA key
		Scanner sc = new Scanner(keyFile);
		n = sc.nextLong();
		e = sc.nextLong();
		d = sc.nextLong();
		if (DEBUG) System.out.println("n, e, d");
		if (DEBUG) System.out.println(n + " " + e + " " + d);
		sc.close();
		
		// to encrypt 3 bytes, n must be > 2^24
		assert(n > Math.pow(2, 24) ): "n must be greater than 2^24.";
		// n
		assert(n < Math.pow(2, 30) ): "n must be less than 2^30.";
		
		DataInputStream in = new DataInputStream(new FileInputStream(inputFile) );
		DataOutputStream out = new DataOutputStream(new FileOutputStream(outputFile) );
		
//		try{
		while(in.available() > 0){
			
			// concatenate 3|4 bytes into a long
			m = 0;
			int i = decrypt ? 3 : 2;
			for(; i >= 0 && in.available() > 0; --i){
				long inByte = (long)in.readByte() & 0x0FF;		// acount for sign extension
				// debug output in hex and decimal
				if(DEBUG) System.out.println(String.format(" inByte = 0x%1$X, %1$d", inByte) );
				m = (inByte << i*8) | m ;		//  shift byte then or into m
			
			}
			if(DEBUG) System.out.println(String.format("m = 0x%1$X, %1$d", m) );
			
			// encrypt/decrypt block
			long key = decrypt ? e : d;
			long c = exponentiation(m, key, n);		// ciphertext
			if(DEBUG) System.out.println(String.format("c = 0x%1$X, %1$d", c) );
			
			if (decrypt){
				int dc = (int)c;		//  cast to int
				out.writeByte( dc >> 16 );		// write high byte
				if(in.available() > 0 || (dc & 0x0000FFFF) != 0)		// only write high byte
					out.writeByte( dc >> 8 );		//  write 2nd byte
				if (in.available() > 0 || (dc & 0x0FF) != 0 )
					out.writeByte(dc);		// write low byte
			// Encrypt output
			}else		// encrypt always writes all 4 bytes
				out.writeInt( (int)c );
			
		}
/*		}catch (Exception ex)
			if(DEBUG) System.out.println("End of file.");
*/		
		in.close();
		out.close();
	}
	
	public static void genKey(long p, long q){
		assert(isPrime(p) && isPrime(q) ): "p and q must both be prime.";
		
		long n = p * q;
		long phi = (p-1)*(q-1);
		long x = 3;
		long e = calce(phi, n);
		if(e < 2){
			System.out.println("***ERROR*** Could not calculate e");
			return;}
		long[] solution = euclid(e, phi);
		long d = solution[1];
		
		while(d < 0) d+= phi;
		
		System.out.println(n + " " + e + " " + d);
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

	public static long gcd(long y, long z){
		if(z == 0) return y;
		return gcd(z, y % z);
	}

	public static long[] euclid(long p, long q){
		if(q == 0) return p;
		long sol = euclid(q, p % q);
		return sol;
		
		
	}

	public static long calce(long phi, long n){
		for(long x = 3; x < n; x+=2){
			if(gcd(phi, x) == 1) return x;		
		}
		return 0;

	}
	
	public static boolean isPrime(long x){
		if(x % 2 == 0) return false;
		for(int i = 3; i*i <= x; i += 2)
			if(x%i == 0) return false;
		return true;
	}	
}
