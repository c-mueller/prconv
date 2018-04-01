package xyz.cmueller.prconv.model

import org.json.JSONArray

class PrintRArray : PrintRElement() {
    override fun toJSONValue(): Any {
        val jsonArray = JSONArray()
        objects.forEach { e -> jsonArray.put(e.toJSONValue()) }
        return jsonArray
    }

    val objects: MutableList<PrintRElement> = ArrayList()
}