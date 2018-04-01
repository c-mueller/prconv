package xyz.cmueller.prconv.model

import org.json.JSONObject

open class PrintRObject : PrintRElement() {
    override fun toJSONValue(): Any {
        val outObject = JSONObject()
        children.forEach { k, v ->
            outObject.put(k, v.toJSONValue())
        }
        return outObject
    }

    fun toJSON() = toJSONValue() as JSONObject

    val children: MutableMap<String, PrintRElement> = HashMap()

}