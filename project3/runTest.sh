# file variables
pattern="pattern.txt"
source="source.txt"
outFile="results.txt"

echo COMPILING...
javac strMatch.java

echo EXECTUTING...
java strMatch $pattern $source $outFile
