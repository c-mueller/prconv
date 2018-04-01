package xyz.cmueller.prconv.model

class PrintRValue(val value: String) : PrintRElement() {
    override fun toJSONValue(): Any {
        when {
            this.value.toLowerCase() == "true" -> {
                return true
            }
            this.value.toLowerCase() == "false" -> {
                return false
            }
            this.value.toLongOrNull() != null -> {
                return this.value.toLong()
            }
            this.value.toDoubleOrNull() != null -> {
                return this.value.toDouble()
            }
            else -> {
                return this.value
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        return other != null && other is PrintRValue && other.value == value
    }

    override fun toString() = this.value
}