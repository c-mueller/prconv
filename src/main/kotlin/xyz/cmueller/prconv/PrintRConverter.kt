package xyz.cmueller.prconv

import xyz.cmueller.prconv.model.PrintRArray
import xyz.cmueller.prconv.model.PrintRObject
import xyz.cmueller.prconv.model.PrintRValue
import xyz.cmueller.prconv.util.constants.ARRAY_DEFINITON_DELIMITER
import xyz.cmueller.prconv.util.constants.KEY_CLOSE_DELIMITER
import xyz.cmueller.prconv.util.constants.KEY_OPEN_DELIMITER
import xyz.cmueller.prconv.util.constants.OBJECT_CLOSE_DELIMITER
import xyz.cmueller.prconv.util.constants.OBJECT_DEFINITON_DELIMITER
import xyz.cmueller.prconv.util.constants.OBJECT_OPEN_DELIMITER
import xyz.cmueller.prconv.util.constants.VALUE_DEFINITION_DELIMITER
import xyz.cmueller.prconv.util.splitAtLineDelimiter
import xyz.cmueller.prconv.util.trimLeadingWhitespace

class PrintRConverter {

    fun analyze(content: String): PrintRObject {
        val lines = content.splitAtLineDelimiter().map { e -> e.trimLeadingWhitespace() }
        return parseRootObject(lines).first
    }

    private fun parseRootObject(lines: List<String>, startIdx: Int = 0): Pair<PrintRObject, Int> {
        if (
            lines.size < 3 ||
            !lines[startIdx].endsWith(OBJECT_DEFINITON_DELIMITER) ||
            lines[startIdx + 1] != OBJECT_OPEN_DELIMITER
        ) {
            throw ParsingException("Invalid Header Format!")
        }

        var obj = PrintRObject()

        var currentIndex = startIdx + 2
        while (currentIndex < lines.size) {
            var lineParts = lines[currentIndex].replace(KEY_OPEN_DELIMITER, "")
                .replace(KEY_CLOSE_DELIMITER, "")
                .replace(" $VALUE_DEFINITION_DELIMITER ", VALUE_DEFINITION_DELIMITER)
                .split(VALUE_DEFINITION_DELIMITER)

            if (lineParts.size > 1) {
                when {
                    lineParts[1].trim() == OBJECT_DEFINITON_DELIMITER -> {
                        val childPair = parseRootObject(lines, currentIndex)
                        currentIndex = childPair.second
                        obj.children.put(lineParts[0], childPair.first)
                    }
                    lineParts[1].trim() == ARRAY_DEFINITON_DELIMITER -> {
                        val arrayChildPair = parseArray(lines, currentIndex)
                        currentIndex = arrayChildPair.second
                        obj.children.put(lineParts[0], arrayChildPair.first)
                    }
                    lines[currentIndex].contains(VALUE_DEFINITION_DELIMITER) -> {
                        obj.children.put(lineParts[0], PrintRValue(lineParts[1]))
                    }
                }
            } else if (lineParts[0] == OBJECT_CLOSE_DELIMITER) {
                return Pair(obj, currentIndex)
            }
            currentIndex++
        }

        return Pair(obj, currentIndex - 1)
    }

    private fun parseArray(lines: List<String>, startIdx: Int): Pair<PrintRArray, Int> {
        if (
            lines.size < 3 ||
            !lines[startIdx].endsWith(ARRAY_DEFINITON_DELIMITER) ||
            lines[startIdx + 1] != OBJECT_OPEN_DELIMITER
        ) {
            throw ParsingException("Invalid Header Format!")
        }

        var obj = PrintRArray()

        var currentIndex = startIdx + 2
        while (currentIndex < lines.size) {
            var lineParts = lines[currentIndex].replace(KEY_OPEN_DELIMITER, "")
                .replace(KEY_CLOSE_DELIMITER, "")
                .replace(" $VALUE_DEFINITION_DELIMITER ", VALUE_DEFINITION_DELIMITER)
                .split(VALUE_DEFINITION_DELIMITER)

            if (lineParts.size > 1) {
                when {
                    lineParts[1].trim() == OBJECT_DEFINITON_DELIMITER -> {
                        val childPair = parseRootObject(lines, currentIndex)
                        currentIndex = childPair.second
                        obj.objects.add(childPair.first)
                    }
                    lineParts[1].trim() == ARRAY_DEFINITON_DELIMITER -> {
                        val arrayChildPair = parseArray(lines, currentIndex)
                        currentIndex = arrayChildPair.second
                        obj.objects.add(arrayChildPair.first)
                    }
                    lines[currentIndex].contains(VALUE_DEFINITION_DELIMITER) -> {
                        obj.objects.add(PrintRValue(lineParts[1]))
                    }
                }
            } else if (lineParts[0] == OBJECT_CLOSE_DELIMITER) {
                return Pair(obj, currentIndex)
            }

            currentIndex++
        }

        return Pair(obj, currentIndex - 1)
    }

}