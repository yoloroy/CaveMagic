package utils

inline fun <T> List<T>.onNotEmpty(block: List<T>.() -> Unit) = if (isNotEmpty()) block() else Unit
