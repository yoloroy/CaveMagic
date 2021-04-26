package utils

fun IntArray.setAll(values: List<Int>) = values.forEachIndexed { index, value -> set(index, value) }
