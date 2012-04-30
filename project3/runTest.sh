# file variables
pattern="biblePattern.txt"
source="bible.txt"
outFile="results.txt"
compile=true
grep=false
noError=true

if $compile; then
	echo COMPILING...
	javac strMatch.java
	noError=([ $? == 0 ])
fi

if $noError; then		# don't exectute if there were compilation errors
	echo COMPILED successfully.
	echo EXECTUTING...
	java -ea strMatch $pattern $source $outFile
fi

if $grep; then
	echo GREP...
	time grep -m 1 '78:25 Man did eat angels' $source
fi
