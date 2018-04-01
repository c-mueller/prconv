package xyz.cmueller.prconv

import xyz.cmueller.prconv.model.PrintRRootObject
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

    fun analyze(content: String): PrintRRootObject {
        val lines = content.splitAtLineDelimiter().map { e -> e.trimLeadingWhitespace() }
        return parseRootObject(lines).first
    }

    private fun parseRootObject(lines: List<String>, startIdx: Int = 0): Pair<PrintRRootObject, Int> {
        if (
            lines.size < 3 ||
            !lines[startIdx].endsWith(OBJECT_DEFINITON_DELIMITER) ||
            lines[startIdx + 1] != OBJECT_OPEN_DELIMITER
        ) {
            throw ParsingException("Invalid Header Format!")
        }

        var obj = PrintRRootObject()

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
                        obj.chrildren.put(lineParts[0], childPair.first)
                    }
                    lineParts[1].trim() == ARRAY_DEFINITON_DELIMITER -> TODO("NYI")
                    lines[currentIndex].contains(VALUE_DEFINITION_DELIMITER) -> {
                        obj.chrildren.put(lineParts[0], PrintRValue(lineParts[1]))
                    }
                }
            } else {
                when (OBJECT_CLOSE_DELIMITER) {
                    lineParts[0] -> return Pair(obj, currentIndex)
                }
            }
            currentIndex++
        }

        return Pair(obj, currentIndex)
    }

}