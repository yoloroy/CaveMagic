package lib.extensions

fun <A, R> List<(A) -> R>.callAll(arg1: A) = forEach { it(arg1) }

fun <A, B, R> List<(A, B) -> R>.callAll(arg1: A, arg2: B) = forEach { it(arg1, arg2) }
