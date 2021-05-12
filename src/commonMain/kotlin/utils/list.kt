package utils

inline fun <T> List<T>.runIfNotEmpty(block: List<T>.() -> Unit) = if (isNotEmpty()) block() else Unit
