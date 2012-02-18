NAMES: Ian Buitrago, Amber Cooper
UT EIDS: ib, alc959
SECTIONS: 52945 52973
CODING STATUS: Everything works;
NOTE ON PERFORMANCE: Decompress if implemented with the trie structure dictionary
as explicitly stated in the project requirements, but the runtime is far slower
than if the decompression dictionary data structure is an Arraylist. I've left the
Arraylist implementation commented out.

*Explanation on why the compression fails on the larger.txt*

The pairs in the cpz aren't reused enough to reduce the size of larger.txt.
For example, if there are only 3 characters in the uncompressed file,
the pairs only relate 2 or 3 characters to each other. Whereas in the in smaller.txt,
compressions works because the pairs use the dictionary many times.
