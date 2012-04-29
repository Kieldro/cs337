# file variables
pattern="biblePattern.txt"
source="bible.txt"
outFile="results.txt"

echo COMPILING...
javac strMatch.java

echo EXECTUTING...
java -ea strMatch $pattern $source $outFile

#echo GREP...
#time grep Project GutenBerg $source
