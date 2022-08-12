
## Defines static functions that are used many times in the server
import hashlib

# Returns a string representation of the md5 hash of a supplied input string when combined with a salt string
def hash(information : str, salt : str = "") -> str:
	str2hash = information + salt
	return hashlib.md5(str2hash.encode()).hexdigest()