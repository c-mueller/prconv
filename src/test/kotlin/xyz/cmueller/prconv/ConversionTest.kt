package xyz.cmueller.prconv

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import xyz.cmueller.prconv.util.constants.OBJECT_DEFINITON_DELIMITER

class ConversionTest {

    private lateinit var converter: PrintRConverter

    @Before
    fun setUp() {
        converter = PrintRConverter()
    }

    @Test
    fun testConversion_SimpleNotNested() {
        println("Reading Sample File")
        val input = javaClass.getResourceAsStream("/samples/simple.txt")
        val data = String(input.readBytes())
        val result = converter.analyze(data)

        Assert.assertEquals(13, result.chrildren.size)

    }

    @Test
    fun testConversion_SimpleNested() {
        println("Reading Sample File")
        val input = javaClass.getResourceAsStream("/samples/simple_nested.txt")
        val data = String(input.readBytes())
        val result = converter.analyze(data)

        Assert.assertEquals(1, result.chrildren.size)

    }

    @Test(expected = ParsingException::class)
    fun testInvalidHeader_ObjectDefinition() {
        converter.analyze("some object\n(\n)")
    }

    @Test(expected = ParsingException::class)
    fun testInvalidHeader_InvalidLength() {
        converter.analyze("$OBJECT_DEFINITON_DELIMITER\n(")
    }

    @Test(expected = ParsingException::class)
    fun testInvalidHeader_InvalidOpenDelimiter() {
        converter.analyze("$OBJECT_DEFINITON_DELIMITER\n)\n)")
    }
}