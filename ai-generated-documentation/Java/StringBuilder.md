![Capgemini Logo](https://www.capgemini.com/wp-content/themes/capgemini2020/assets/images/logo.svg)

### Make it real.

---
## StringBuilder Class Documentation

**Overview:** 

The `StringBuilder` class represents a mutable sequence of characters. It provides methods for appending, inserting, deleting, and manipulating text within the string. Unlike the `String` class, which is immutable, changes made to a `StringBuilder` object do not create new objects; instead, they modify the existing one in place. This makes `StringBuilder` more efficient for tasks involving frequent string modifications.

**Package/module name:**  java.lang (or equivalent)

**Class/file name:** StringBuilder.java

**Detailed Documentation:**


### Constructor Methods:
* **`StringBuilder(String value, int capacity)`**: Initializes a new `StringBuilder` object with the specified initial content (`value`) and capacity. If `capacity` is less than the length of `value`, it will be set to the length of `value`.

* **`StringBuilder(int capacity)`**: Initializes a new `StringBuilder` object with the specified initial capacity. 
* **`StringBuilder()`**: Initializes a new `StringBuilder` object with the default capacity (16).
* **`StringBuilder(String value)`**: Initializes a new `StringBuilder` object with the specified initial content (`value`).

### String Manipulation Methods:

* **`append(boolean value)`**: Appends the boolean value to the end of the string.
* **`append(byte value)`**: Appends the byte value to the end of the string.
* **`append(short value)`**: Appends the short value to the end of the string.
* **`append(int value)`**: Appends the integer value to the end of the string.
* **`append(long value)`**: Appends the long value to the end of the string.
* **`append(float value)`**: Appends the float value to the end of the string.
* **`append(double value)`**: Appends the double value to the end of the string.
* **`append(Object value)`**: Appends the string representation of the object value to the end of the string.
* **`append(char value)`**: Appends the character value to the end of the string.
* **`append(char[] value)`**: Appends the characters from the specified array to the end of the string.
* **`append(String value)`**: Appends the specified string to the end of the string.
* **`append(String value, int startIndex, int count)`**: Appends a portion of the specified string to the end of the string.
* **`append(char[] value, int valueCount)`**: Appends the specified number of characters from the array to the end of the string.
* **`appendRepeated(String value, int repeatCount)`**: Appends the specified string `repeatCount` times to the end of the string.
* **`appendJoined(Iterable<String> values, String separator)`**: Appends the elements of the iterable to the end of the string, separated by the specified separator.

### Insertion Methods:

* **`insert(int index, String value, int count)`**: Inserts the specified portion of the string at the given index.
* **`insert(int index, char[] value, int startIndex, int charCount)`**: Inserts the specified characters from the array at the given index.

### Replacement Methods:

* **`replace(char oldChar, char newChar)`**: Replaces all occurrences of `oldChar` with `newChar` in the string.
* **`replace(String oldValue, String newValue)`**: Replaces all occurrences of `oldValue` with `newValue` in the string.
* **`replaceFirst(String oldValue, String newValue)`**: Replaces the first occurrence of `oldValue` with `newValue` in the string.
* **`replaceLast(String oldValue, String newValue)`**: Replaces the last occurrence of `oldValue` with `newValue` in the string.

### Removal Methods:

* **`remove(int startIndex, int count)`**: Removes a specified number of characters from the string starting at the given index.


### Other Methods:
* **`clear()`**: Removes all characters from the string.
* **`toString()`**: Returns a string representation of the `StringBuilder` object.
* **`toString(int startIndex, int length)`**: Returns a substring of the `StringBuilder` object starting at the specified index and with the given length.
* **`startsWith(String value)`**: Checks if the string starts with the specified prefix.
* **`endsWith(String value)`**: Checks if the string ends with the specified suffix.
* **`indexOf(char value)`**: Returns the index of the first occurrence of the specified character in the string.
* **`lastIndexOf(char value)`**: Returns the index of the last occurrence of the specified character in the string.
* **`count(char value)`**: Returns the number of occurrences of the specified character in the string.
* **`contains(String value)`**: Checks if the string contains the specified substring.
* **`equalsText(String value)`**: Compares this `StringBuilder` object to another string for equality based on their text content.
* **`compareToText(String value)`**: Compares this `StringBuilder` object to another string for order based on their text content.

### String Manipulation Helpers:


* **`trim()`**: Removes leading and trailing whitespace from the string.
* **`toUpperCaseInPlace()`**: Converts all characters in the string to uppercase.
* **`toLowerCaseInPlace()`**: Converts all characters in the string to lowercase.
* **`padCenter(int totalWidth, char paddingChar)`**: Pads the string with `paddingChar` on both sides to achieve a total width of `totalWidth`.
* **`repeat(int repeatCount)`**: Creates a new `StringBuilder` object containing the original string repeated `repeatCount` times.

### Advanced Methods:


* **`split(char separator)`**: Splits the string into an array of substrings based on the specified separator character.



**Pseudocode:**
The pseudocode for each method is omitted due to its length and complexity. However, it generally involves iterating through characters, manipulating arrays, and performing string comparisons.

