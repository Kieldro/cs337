/*
Ian Buitrago: Slip days used for this project: 1  Slip days used (total): 1
Amber Cooper: Slip days used for this project: 1  Slip days used (total): 1

Pair programming log (> 80% paired)
2/14 1a-4a  Ian, 3 hr
2/15 7-10p Ian, Amber, 6 hrs
2/16 4-10p Ian, Amber, 12 hrs

Total time 21 hrs, 18 hrs of pair programing

Challenges: Converting the first argument from string to character
(Amber) Creating the trie data structure.
(Ian) Understanding the LZ algorithm 

Learned: Encoding/ decoding
More Java API

Notes:
java version "1.6.0_31"
run with commands:
javac LZcoding.java
java -ea LZcoding c test
java -ea LZcoding d test.cpz

To compare the files: 
cmp test test.cpz.dcz
*/

import java.util.HashMap;
import java.util.ArrayList;
// Amber driving now
// swap every ~30 min
public class LZcoding {
	
	static final boolean DEBUG = false;
	public static void main(String[] args) throws Exception{
		// measure elapsed time
		long start = System.currentTimeMillis();

		// assertion
		assert(args.length == 2);
		assert(args[0].charAt(0) == 'c' || args[0].charAt(0) == 'd');
		assert(args[0].length() == 1);

		// testing args[]
		char type = args[0].charAt(0);
		String file = args[1];

		// Compress or decompress file based on the input
		if(type == 'c'){
			if(DEBUG) System.out.println("Compresssing file: " + file + " ...");
			compress(file);
		}else if(type == 'd'){
			if(DEBUG) System.out.println("Decompresssing file: " + file + " ...");
			decompress(file);
		}
		// Comparing time of execution
		long elapsed = System.currentTimeMillis() - start;
		if(DEBUG) System.out.println("elapsed run time: " + elapsed + "ms");
	}
	
	// Ian driving now
	public static void compress(String file) throws Exception{
		// IO compressor on the file
		IO.Compressor compressor = new IO.Compressor(file);
		// Convert file to an array of characters
		char[] charArray = compressor.getCharacters();
		if(DEBUG) System.out.println("charArray = " + String.valueOf(charArray) );
		// initialize dictionary with root / <> empty string
		int idx = 0;
		final trieNode root = new trieNode(idx, "");
		trie dict = new trie(root);
		++idx;
		// Initial empty string to lookup in the file
		String lookup = "";
		// For every character in the array of characters, run encode
		// Amber driving now
		for (int i = 0; i < charArray.length; ++i){
			// Append the next character to the lookup string
			lookup += charArray[i];
			if(DEBUG) System.out.println("lookup = " + lookup);
			trieNode parent = dict.contains(lookup);
			if(DEBUG) System.out.println("parent = " + parent);
			
			// If the new string formed is not in the dictionary
			// and if the string is just a character
			if(parent != null){
				// Create a transmission node based on the counter and that character
				// Add that node to the dictionary trie
				parent.add(idx, lookup);
				// Run encode on that character with index 0
				compressor.encode(parent.getIndex(), charArray[i]);
				lookup = "";
				++idx;					
			}
			// Else the entry already exists, so we must add another char to lookup
			
		}
	// Finalize compressor
	compressor.finalize();
	}

	public static void decompress(String file) throws Exception{
		// Initialize decompressor and the arrayList that will serve
		// as the dictionary.
		IO.Decompressor io = new IO.Decompressor(file);
		ArrayList<String> dictionary = new ArrayList<String>();
		dictionary.add("Foo String, since dictionary[0] will never be user");

		// Get the first pair and start the counter
		IO.Pair next = io.decode();
		int counter = 1;
		// While that pair is valid
		while (next.isValid()){
			// If the index is zero
			if(next.getIndex() == 0){
				// That character is added to the dictionary
				String newEntry = Character.toString(next.getCharacter());
				dictionary.add(newEntry);
				// Write the new string to file
				io.append(newEntry);
				counter++;
			}
			// Else the index > 0, which means that that character is already in the dictionary
			else {
				// The new string is the string that is at dictionary[index] + the new character from the pair
				// Add the new enry to the dictionary
				String newEntry = dictionary.get(next.getIndex()) + Character.toString(next.getCharacter());
				dictionary.add(newEntry);
				// Writte the new string to file 
				io.append(newEntry);
				counter++;
			}
			// Get the next pair
			next = io.decode();      
		}
		// Finalize the decompressor
		io.finalize();
	}
}
// Ian driving

// Trie class
class trie{
	private trieNode root;
	// Constructor
	public trie(trieNode node){
		root = node;
	}
	// If the dictionary contains the word
	// return that node's index, else return null
	public trieNode contains (String s){
		return contains(s, root);
	}
	// Recursive helper method to search the trie
	public trieNode contains (String s, trieNode node){
		char target = s.charAt(0);
		// Base cases
		if(!node.branch.containsKey(target))
			return node;
		else if(s.length() == 1)
			return null;
		//Recursive case
		else
			return contains(s.substring(1), node.branch.get(target));
	}
}
// The trieNode holds the word and the index of that word in the dictionary
// and a HashMap of links and children trieNodes
class trieNode{
	private String word;
	private int idx;
	HashMap<Character, trieNode> branch;
	// Constructor
	public trieNode(int i, String s){
		word = s;
		idx = i;
		branch = new HashMap<Character,trieNode>();
	}
	// Add a child trieNode to this node and return the child
	// We're not using the returned child, but it's just convention
	public trieNode add (int i, String s){
		trieNode child = new trieNode(i, s);
		Character c = new Character(s.charAt(s.length()-1));
		branch.put(c, child); 

		return child;
	}
	// Index accessor
	public int getIndex(){
		return idx;
	}
	// Print the node
	public String toString (){		//for debugging
		return "(" + idx + ", \"" + word + "\")";
	}
}
