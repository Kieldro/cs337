
# file variables
pattern="pattern.txt"
source="source.txt"
outFile="results.txt"

echo compiling...
javac strMatch.java

echo running...
java strMatch $pattern $source $outFile

#echo comparing $original and $decrypted...
#cmp -lc $encrypted test2.enc
