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

import java.util.HashMap;		// for branch data struture
import java.util.ArrayList;
// Amber driving now
// swap every ~30 min
class Global{	// for global variables
	static final boolean DEBUG = false;
}
public class LZcoding {
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
			if(Global.DEBUG) System.out.println("Compresssing file: " + file + " ...");
			compress(file);
		}else if(type == 'd'){
			if(Global.DEBUG) System.out.println("Decompresssing file: " + file + " ...");
			decompress(file);
		}
		// Comparing time of execution
		long elapsed = System.currentTimeMillis() - start;
		if(Global.DEBUG) System.out.println("elapsed run time: " + elapsed + "ms");
	}
	
	// Ian driving now
	public static void compress(String file) throws Exception{
		IO.Compressor compressor = new IO.Compressor(file);
		char[] charArray = compressor.getCharacters();		// Convert file to an array of characters
		if(Global.DEBUG) System.out.println("charArray = " + String.valueOf(charArray) );
		// initialize dictionary with root (0, <>)
		int idx = 0;
		final trieNode root = new trieNode(idx, "");
		trie dict = new trie(root);
		++idx;
		trieNode currentNode = root;
		trieNode parent = root;
		// Initial empty string to lookup in the file
		String lookup = "";
		
		
		for(int i = 0; i < charArray.length; ++i){
			lookup += charArray[i];
			if(Global.DEBUG) System.out.println("lookup = " + lookup);
			parent = dict.findParent(lookup);
			
			if( !parent.contains(charArray[i]) ){	// lookup not in trie
				// add to the trie dictionary under the proper parent node
				parent.add(idx, lookup);
				// Run encode on the pair
				compressor.encode(parent.getIndex(), charArray[i] );
				if(Global.DEBUG)System.out.printf("encode pair: (%d, \'%c\')\n",
					parent.getIndex(), charArray[i]);
				lookup = "";
				++idx;
			}else		// lookup already in trie
				continue;		// continue building lookup string
		}
		
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
				String newEntry = dictionary.get(next.getIndex() ) + Character.toString(next.getCharacter());
				dictionary.add(newEntry);
				// Write the new string to file 
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
	// Returns the parent or future parent of the node
	public trieNode findParent (String lookup){
		return findParent(lookup, root);
	}
	// Recursive helper method to search the trie
	public trieNode findParent (String path, trieNode node){
		char target = path.charAt(0);
		
		// Base cases
		if(node.branch.size() == 0)
			return node;		// node is a leaf
		if( !node.branch.containsKey(target) )
			return node;		// target node is not yet a child of this node
		if(path.length() == 1)
			return node;		// node is the parent of the target node
		
		// Recursive case
		return findParent(path.substring(1) , node.branch.get(target) );
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
		if(Global.DEBUG) System.out.println("add node: " + child);

		return child;
	}
	
	public boolean contains(char target){
		return this.branch.containsKey(target);
	}
	
	// Index accessor
	public int getIndex(){
		return idx;
	}
	// Print the node
	public String toString (){		//for debugging
		return "{" + idx + " : \"" + word + "\"}";
	}
}
