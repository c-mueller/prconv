package xyz.cmueller.prconv.util

fun String.trimLeadingWhitespace() = this.trimStart()
fun String.splitAtLineDelimiter() = this.split("\n")