package xyz.cmueller.prconv.model

class PrintRValue(val value: String) : PrintRElement() {

    override fun equals(other: Any?): Boolean {
        return other != null && other is PrintRValue && other.value == value
    }

    override fun toString() = this.value
}