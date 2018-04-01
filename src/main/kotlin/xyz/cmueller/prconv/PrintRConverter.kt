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
        failOnInvalidInput(lines, startIdx)

        val outputObject = PrintRObject()

        var currentIndex = startIdx + 2
        while (currentIndex < lines.size) {
            val currentLine = lines[currentIndex]
            val currentLineSeparated = currentLine.cleanupAndSplit()

            if (currentLineSeparated.size > 1) {
                val key = currentLineSeparated[0].replace(" ", "")
                val value = currentLineSeparated[1].trim()
                when {
                    value == OBJECT_DEFINITON_DELIMITER -> {
                        val childPair = parseRootObject(lines, currentIndex)
                        currentIndex = childPair.second
                        outputObject.children.put(key, childPair.first)
                    }
                    value == ARRAY_DEFINITON_DELIMITER -> {
                        val arrayChildPair = parseArray(lines, currentIndex)
                        currentIndex = arrayChildPair.second
                        outputObject.children.put(key, arrayChildPair.first)
                    }
                    currentLine.contains(VALUE_DEFINITION_DELIMITER) -> {
                        outputObject.children.put(key, PrintRValue(value))
                    }
                }
            } else if (currentLineSeparated[0] == OBJECT_CLOSE_DELIMITER) {
                return Pair(outputObject, currentIndex)
            }
            currentIndex++
        }
        throw ParsingException("Parsing Failed. Please check opening (')') and closing brackets (')')")
    }

    private fun parseArray(lines: List<String>, startIdx: Int): Pair<PrintRArray, Int> {
        failOnInvalidInput(lines, startIdx, ARRAY_DEFINITON_DELIMITER)

        val outputArray = PrintRArray()

        var currentIndex = startIdx + 2
        while (currentIndex < lines.size) {
            val currentLine = lines[currentIndex]
            val currentLineSeparated = currentLine.cleanupAndSplit()

            if (currentLineSeparated.size > 1) {
                val value = currentLineSeparated[1].trim()
                when {
                    value == OBJECT_DEFINITON_DELIMITER -> {
                        val childPair = parseRootObject(lines, currentIndex)
                        currentIndex = childPair.second
                        outputArray.objects.add(childPair.first)
                    }
                    value.trim() == ARRAY_DEFINITON_DELIMITER -> {
                        val arrayChildPair = parseArray(lines, currentIndex)
                        currentIndex = arrayChildPair.second
                        outputArray.objects.add(arrayChildPair.first)
                    }
                    currentLine.contains(VALUE_DEFINITION_DELIMITER) -> {
                        outputArray.objects.add(PrintRValue(currentLineSeparated[1]))
                    }
                }
            } else if (currentLineSeparated[0] == OBJECT_CLOSE_DELIMITER) {
                return Pair(outputArray, currentIndex)
            }

            currentIndex++
        }

        throw ParsingException("Parsing Failed. Please check opening (')') and closing brackets (')')")
    }

    private fun String.cleanupAndSplit() = this.replace(KEY_OPEN_DELIMITER, "")
        .replace(KEY_CLOSE_DELIMITER, "")
        .replace(" $VALUE_DEFINITION_DELIMITER ", VALUE_DEFINITION_DELIMITER)
        .split(VALUE_DEFINITION_DELIMITER)

    private fun failOnInvalidInput(lines: List<String>, startIdx: Int, delimiter: String = OBJECT_DEFINITON_DELIMITER) {
        if (
            lines.size < 3 ||
            !lines[startIdx].endsWith(delimiter) ||
            lines[startIdx + 1] != OBJECT_OPEN_DELIMITER
        ) {
            throw ParsingException("Invalid Header Format!")
        }
    }
}