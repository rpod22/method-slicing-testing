import java.util.Arrays;

/**
 * Represents a mutable string of characters. This class cannot be inherited.
 * Port of the .NET StringBuilder class based on the provided C# implementation.
 */
public final class StringBuilder {
    private static final int DefaultCapacity = 0x10;
    private static final int DefaultMaxCapacity = Integer.MAX_VALUE;
    private static final int MaxChunkSize = 8000;

    private final int _maxCapacity;
    private char[] _chunkChars;
    private int _chunkLength;
    private StringBuilder _chunkPrevious;
    private int _chunkOffset;

    public int getMaxCapacity() {
        return _maxCapacity;
    }

    public char charAt(int index) {
        StringBuilder chunk = this;
        while (true) {
            int indexInBlock = index - chunk._chunkOffset;
            if (indexInBlock >= 0) {
                if (indexInBlock >= chunk._chunkLength) {
                    throw new IndexOutOfBoundsException();
                }
                return chunk._chunkChars[indexInBlock];
            }
            chunk = chunk._chunkPrevious;
            if (chunk == null) {
                throw new IndexOutOfBoundsException();
            }
        }
    }

    public void setCharAt(int index, char value) {
        StringBuilder chunk = this;
        while (true) {
            int indexInBlock = index - chunk._chunkOffset;
            if (indexInBlock >= 0) {
                if (indexInBlock >= chunk._chunkLength) {
                    throw new IndexOutOfBoundsException();
                }
                chunk._chunkChars[indexInBlock] = value;
                return;
            }
            chunk = chunk._chunkPrevious;
            if (chunk == null) {
                throw new IndexOutOfBoundsException();
            }
        }
    }

    public int getCapacity() {
        return _chunkChars.length + _chunkOffset;
    }

    public void setCapacity(int value) {
        if (value < 0 || value > _maxCapacity || value < getLength()) {
            throw new IllegalArgumentException();
        }
        if (getCapacity() != value) {
            int num = value - _chunkOffset;
            char[] destinationArray = new char[num];
            System.arraycopy(_chunkChars, 0, destinationArray, 0, _chunkLength);
            _chunkChars = destinationArray;
        }
    }

    public int getLength() {
        return _chunkOffset + _chunkLength;
    }

    public void setLength(int value) {
        if (value < 0 || value > _maxCapacity) {
            throw new IllegalArgumentException();
        }
        if (value == 0 && _chunkPrevious == null) {
            _chunkLength = 0;
            _chunkOffset = 0;
            return;
        }
        int delta = value - getLength();
        if (delta > 0) {
            append('\0', delta);
        } else {
            StringBuilder chunk = findChunkForIndex(value);
            if (chunk != this) {
                int capacityToPreserve = Math.min(getCapacity(), Math.max(getLength() * 6 / 5, _chunkChars.length));
                int newLen = capacityToPreserve - chunk._chunkOffset;
                if (newLen > chunk._chunkChars.length) {
                    char[] newArray = new char[newLen];
                    System.arraycopy(chunk._chunkChars, 0, newArray, 0, chunk._chunkLength);
                    _chunkChars = newArray;
                } else {
                    _chunkChars = chunk._chunkChars;
                }
                _chunkPrevious = chunk._chunkPrevious;
                _chunkOffset = chunk._chunkOffset;
            }
            _chunkLength = value - chunk._chunkOffset;
        }
    }

    public StringBuilder(String value, int startIndex, int length, int capacity) {
        if (capacity < 0 || length < 0 || startIndex < 0) {
            throw new IllegalArgumentException();
        }
        if (value == null) {
            value = "";
        }
        if (startIndex > value.length() - length) {
            throw new IllegalArgumentException();
        }
        _maxCapacity = DefaultMaxCapacity;
        if (capacity == 0) {
            capacity = DefaultCapacity;
        }
        if (capacity < length) {
            capacity = length;
        }
        _chunkChars = new char[capacity];
        _chunkLength = length;
        value.getChars(startIndex, startIndex + length, _chunkChars, 0);
    }

    public StringBuilder(String value, int capacity) {
        this(value, 0, value != null ? value.length() : 0, capacity);
    }

    public StringBuilder(int capacity, int maxCapacity) {
        if (capacity > maxCapacity || maxCapacity < 1 || capacity < 0) {
            throw new IllegalArgumentException();
        }
        if (capacity == 0) {
            capacity = Math.min(DefaultCapacity, maxCapacity);
        }
        _maxCapacity = maxCapacity;
        _chunkChars = new char[capacity];
    }

    public StringBuilder(int capacity) {
        this("", capacity);
    }

    public StringBuilder(String value) {
        this(value, DefaultCapacity);
    }

    public StringBuilder() {
        this(DefaultCapacity);
    }

    private StringBuilder(int size, int maxCapacity, StringBuilder previousBlock) {
        _chunkChars = new char[size];
        _maxCapacity = maxCapacity;
        _chunkPrevious = previousBlock;
        if (previousBlock != null) {
            _chunkOffset = previousBlock._chunkOffset + previousBlock._chunkLength;
        }
    }

    private StringBuilder(StringBuilder from) {
        _chunkLength = from._chunkLength;
        _chunkOffset = from._chunkOffset;
        _chunkChars = from._chunkChars;
        _chunkPrevious = from._chunkPrevious;
        _maxCapacity = from._maxCapacity;
    }

    public StringBuilder append(boolean value) { return append(String.valueOf(value)); }
    public StringBuilder append(byte value) { return append(String.valueOf(value)); }
    public StringBuilder append(short value) { return append(String.valueOf(value)); }
    public StringBuilder append(int value) { return append(String.valueOf(value)); }
    public StringBuilder append(long value) { return append(String.valueOf(value)); }
    public StringBuilder append(float value) { return append(String.valueOf(value)); }
    public StringBuilder append(double value) { return append(String.valueOf(value)); }
    public StringBuilder append(Object value) { return value == null ? this : append(value.toString()); }

    public StringBuilder append(char value) {
        if (_chunkLength < _chunkChars.length) {
            _chunkChars[_chunkLength++] = value;
        } else {
            append(value, 1);
        }
        return this;
    }

    public StringBuilder append(char[] value) {
        if (value != null && value.length > 0) {
            append(value, value.length);
        }
        return this;
    }

    public StringBuilder append(String value) {
        if (value != null && !value.isEmpty()) {
            char[] chunkChars = _chunkChars;
            int chunkLength = _chunkLength;
            int length = value.length();
            int num3 = chunkLength + length;
            if (num3 < chunkChars.length) {
                value.getChars(0, length, chunkChars, chunkLength);
                _chunkLength = num3;
            } else {
                appendHelper(value);
            }
        }
        return this;
    }

    public StringBuilder append(String value, int startIndex, int count) {
        if (startIndex < 0 || count < 0 || value == null || startIndex > value.length() - count) {
            throw new IllegalArgumentException();
        }
        if (count != 0) {
            append(value.substring(startIndex, startIndex + count));
        }
        return this;
    }

    public StringBuilder append(char[] value, int startIndex, int charCount) {
        if (startIndex < 0 || charCount < 0 || value == null || startIndex > value.length - charCount) {
            throw new IllegalArgumentException();
        }
        if (charCount != 0) {
            for (int i = startIndex; i < startIndex + charCount; ++i) {
                append(value[i], 1);
            }
        }
        return this;
    }

    public StringBuilder append(char value, int repeatCount) {
        if (repeatCount < 0) {
            throw new IllegalArgumentException();
        }
        if (repeatCount != 0) {
            int chunkLength = _chunkLength;
            while (repeatCount > 0) {
                if (chunkLength < _chunkChars.length) {
                    _chunkChars[chunkLength++] = value;
                    repeatCount--;
                } else {
                    _chunkLength = chunkLength;
                    expandByABlock(repeatCount);
                    chunkLength = 0;
                }
            }
            _chunkLength = chunkLength;
        }
        return this;
    }

    public StringBuilder remove(int startIndex, int length) {
        if (length < 0 || startIndex < 0 || length > getLength() - startIndex) {
            throw new IllegalArgumentException();
        }
        if (getLength() == length && startIndex == 0) {
            setLength(0);
            return this;
        }
        if (length > 0) {
            removeInternal(startIndex, length);
        }
        return this;
    }

    public StringBuilder insert(int index, String value, int count) {
        if (count < 0 || index > getLength()) {
            throw new IllegalArgumentException();
        }
        if (value != null && !value.isEmpty() && count != 0) {
            long num2 = (long) value.length() * count;
            if (num2 > _maxCapacity - getLength()) {
                throw new OutOfMemoryError();
            }
            MakeRoomResult room = makeRoom(index, (int)num2, false);
            char[] chars = value.toCharArray();
            int charLength = chars.length;
            while (count > 0) {
                int cindex = 0;
                room = replaceInPlaceAtChunk(room.chunk, room.indexInChunk, chars, cindex, charLength);
                --count;
            }
        }
        return this;
    }

    public StringBuilder insert(int index, char[] value, int startIndex, int charCount) {
        if (index > getLength() || value == null || startIndex < 0 || charCount < 0 || startIndex > value.length - charCount) {
            throw new IllegalArgumentException();
        }
        if (charCount > 0) {
            insert(index, new String(value, startIndex, charCount), 1);
        }
        return this;
    }

    public StringBuilder replace(char oldChar, char newChar, int startIndex, int count) {
        if (startIndex > getLength() || count < 0 || startIndex > getLength() - count) {
            throw new IllegalArgumentException();
        }
        int num2 = startIndex + count;
        StringBuilder chunkPrevious = this;
        while (true) {
            int num3 = num2 - chunkPrevious._chunkOffset;
            int num4 = startIndex - chunkPrevious._chunkOffset;
            if (num3 >= 0) {
                int index = Math.max(num4, 0);
                int num6 = Math.min(chunkPrevious._chunkLength, num3);
                while (index < num6) {
                    if (chunkPrevious._chunkChars[index] == oldChar) {
                        chunkPrevious._chunkChars[index] = newChar;
                    }
                    index++;
                }
            }
            if (num4 < 0) {
                chunkPrevious = chunkPrevious._chunkPrevious;
            } else {
                break;
            }
        }
        return this;
    }

    public StringBuilder replace(char oldChar, char newChar) {
        return replace(oldChar, newChar, 0, getLength());
    }

    public StringBuilder replace(String oldValue, String newValue, int startIndex, int count) {
        if (startIndex > getLength() || count < 0 || startIndex > getLength() - count || oldValue == null || oldValue.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (newValue == null) newValue = "";
        int newLength = newValue.length();
        int oldLength = oldValue.length();
        int[] sourceArray = null;
        int replacementsCount = 0;
        StringBuilder chunk = findChunkForIndex(startIndex);
        int indexInChunk = startIndex - chunk._chunkOffset;

        while (count > 0) {
            if (startsWith(chunk, indexInChunk, count, oldValue)) {
                if (sourceArray == null) {
                    sourceArray = new int[5];
                } else if (replacementsCount >= sourceArray.length) {
                    int[] destinationArray = new int[sourceArray.length * 3 / 2 + 4];
                    System.arraycopy(sourceArray, 0, destinationArray, 0, sourceArray.length);
                    sourceArray = destinationArray;
                }
                sourceArray[replacementsCount] = indexInChunk;
                ++replacementsCount;
                indexInChunk += oldLength;
                count -= oldLength;
            } else {
                ++indexInChunk;
                --count;
            }

            if (indexInChunk >= chunk._chunkLength || count == 0) {
                int index = indexInChunk + chunk._chunkOffset;
                replaceAllInChunk(sourceArray, replacementsCount, chunk, oldLength, newValue);
                index += (newLength - oldLength) * replacementsCount;
                replacementsCount = 0;
                chunk = findChunkForIndex(index);
                indexInChunk = index - chunk._chunkOffset;
            }
        }
        return this;
    }

    public StringBuilder replace(String oldValue, String newValue) {
        return replace(oldValue, newValue, 0, getLength());
    }

    public StringBuilder clear() {
        setLength(0);
        return this;
    }

    @Override
    public String toString() {
        char[] result = new char[getLength()];
        StringBuilder chunkPrevious = this;
        do {
            if (chunkPrevious._chunkLength > 0) {
                System.arraycopy(chunkPrevious._chunkChars, 0, result, chunkPrevious._chunkOffset, chunkPrevious._chunkLength);
            }
            chunkPrevious = chunkPrevious._chunkPrevious;
        } while (chunkPrevious != null);
        return new String(result);
    }

    public String toString(int startIndex, int length) {
        int currentLength = getLength();
        if (startIndex < 0 || startIndex > currentLength || length < 0 || startIndex > currentLength - length) {
            throw new IllegalArgumentException();
        }
        StringBuilder chunk = this;
        int sourceEndIndex = startIndex + length;
        char[] result = new char[length];
        int destinationIndex = length;

        while (destinationIndex > 0) {
            int chunkLength = sourceEndIndex - chunk._chunkOffset;
            if (chunkLength >= 0) {
                if (chunkLength > chunk._chunkLength) {
                    chunkLength = chunk._chunkLength;
                }
                int leftChars = destinationIndex;
                int charCount = leftChars;
                int index = chunkLength - leftChars;
                if (index < 0) {
                    charCount += index;
                    index = 0;
                }
                destinationIndex -= charCount;
                if (charCount > 0) {
                    char[] chunkChars = chunk._chunkChars;
                    if (charCount + destinationIndex > length || charCount + index > chunkChars.length) {
                        throw new IllegalArgumentException();
                    }
                    System.arraycopy(chunkChars, index, result, destinationIndex, charCount);
                }
            }
            chunk = chunk._chunkPrevious;
        }
        return new String(result);
    }

    public StringBuilder appendLine(String str) {
        append(str);
        return appendLine();
    }

    public StringBuilder appendLine() {
        return append("\r\n");
    }

    // INTERNAL HELPER CLASSES AND METHODS

    private boolean startsWith(StringBuilder chunk, int indexInChunk, int count, String value) {
        for (int i = 0, e = value.length(); i < e; ++i) {
            if (count == 0) return false;
            if (indexInChunk >= chunk._chunkLength) {
                chunk = next(chunk);
                if (chunk == null) return false;
                indexInChunk = 0;
            }
            if (value.charAt(i) != chunk._chunkChars[indexInChunk]) {
                return false;
            }
            ++indexInChunk;
            --count;
        }
        return true;
    }

    private void replaceAllInChunk(int[] replacements, int replacementsCount, StringBuilder sourceChunk, int removeCount, String value) {
        if (replacementsCount > 0) {
            int count = (value.length() - removeCount) * replacementsCount;
            StringBuilder chunk = sourceChunk;
            int indexInChunk = replacements[0];

            if (count > 0) {
                MakeRoomResult room = makeRoom(chunk._chunkOffset + indexInChunk, count, true);
                chunk = room.chunk;
                indexInChunk = room.indexInChunk;
            }

            int index = 0;
            int replacementIndex = 0;
            char[] chars = value.toCharArray();

            while (true) {
                MakeRoomResult repl = replaceInPlaceAtChunk(chunk, indexInChunk, chars, replacementIndex, value.length());
                chunk = repl.chunk;
                indexInChunk = repl.indexInChunk;
                replacementIndex = repl.valueIndex;

                if (replacementIndex == value.length()) {
                    replacementIndex = 0;
                }

                int valueIndex = replacements[index] + removeCount;
                ++index;

                if (index < replacementsCount) {
                    int nextIndex = replacements[index];
                    if (count != 0) {
                        MakeRoomResult r2 = replaceInPlaceAtChunk(chunk, indexInChunk, sourceChunk._chunkChars, valueIndex, nextIndex - valueIndex);
                        chunk = r2.chunk;
                        indexInChunk = r2.indexInChunk;
                    } else {
                        indexInChunk += nextIndex - valueIndex;
                    }
                    continue;
                }

                if (count < 0) {
                    removeInternal(chunk._chunkOffset + indexInChunk, -count);
                }
                break;
            }
        }
    }

    private StringBuilder next(StringBuilder chunk) {
        return chunk == this ? null : findChunkForIndex(chunk._chunkOffset + chunk._chunkLength);
    }

    private static class MakeRoomResult {
        StringBuilder chunk;
        int indexInChunk;
        int valueIndex;
    }

    private MakeRoomResult replaceInPlaceAtChunk(StringBuilder chunk, int indexInChunk, char[] value, int valueIndex, int count) {
        MakeRoomResult result = new MakeRoomResult();
        if (count == 0) {
            result.chunk = chunk;
            result.indexInChunk = indexInChunk;
            result.valueIndex = valueIndex;
            return result;
        }
        while (true) {
            int length = Math.min(chunk._chunkLength - indexInChunk, count);
            System.arraycopy(value, valueIndex, chunk._chunkChars, indexInChunk, length);
            indexInChunk += length;
            if (indexInChunk >= chunk._chunkLength) {
                chunk = next(chunk);
                indexInChunk = 0;
            }
            count -= length;
            valueIndex += length;
            if (count == 0) {
                result.chunk = chunk;
                result.indexInChunk = indexInChunk;
                result.valueIndex = valueIndex;
                return result;
            }
        }
    }

    private MakeRoomResult makeRoom(int index, int count, boolean doneMoveFollowingChars) {
        if (count + getLength() > _maxCapacity) {
            throw new IllegalArgumentException();
        }
        StringBuilder chunk = this;
        while (chunk._chunkOffset > index) {
            chunk._chunkOffset += count;
            chunk = chunk._chunkPrevious;
        }
        int indexInChunk = index - chunk._chunkOffset;

        if (!doneMoveFollowingChars && chunk._chunkLength <= 0x20 && chunk._chunkChars.length - chunk._chunkLength >= count) {
            int chunkLength = chunk._chunkLength;
            while (chunkLength > indexInChunk) {
                chunkLength--;
                chunk._chunkChars[chunkLength + count] = chunk._chunkChars[chunkLength];
            }
            chunk._chunkLength += count;
        } else {
            StringBuilder builder = new StringBuilder(Math.max(count, DefaultCapacity), chunk._maxCapacity, chunk._chunkPrevious);
            builder._chunkLength = count;
            int length = Math.min(count, indexInChunk);
            if (length > 0) {
                System.arraycopy(chunk._chunkChars, 0, builder._chunkChars, 0, length);
                int nextLength = indexInChunk - length;
                if (nextLength >= 0) {
                    System.arraycopy(chunk._chunkChars, length, chunk._chunkChars, 0, nextLength);
                    indexInChunk = nextLength;
                }
            }
            chunk._chunkPrevious = builder;
            chunk._chunkOffset += count;
            if (length < count) {
                chunk = builder;
                indexInChunk = length;
            }
        }
        MakeRoomResult res = new MakeRoomResult();
        res.chunk = chunk;
        res.indexInChunk = indexInChunk;
        return res;
    }

    private StringBuilder findChunkForIndex(int index) {
        StringBuilder chunkPrevious = this;
        while (chunkPrevious._chunkOffset > index) {
            chunkPrevious = chunkPrevious._chunkPrevious;
        }
        return chunkPrevious;
    }

    private void appendHelper(String value) {
        if (value == null || value.isEmpty()) return;
        append(value.toCharArray(), value.length());
    }

    private void expandByABlock(int minBlockCharCount) {
        if (minBlockCharCount + getLength() > _maxCapacity || minBlockCharCount + getLength() < minBlockCharCount) {
            throw new IllegalArgumentException();
        }
        int newBlockLength = Math.max(minBlockCharCount, Math.min(getLength(), MaxChunkSize));
        char[] chunkChars = new char[newBlockLength];
        _chunkPrevious = new StringBuilder(this);
        _chunkOffset += _chunkLength;
        _chunkLength = 0;
        _chunkChars = chunkChars;
    }

    private static class RemoveResult {
        StringBuilder chunk;
        int indexInChunk;
    }

    private RemoveResult removeInternal(int startIndex, int count) {
        int num = startIndex + count;
        StringBuilder chunk = this;
        StringBuilder builder = null;
        int sourceIndex = 0;

        while (true) {
            if (num - chunk._chunkOffset >= 0) {
                if (builder == null) {
                    builder = chunk;
                    sourceIndex = num - builder._chunkOffset;
                }
                if (startIndex - chunk._chunkOffset >= 0) {
                    int indexInChunk = startIndex - chunk._chunkOffset;
                    int destinationIndex = indexInChunk;
                    int num4 = builder._chunkLength - sourceIndex;

                    if (builder != chunk) {
                        destinationIndex = 0;
                        chunk._chunkLength = indexInChunk;
                        builder._chunkPrevious = chunk;
                        builder._chunkOffset = chunk._chunkOffset + chunk._chunkLength;
                        if (indexInChunk == 0) {
                            builder._chunkPrevious = chunk._chunkPrevious;
                            chunk = builder;
                        }
                    }
                    builder._chunkLength -= sourceIndex - destinationIndex;
                    if (destinationIndex != sourceIndex) {
                        System.arraycopy(builder._chunkChars, sourceIndex, builder._chunkChars, destinationIndex, num4);
                    }
                    RemoveResult res = new RemoveResult();
                    res.chunk = chunk;
                    res.indexInChunk = indexInChunk;
                    return res;
                }
            } else {
                chunk._chunkOffset -= count;
            }
            chunk = chunk._chunkPrevious;
        }
    }

    private void append(char[] value, int valueCount) {
        if (value == null) return;
        int num = valueCount + _chunkLength;
        if (num <= _chunkChars.length) {
            System.arraycopy(value, 0, _chunkChars, _chunkLength, valueCount);
            _chunkLength = num;
        } else {
            int count = _chunkChars.length - _chunkLength;
            if (count > 0) {
                System.arraycopy(value, 0, _chunkChars, _chunkLength, count);
                _chunkLength = _chunkChars.length;
            }
            int minBlockCharCount = valueCount - count;
            expandByABlock(minBlockCharCount);
            System.arraycopy(value, count, _chunkChars, 0, minBlockCharCount);
            _chunkLength = minBlockCharCount;
        }
    }

    public boolean isEmpty() {
        return getLength() == 0;
    }

    public boolean isNotEmpty() {
        return getLength() != 0;
    }

    public boolean startsWith(String value) {
        if (value == null) {
            return false;
        }

        int valueLength = value.length();
        if (valueLength > getLength()) {
            return false;
        }

        for (int index = 0; index < valueLength; index++) {
            if (charAt(index) != value.charAt(index)) {
                return false;
            }
        }

        return true;
    }

    public boolean endsWith(String value) {
        if (value == null) {
            return false;
        }

        int valueLength = value.length();
        int currentLength = getLength();
        if (valueLength > currentLength) {
            return false;
        }

        int startIndex = currentLength - valueLength;
        for (int valueIndex = 0; valueIndex < valueLength; valueIndex++) {
            if (charAt(startIndex + valueIndex) != value.charAt(valueIndex)) {
                return false;
            }
        }

        return true;
    }

    public int indexOf(char value) {
        int currentLength = getLength();
        for (int index = 0; index < currentLength; index++) {
            if (charAt(index) == value) {
                return index;
            }
        }

        return -1;
    }

    public int lastIndexOf(char value) {
        for (int index = getLength() - 1; index >= 0; index--) {
            if (charAt(index) == value) {
                return index;
            }
        }

        return -1;
    }

    public int count(char value) {
        int matches = 0;
        int currentLength = getLength();

        for (int index = 0; index < currentLength; index++) {
            if (charAt(index) == value) {
                matches++;
            }
        }

        return matches;
    }

    public boolean contains(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }

        return indexOf(value.charAt(0)) >= 0 && toString().contains(value);
    }

    public StringBuilder reverse() {
        int leftIndex = 0;
        int rightIndex = getLength() - 1;

        while (leftIndex < rightIndex) {
            char leftChar = charAt(leftIndex);
            char rightChar = charAt(rightIndex);
            setCharAt(leftIndex, rightChar);
            setCharAt(rightIndex, leftChar);
            leftIndex++;
            rightIndex--;
        }

        return this;
    }

    public StringBuilder appendRepeated(String value, int repeatCount) {
        if (value == null || repeatCount <= 0) {
            return this;
        }

        for (int repeatIndex = 0; repeatIndex < repeatCount; repeatIndex++) {
            append(value);
        }

        return this;
    }

    public StringBuilder appendSeparated(String[] values, String separator) {
        if (values == null || values.length == 0) {
            return this;
        }

        String actualSeparator = separator == null ? "" : separator;

        for (int valueIndex = 0; valueIndex < values.length; valueIndex++) {
            if (valueIndex > 0) {
                append(actualSeparator);
            }

            String currentValue = values[valueIndex];
            if (currentValue != null) {
                append(currentValue);
            }
        }

        return this;
    }

    public StringBuilder trimLeadingWhitespace() {
        int removeCount = 0;
        int currentLength = getLength();

        while (removeCount < currentLength && Character.isWhitespace(charAt(removeCount))) {
            removeCount++;
        }

        if (removeCount > 0) {
            remove(0, removeCount);
        }

        return this;
    }

    public StringBuilder trimTrailingWhitespace() {
        int newLength = getLength();

        while (newLength > 0 && Character.isWhitespace(charAt(newLength - 1))) {
            newLength--;
        }

        if (newLength < getLength()) {
            setLength(newLength);
        }

        return this;
    }

    public StringBuilder removeWhitespace() {
        int writeIndex = 0;
        int currentLength = getLength();

        for (int readIndex = 0; readIndex < currentLength; readIndex++) {
            char currentChar = charAt(readIndex);
            if (!Character.isWhitespace(currentChar)) {
                if (writeIndex != readIndex) {
                    setCharAt(writeIndex, currentChar);
                }
                writeIndex++;
            }
        }

        if (writeIndex < currentLength) {
            setLength(writeIndex);
        }

        return this;
    }

    public StringBuilder normalizeLineEndings() {
        int currentLength = getLength();
        int writeIndex = 0;

        for (int readIndex = 0; readIndex < currentLength; readIndex++) {
            char currentChar = charAt(readIndex);
            if (currentChar == '\r') {
                if (readIndex + 1 < currentLength && charAt(readIndex + 1) == '\n') {
                    continue;
                }

                currentChar = '\n';
            }

            if (writeIndex != readIndex) {
                setCharAt(writeIndex, currentChar);
            }

            writeIndex++;
        }

        if (writeIndex < currentLength) {
            setLength(writeIndex);
        }

        return this;
    }

    public StringBuilder replaceAllWhitespaceWith(char replacement) {
        int currentLength = getLength();

        for (int index = 0; index < currentLength; index++) {
            if (Character.isWhitespace(charAt(index))) {
                setCharAt(index, replacement);
            }
        }

        return this;
    }

    public char[] toCharArray() {
        int currentLength = getLength();
        char[] result = new char[currentLength];

        for (int index = 0; index < currentLength; index++) {
            result[index] = charAt(index);
        }

        return result;
    }

    public StringBuilder cloneBuilder() {
        return new StringBuilder(this);
    }

    public StringBuilder padLeft(int totalWidth, char paddingChar) {
        int currentLength = getLength();
        if (totalWidth <= currentLength) {
            return this;
        }

        StringBuilder result = new StringBuilder(totalWidth);
        for (int index = 0; index < totalWidth - currentLength; index++) {
            result.append(paddingChar);
        }

        result.append(toString());
        return result;
    }

    public StringBuilder padRight(int totalWidth, char paddingChar) {
        int currentLength = getLength();
        if (totalWidth <= currentLength) {
            return this;
        }

        appendRepeated(String.valueOf(paddingChar), totalWidth - currentLength);
        return this;
    }

    public StringBuilder appendCodePoint(int codePoint) {
        append(Character.toChars(codePoint));
        return this;
    }

    public String substringSafe(int startIndex, int length) {
        return toString(startIndex, length);
    }

    public int indexOf(String value) {
        if (value == null || value.isEmpty()) {
            return -1;
        }

        int currentLength = getLength();
        int valueLength = value.length();
        if (valueLength > currentLength) {
            return -1;
        }

        for (int startIndex = 0; startIndex <= currentLength - valueLength; startIndex++) {
            boolean matched = true;
            for (int valueIndex = 0; valueIndex < valueLength; valueIndex++) {
                if (charAt(startIndex + valueIndex) != value.charAt(valueIndex)) {
                    matched = false;
                    break;
                }
            }

            if (matched) {
                return startIndex;
            }
        }

        return -1;
    }

    public int lastIndexOf(String value) {
        if (value == null || value.isEmpty()) {
            return -1;
        }

        int currentLength = getLength();
        int valueLength = value.length();
        if (valueLength > currentLength) {
            return -1;
        }

        for (int startIndex = currentLength - valueLength; startIndex >= 0; startIndex--) {
            boolean matched = true;
            for (int valueIndex = 0; valueIndex < valueLength; valueIndex++) {
                if (charAt(startIndex + valueIndex) != value.charAt(valueIndex)) {
                    matched = false;
                    break;
                }
            }

            if (matched) {
                return startIndex;
            }
        }

        return -1;
    }

    public boolean equalsText(String value) {
        if (value == null) {
            return false;
        }

        int currentLength = getLength();
        if (value.length() != currentLength) {
            return false;
        }

        for (int index = 0; index < currentLength; index++) {
            if (charAt(index) != value.charAt(index)) {
                return false;
            }
        }

        return true;
    }

    public int compareToText(String value) {
        if (value == null) {
            return getLength() == 0 ? 0 : 1;
        }

        int currentLength = getLength();
        int limit = Math.min(currentLength, value.length());

        for (int index = 0; index < limit; index++) {
            int difference = charAt(index) - value.charAt(index);
            if (difference != 0) {
                return difference;
            }
        }

        return currentLength - value.length();
    }

    public StringBuilder ensurePrefix(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return this;
        }

        if (!startsWith(prefix)) {
            insert(0, prefix, 1);
        }

        return this;
    }

    public StringBuilder ensureSuffix(String suffix) {
        if (suffix == null || suffix.isEmpty()) {
            return this;
        }

        if (!endsWith(suffix)) {
            append(suffix);
        }

        return this;
    }

    public StringBuilder removePrefix(String prefix) {
        if (prefix != null && startsWith(prefix)) {
            remove(0, prefix.length());
        }

        return this;
    }

    public StringBuilder removeSuffix(String suffix) {
        if (suffix != null && endsWith(suffix)) {
            remove(getLength() - suffix.length(), suffix.length());
        }

        return this;
    }

    public StringBuilder replaceFirst(String oldValue, String newValue) {
        int index = indexOf(oldValue);
        if (index >= 0) {
            replace(oldValue, newValue, index, oldValue.length());
        }

        return this;
    }

    public StringBuilder replaceLast(String oldValue, String newValue) {
        int index = lastIndexOf(oldValue);
        if (index >= 0) {
            replace(oldValue, newValue, index, oldValue.length());
        }

        return this;
    }

    public StringBuilder trim() {
        trimLeadingWhitespace();
        trimTrailingWhitespace();
        return this;
    }

    public StringBuilder toUpperCaseInPlace() {
        int currentLength = getLength();

        for (int index = 0; index < currentLength; index++) {
            setCharAt(index, Character.toUpperCase(charAt(index)));
        }

        return this;
    }

    public StringBuilder toLowerCaseInPlace() {
        int currentLength = getLength();

        for (int index = 0; index < currentLength; index++) {
            setCharAt(index, Character.toLowerCase(charAt(index)));
        }

        return this;
    }

    public StringBuilder collapseWhitespace() {
        int currentLength = getLength();
        int writeIndex = 0;
        boolean lastWasWhitespace = false;

        for (int readIndex = 0; readIndex < currentLength; readIndex++) {
            char currentChar = charAt(readIndex);
            if (Character.isWhitespace(currentChar)) {
                if (!lastWasWhitespace) {
                    if (writeIndex != readIndex) {
                        setCharAt(writeIndex, ' ');
                    }
                    writeIndex++;
                    lastWasWhitespace = true;
                }
            } else {
                if (writeIndex != readIndex) {
                    setCharAt(writeIndex, currentChar);
                }
                writeIndex++;
                lastWasWhitespace = false;
            }
        }

        if (writeIndex < currentLength) {
            setLength(writeIndex);
        }

        return this;
    }

    public StringBuilder appendJoined(Iterable<String> values, String separator) {
        if (values == null) {
            return this;
        }

        String actualSeparator = separator == null ? "" : separator;
        boolean firstValue = true;

        for (String currentValue : values) {
            if (!firstValue) {
                append(actualSeparator);
            }

            if (currentValue != null) {
                append(currentValue);
            }

            firstValue = false;
        }

        return this;
    }

    public String[] split(char separator) {
        java.util.ArrayList<String> parts = new java.util.ArrayList<String>();
        int currentLength = getLength();
        int startIndex = 0;

        for (int index = 0; index < currentLength; index++) {
            if (charAt(index) == separator) {
                parts.add(toString(startIndex, index - startIndex));
                startIndex = index + 1;
            }
        }

        parts.add(toString(startIndex, currentLength - startIndex));
        return parts.toArray(new String[0]);
    }

    public String[] splitLines() {
        java.util.ArrayList<String> lines = new java.util.ArrayList<String>();
        int currentLength = getLength();
        int startIndex = 0;

        for (int index = 0; index < currentLength; index++) {
            char currentChar = charAt(index);
            if (currentChar == '\n') {
                int lineLength = index - startIndex;
                if (lineLength > 0 && charAt(index - 1) == '\r') {
                    lineLength--;
                }
                lines.add(toString(startIndex, lineLength));
                startIndex = index + 1;
            }
        }

        if (startIndex <= currentLength) {
            lines.add(toString(startIndex, currentLength - startIndex));
        }

        return lines.toArray(new String[0]);
    }

    public StringBuilder appendQuoted(String value) {
        append('"');
        if (value != null) {
            appendEscapedJson(value);
        }
        append('"');
        return this;
    }

    public StringBuilder appendEscapedJson(String value) {
        if (value == null) {
            return this;
        }

        for (int index = 0; index < value.length(); index++) {
            char currentChar = value.charAt(index);
            switch (currentChar) {
                case '"':
                    append("\\\"");
                    break;
                case '\\':
                    append("\\\\");
                    break;
                case '\b':
                    append("\\b");
                    break;
                case '\f':
                    append("\\f");
                    break;
                case '\n':
                    append("\\n");
                    break;
                case '\r':
                    append("\\r");
                    break;
                case '\t':
                    append("\\t");
                    break;
                default:
                    if (currentChar < 32) {
                        String hexValue = Integer.toHexString(currentChar).toUpperCase();
                        append("\\u");
                        for (int paddingIndex = hexValue.length(); paddingIndex < 4; paddingIndex++) {
                            append('0');
                        }
                        append(hexValue);
                    } else {
                        append(currentChar);
                    }
                    break;
            }
        }

        return this;
    }

    public StringBuilder appendHexDump(byte[] data) {
        if (data == null || data.length == 0) {
            return this;
        }

        char[] hexDigits = "0123456789ABCDEF".toCharArray();
        for (int index = 0; index < data.length; index++) {
            int unsignedValue = data[index] & 0xFF;
            append(hexDigits[unsignedValue >>> 4]);
            append(hexDigits[unsignedValue & 0x0F]);
            if (index + 1 < data.length) {
                append(' ');
            }
        }

        return this;
    }

    public StringBuilder appendIndentedLines(String value, String indent) {
        if (value == null) {
            return this;
        }

        String actualIndent = indent == null ? "" : indent;
        String[] lines = new StringBuilder(value).splitLines();

        for (int index = 0; index < lines.length; index++) {
            append(actualIndent);
            append(lines[index]);
            if (index + 1 < lines.length) {
                appendLine();
            }
        }

        return this;
    }

    public StringBuilder retainLettersAndDigits() {
        int currentLength = getLength();
        int writeIndex = 0;

        for (int readIndex = 0; readIndex < currentLength; readIndex++) {
            char currentChar = charAt(readIndex);
            if (Character.isLetterOrDigit(currentChar)) {
                if (writeIndex != readIndex) {
                    setCharAt(writeIndex, currentChar);
                }
                writeIndex++;
            }
        }

        if (writeIndex < currentLength) {
            setLength(writeIndex);
        }

        return this;
    }

    public StringBuilder padCenter(int totalWidth, char paddingChar) {
        int currentLength = getLength();
        if (totalWidth <= currentLength) {
            return this;
        }

        int totalPadding = totalWidth - currentLength;
        int leftPadding = totalPadding / 2;
        int rightPadding = totalPadding - leftPadding;
        String originalValue = toString();

        clear();
        for (int index = 0; index < leftPadding; index++) {
            append(paddingChar);
        }
        append(originalValue);
        for (int index = 0; index < rightPadding; index++) {
            append(paddingChar);
        }

        return this;
    }

    public StringBuilder repeat(int repeatCount) {
        if (repeatCount <= 0) {
            clear();
            return this;
        }

        String originalValue = toString();
        for (int index = 1; index < repeatCount; index++) {
            append(originalValue);
        }

        return this;
    }

    public int countOccurrences(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }

        int occurrenceCount = 0;
        int searchIndex = 0;
        int currentLength = getLength();
        int valueLength = value.length();

        while (searchIndex <= currentLength - valueLength) {
            boolean matched = true;
            for (int valueIndex = 0; valueIndex < valueLength; valueIndex++) {
                if (charAt(searchIndex + valueIndex) != value.charAt(valueIndex)) {
                    matched = false;
                    break;
                }
            }

            if (matched) {
                occurrenceCount++;
                searchIndex += valueLength;
            } else {
                searchIndex++;
            }
        }

        return occurrenceCount;
    }

    public StringBuilder appendWrapped(String prefix, String value, String suffix) {
        if (prefix != null) {
            append(prefix);
        }

        if (value != null) {
            append(value);
        }

        if (suffix != null) {
            append(suffix);
        }

        return this;
    }

    public StringBuilder removeAll(String value) {
        if (value == null || value.isEmpty()) {
            return this;
        }

        int currentIndex = indexOf(value);
        while (currentIndex >= 0) {
            remove(currentIndex, value.length());
            currentIndex = indexOf(value);
        }

        return this;
    }

    public StringBuilder compactRepeatedCharacters() {
        int currentLength = getLength();
        if (currentLength <= 1) {
            return this;
        }

        int writeIndex = 1;
        char previousChar = charAt(0);

        for (int readIndex = 1; readIndex < currentLength; readIndex++) {
            char currentChar = charAt(readIndex);
            if (currentChar != previousChar) {
                if (writeIndex != readIndex) {
                    setCharAt(writeIndex, currentChar);
                }
                writeIndex++;
                previousChar = currentChar;
            }
        }

        if (writeIndex < currentLength) {
            setLength(writeIndex);
        }

        return this;
    }
}