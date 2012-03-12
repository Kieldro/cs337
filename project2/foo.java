import java.io.*;		// for File
import java.util.*;

public class foo{

	public static void main(String[] args) throws FileNotFoundException{
		DataInputStream in = new DataInputStream(new FileInputStream(new File(args[0])));
		try{
			while(in.available() > 0){
				System.out.println(String.format("m = 0x%1$X, %1$d", in.readByte()));
			}
		}
		catch(IOException e){}
	}


}
