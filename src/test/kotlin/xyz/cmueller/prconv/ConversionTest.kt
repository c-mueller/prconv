package xyz.cmueller.prconv

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import xyz.cmueller.prconv.model.PrintRArray
import xyz.cmueller.prconv.model.PrintRObject
import xyz.cmueller.prconv.model.PrintRValue
import xyz.cmueller.prconv.util.constants.OBJECT_DEFINITON_DELIMITER

class ConversionTest {

    private lateinit var converter: PrintRConverter

    @Before
    fun setUp() {
        converter = PrintRConverter()
    }

    @Test
    fun testParser_SimpleNotNested() {
        val result = loadAndParseObject("/samples/simple.txt")

        Assert.assertEquals(13, result.children.size)
        Assert.assertEquals(PrintRValue("8f2e2a9cb599d2357f198060821b2526b19cc284"), result.children["rr_id"])
    }
    @Test
    fun testParser_SimpleNested() {
        val result = loadAndParseObject("/samples/simple_nested.txt")

        Assert.assertEquals(1, result.children.size)
    }

    @Test
    fun testParser_Array() {
        val result = loadAndParseObject("/samples/array.txt")

        Assert.assertEquals(2, result.children.size)
    }

    @Test
    fun testParser_NestedArrays() {
        val result = loadAndParseObject("/samples/array_nested.txt")

        Assert.assertEquals(2, result.children.size)
        Assert.assertEquals(true, result.children["int_array"] is PrintRArray)
        val intarr = result.children["int_array"] as PrintRArray
        intarr.objects.forEach { e -> Assert.assertTrue(e.toJSONValue() is Long) }

        val nestarr = result.children["array_of_arrays"] as PrintRArray
        nestarr.objects.forEach { e ->
            Assert.assertTrue(e is PrintRArray)
            val innerArr = e as PrintRArray
            innerArr.objects.forEach { e -> Assert.assertTrue(e.toJSONValue() is Long) }
        }
    }

    @Test
    fun testParser_ComplexOutput() {
        val result = loadAndParseObject("/samples/array_complex.txt")

        Assert.assertEquals(5, result.children.size)
    }

    @Test
    fun testConversion_Types() {
        val result = loadAndParseObject("/samples/types.txt")

        Assert.assertEquals(5, result.children.size)

        val jsonOut = result.toJSON()
        Assert.assertTrue(jsonOut.has("boola"))
        Assert.assertTrue(jsonOut.getBoolean("boola"))
        Assert.assertTrue(jsonOut.has("boolb"))
        Assert.assertFalse(jsonOut.getBoolean("boolb"))
    }

    @Test(expected = ParsingException::class)
    fun testParser_Array_InvalidFile() {
        loadAndParseObject("/samples/array_error.txt")
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

    private fun loadAndParseObject(path: String): PrintRObject {
        println("Reading Sample File")
        val input = javaClass.getResourceAsStream(path)
        val data = String(input.readBytes())
        val result = converter.analyze(data)
        println("Json Result:")
        println(result.toJSON().toString())
        return result
    }
}