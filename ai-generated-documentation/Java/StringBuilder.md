![Capgemini Logo](https://www.capgemini.com/wp-content/themes/capgemini2020/assets/images/logo.svg)

### Make it real.

---
## Overview

This code defines a `StringBuilder` class that provides functionality for efficiently building and manipulating strings in a mutable manner. It offers methods for appending characters, strings, and other data types, inserting text at specific positions, replacing substrings, trimming whitespace, converting to uppercase or lowercase, splitting strings, and more. 

## Package/module name

This code belongs to the `java.lang` package.


## Class/file name

The class name is `StringBuilder`.



## Detailed Documentation

###  Constructor Methods:
* **`StringBuilder(String value, int startIndex, int length, int capacity)`**: Initializes a new StringBuilder with a specified string, starting index, length, and initial capacity. 
* **`StringBuilder(String value, int capacity)`**: Initializes a new StringBuilder with a specified string and an optional capacity. If no capacity is provided, it defaults to the `DefaultCapacity`.
* **`StringBuilder(int capacity, int maxCapacity)`**: Initializes a new StringBuilder with a specified initial capacity and maximum capacity. 
* **`StringBuilder(int capacity)`**: Initializes a new StringBuilder with a specified initial capacity.
* **`StringBuilder()`**: Initializes a new StringBuilder with the default capacity (`DefaultCapacity`).

### String Manipulation Methods:

* **`append(boolean value)`**: Appends a boolean value to the StringBuilder.
* **`append(byte value)`**: Appends a byte value to the StringBuilder.
* **`append(short value)`**: Appends a short value to the StringBuilder.
* **`append(int value)`**: Appends an integer value to the StringBuilder.
* **`append(long value)`**: Appends a long value to the StringBuilder.
* **`append(float value)`**: Appends a float value to the StringBuilder.
* **`append(double value)`**: Appends a double value to the StringBuilder.
* **`append(Object value)`**: Appends an object's string representation to the StringBuilder.
* **`append(char value)`**: Appends a character to the StringBuilder.
* **`append(char[] value)`**: Appends a character array to the StringBuilder.
* **`append(String value)`**: Appends a string to the StringBuilder.
* **`append(String value, int startIndex, int count)`**: Appends a portion of a string to the StringBuilder.
* **`append(char[] value, int valueCount)`**: Appends a specified number of characters from a character array to the StringBuilder.
* **`append(char value, int repeatCount)`**: Repeats a character a specified number of times and appends it to the StringBuilder.
* **`remove(int startIndex, int count)`**: Removes a specified number of characters starting at a given index.
* **`insert(int index, String value, int count)`**: Inserts a string at a specific index.
* **`replace(char oldChar, char newChar, int startIndex, int count)`**: Replaces occurrences of an old character with a new character within a specified range.
* **`replace(String oldValue, String newValue)`**: Replaces all occurrences of an old string with a new string.
* **`trim()`**: Removes leading and trailing whitespace from the StringBuilder.
* **`toUpperCaseInPlace()`**: Converts all characters in the StringBuilder to uppercase.
* **`toLowerCaseInPlace()`**: Converts all characters in the StringBuilder to lowercase.
* **`collapseWhitespace()`**: Replaces consecutive whitespace characters with a single space.
* **`normalizeLineEndings()`**: Standardizes line endings within the StringBuilder to use only newline characters (`\n`).
* **`replaceAllWhitespaceWith(char replacement)`**: Replaces all whitespace characters in the StringBuilder with a specified character.
* **`toCharArray()`**: Returns an array of characters representing the contents of the StringBuilder.
* **`cloneBuilder()`**: Creates a new StringBuilder object that is a copy of the current one.

### Other Methods:

* **`startsWith(String value)`**: Checks if the StringBuilder starts with a specified string.
* **`endsWith(String value)`**: Checks if the StringBuilder ends with a specified string.
* **`indexOf(char value)`**: Returns the index of the first occurrence of a character within the StringBuilder.
* **`lastIndexOf(char value)`**: Returns the index of the last occurrence of a character within the StringBuilder.
* **`countOccurrences(String value)`**: Counts the number of occurrences of a string within the StringBuilder.
* **`appendWrapped(String prefix, String value, String suffix)`**: Appends a string with a specified prefix and suffix, wrapping it to a maximum width if necessary.



###  Helper Methods:

* **`compactRepeatedCharacters()`**: Compacts consecutive repeated characters within the StringBuilder, effectively removing redundancy while preserving the original order of unique characters.
* **`findChunkForIndex(int index)`**: Finds the appropriate chunk (segment) of the StringBuilder that contains a given index.



###  Internal Helper Classes and Methods:

The code also includes internal helper classes and methods for managing chunks of characters within the StringBuilder, handling memory allocation, and optimizing string manipulation operations. These are not directly exposed to users but play a crucial role in the efficiency and performance of the `StringBuilder` class.


## Pseudocode (Example Method - `append(String value)`):

```
// Method: append(String value)
  1. Check if the StringBuilder's current capacity is sufficient to accommodate the new string.
     - If not, expand the capacity using a method like `expandByABlock()`.
  2. Append the characters of the input string to the end of the StringBuilder's internal character array.
  3. Update the length of the StringBuilder to reflect the addition of the new characters.



```

