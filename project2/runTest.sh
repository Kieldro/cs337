
# file variables
original="test1"
encrypted="test1.dec" #$original".enc"
decrypted="test1.dec" #$original".dec"
key="key"
genKey=false
encrypt=true
decrypt=true

echo compiling...
javac RSA.java

if $genKey; then
	echo generating key...
	java -ea RSA key 4513 5101 > $key
fi

# test encrypt and decrypt
if $encrypt; then
	echo encrypting $original to $encrypted...
	java -ea RSA encrypt $original $key $encrypted
fi

if $decrypt; then
	echo decrypting $encrypted to $decrypted...
	java -ea RSA decrypt $encrypted $key $decrypted
fi

echo comparing $original and $decrypted...
cmp -l -c $original $decrypted
