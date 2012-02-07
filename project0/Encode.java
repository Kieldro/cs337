/*
Ian Buitrago: Slip days used for this project: 0  Slip days used (total): 0
ib
1-26-2012
CS 337
discussion: Th 9a
project 0

notes:
java source
run with:
javac Encode.java; java Encode
check with: diff project0.enc project0test.enc
*/

import java.io.*;

public class Encode{		// java: everything is in a class
	static final boolean DEBUG = false;
	
	public static void main(String[] args) throws Exception{		// throws because of file io
		FileReader inFile = new FileReader("project0.txt");
		FileOutputStream outFile = new FileOutputStream("project0.enc");
		int character;
		
		while ( -1 != (character = inFile.read()) ){		// inputs a char
			if(DEBUG) System.out.println( (char) character);
			if(DEBUG) System.out.println( String.format("0x%1$X", character) );
			
			// count 1 bits and determine parity
			boolean odd = false;
			for(int i = 0; i < 7; ++i){
				int c = character;
				c >>= i;
				c &= 01;		// octal notation
				if(c == 1)
					odd = !odd;
			}
			
			// set bit 8th bit if odd number of 1 s
			if(odd){
				character |= 0x80;		// add parity bit
				if(DEBUG) System.out.println( "Parity bit added." );
				if(DEBUG) System.out.println( String.format("0x%1$X", character) );
				if(DEBUG) System.out.println( (char) character);
			}else
				if(DEBUG) System.out.println( "byte unchanged." );
			
			if(DEBUG) System.out.println( );
			
			outFile.write(character);
		}
		
		inFile.close();
		outFile.close();
	}
}
