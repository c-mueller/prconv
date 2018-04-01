package xyz.cmueller.prconv.model

open class PrintRRootObject : PrintRElement() {
    val chrildren: MutableMap<String, PrintRElement> = HashMap()

}