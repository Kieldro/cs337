# file variables
pattern="Pattern.txt"
source="Source.txt"
outFile="results.txt"

echo COMPILING...
javac strMatch.java

echo EXECTUTING...
java -ea strMatch $pattern $source $outFile

#echo GREP...
#time grep Project GutenBerg sampleSource.txt
