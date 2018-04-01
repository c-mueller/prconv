package xyz.cmueller.prconv.model

open class PrintRObject : PrintRElement() {
    val children: MutableMap<String, PrintRElement> = HashMap()

}