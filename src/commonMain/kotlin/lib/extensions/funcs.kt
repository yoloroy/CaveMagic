package lib.extensions

typealias SLambda00 = suspend () -> Unit

infix operator fun SLambda00.plus(other: SLambda00): SLambda00 = { this(); other() }
