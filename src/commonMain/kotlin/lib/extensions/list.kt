package lib.extensions

inline fun <T> List<T>.runIfNotEmpty(block: List<T>.() -> Unit) = if (isNotEmpty()) block() else Unit