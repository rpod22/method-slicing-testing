![Capgemini Logo](https://www.capgemini.com/wp-content/themes/capgemini2020/assets/images/logo.svg)

### Make it real.

---
## Overview

This code defines a `StringBuilder` class that extends the functionality of Java's built-in `StringBuilder` class. It provides various methods for manipulating strings, including common operations like appending, replacing, searching, and formatting. 

## Package/Module Name:

CustomStringOperations (or similar)

## Class/File Name:

EnhancedStringBuilder.java

## Detailed Documentation:

**Constructor:**

* **`EnhancedStringBuilder(String initialValue)`**: Initializes the `StringBuilder` with a given string value. If no initial value is provided, it creates an empty `StringBuilder`.

**Methods:**

* **`appendQuoted(String value)`**: Appends a quoted string to the `StringBuilder`, escaping special characters as needed for JSON formatting.
* **`appendEscapedJson(String value)`**: Appends a string to the `StringBuilder`, escaping all non-printable and special characters according to JSON syntax rules.
* **`appendHexDump(byte[] data)`**: Appends a hexadecimal representation of a byte array to the `StringBuilder`.
* **`appendIndentedLines(String value, String indent)`**: Appends a string with each line indented by a specified number of spaces or characters.
* **`padLeft(int totalWidth, char paddingChar)`**: Pads the left side of the string with a given character until it reaches a specified width.
* **`padRight(int totalWidth, char paddingChar)`**: Pads the right side of the string with a given character until it reaches a specified width.
* **`repeat(int repeatCount)`**: Repeats the current string `repeatCount` times and appends the result to itself.
* **`removeAll(String value)`**: Removes all occurrences of a specific string from the `StringBuilder`.
* **`compactRepeatedCharacters()`**: Removes consecutive duplicate characters from the string, keeping only one instance of each character.

**Other Methods:**

The class also includes methods for common string operations like `append`, `insert`, `replace`, `remove`, `substring`, `trim`, `toUpperCaseInPlace`, `toLowerCaseInPlace`, `collapseWhitespace`, and more. These methods are similar to the ones provided by Java's built-in `StringBuilder` class but may have additional logic or behavior specific to this enhanced implementation.

## Pseudo Code:


```
// Class: EnhancedStringBuilder

// Constructor: EnhancedStringBuilder(String initialValue)
  1. Initialize internal string buffer with initialValue (or empty string if null).
  2. Set length of the StringBuilder to the length of the initial value.

// Method: appendQuoted(String value)
  1. Append a double quote character ("") to the StringBuilder.
  2. Iterate through each character in the input value:
    - If the character is a double quote, backslash, or newline, escape it by adding a backslash before it.
    - Otherwise, append the character directly to the StringBuilder.
  3. Append another double quote character ("") to the StringBuilder.

// Method: appendEscapedJson(String value)
  1. Iterate through each character in the input value:
    - If the character is a double quote, backslash, forward slash, carriage return, or newline, escape it by adding a backslash before it.
    - Otherwise, append the character directly to the StringBuilder.

// Method: appendHexDump(byte[] data)
  1. Iterate through each byte in the input array:
    - Convert the byte to its hexadecimal representation (two characters).
    - Append the hexadecimal representation to the StringBuilder.
    - Add a space after every two bytes.

// Method: appendIndentedLines(String value, String indent)
  1. Split the input string into lines using newline characters as delimiters.
  2. Iterate through each line:
    - Append the indent string to the StringBuilder.
    - Append the current line to the StringBuilder.
    - Append a newline character to the StringBuilder.

// Method: padLeft(int totalWidth, char paddingChar)
  1. Calculate the number of padding characters needed.
  2. Append the required number of padding characters to the beginning of the string.

// Method: padRight(int totalWidth, char paddingChar)
  1. Calculate the number of padding characters needed.
  2. Append the required number of padding characters to the end of the string.

// Method: repeat(int repeatCount)
  1. Iterate `repeatCount` times:
    - Append the current string to itself.



```




