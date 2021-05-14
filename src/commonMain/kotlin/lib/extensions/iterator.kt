package lib.extensions

operator fun <T> Iterator<T>.plus(other: Iterator<T>) = iterator {
    forEach {
        yield(it)
    }
    other.forEach {
        yield(it)
    }
}
