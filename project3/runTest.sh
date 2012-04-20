# file variables
pattern="Pattern.txt"
source="sampleSource.txt"
outFile="results.txt"

echo COMPILING...
javac strMatch.java

echo EXECTUTING...
java -ea strMatch $pattern $source $outFile
