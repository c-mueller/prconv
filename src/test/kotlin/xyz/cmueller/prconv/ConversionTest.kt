package xyz.cmueller.prconv

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import xyz.cmueller.prconv.model.PrintRValue
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

        Assert.assertEquals(13, result.children.size)
        Assert.assertEquals(PrintRValue("8f2e2a9cb599d2357f198060821b2526b19cc284"), result.children["rr_id"])
    }

    @Test
    fun testConversion_SimpleNested() {
        println("Reading Sample File")
        val input = javaClass.getResourceAsStream("/samples/simple_nested.txt")
        val data = String(input.readBytes())
        val result = converter.analyze(data)

        Assert.assertEquals(1, result.children.size)
    }

    @Test
    fun testConversion_Array() {
        println("Reading Sample File")
        val input = javaClass.getResourceAsStream("/samples/array.txt")
        val data = String(input.readBytes())
        val result = converter.analyze(data)

        Assert.assertEquals(2, result.children.size)
    }

    @Test
    fun testConversion_Array2() {
        println("Reading Sample File")
        val input = javaClass.getResourceAsStream("/samples/array2.txt")
        val data = String(input.readBytes())
        val result = converter.analyze(data)

        Assert.assertEquals(5, result.children.size)
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