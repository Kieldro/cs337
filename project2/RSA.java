/*
Ian Buitrago: Slip days used for this project: 0  Slip days used (total): 1
Dan Jackson: Slip days used for this project: 0  Slip days used (total): 0
CS 337
project 2
3/23/2012

Pair programming log (> 90% paired)
3/8 4:30 - 5:30p  Ian, Dan 2 hr
3/11 7 - 8p  Dan, 1 hr
3/14 2:30p - 5:30p Ian, Dan, 6 hrs

Total time 23 hrs, 19 hrs of pair programing

*/




public class RSA{
	final static boolean DEBUG = true;
	public static void main(String[] args){
		String arg = args[0];
		if (arg.equals("key") ){
			long p = Long.valueOf(args[1]);
			long q = Long.valueOf(args[2]);
			genKey(p, q);
		}else
			System.out.println("Invalid arguement: " + arg);
		
		
	}
	
	public static void genKey(long p, long q){
		if (!isPrime(p) || !isPrime(q) ){
			System.out.println("p and q must both be prime.");
			return;
		}
		
		long n = p * q;
		long phi = (p-1)*(q-1);
		long x = 3;
		long e = calce(phi, n);
		if(e < 2){
			System.out.println("***ERROR*** Could not calculate e");
			return;}
		long[] solution = euclid(e, phi);
		long d = solution[1];

		
		
		if (DEBUG) System.out.println("n, e, d");
		System.out.println(" " + n + " " + e + " " + d);
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
		if(q == 0) return new long[]{p, 1, 0};
		long[] sol = euclid(q, p % q);
		return new long[] {sol[0], sol[2], sol[1] - ((p / q) * sol[2])};
		
		
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
