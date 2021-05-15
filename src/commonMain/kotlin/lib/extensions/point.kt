package lib.extensions

import com.soywiz.kmem.clamp
import com.soywiz.korma.geom.IPoint
import com.soywiz.korma.geom.Point

operator fun Point.unaryMinus(): Point = Point(-x, -y)

operator fun Point.unaryPlus(): Point = Point(+x, +y)

val IPoint.point get() = Point(this)

val Pair<Point, Point>.center get() = (first + second) / 2

val List<Point>.area: ClosedRange<Point> get() {
    val start = Point(minByOrNull { it.x }!!.x, minByOrNull { it.y }!!.y)
    val end = Point(maxByOrNull { it.x }!!.x, maxByOrNull { it.y }!!.y)

    return start..end
}

fun Point.clamp(range: ClosedRange<Point>): Point = Point(x.clamp(range.x), y.clamp(range.y))

fun Point.setTo(point: Point) = setTo(point.x, point.y)

private fun Double.clamp(pointRange: ClosedRange<Double>): Double = clamp(pointRange.start, pointRange.endInclusive)

operator fun ClosedRange<Point>.minus(pos: Point) = (start - pos)..(endInclusive - pos)

operator fun ClosedRange<Point>.plus(pos: Point) = (start + pos)..(endInclusive + pos)

operator fun ClosedRange<Point>.iterator() = iterator {
    for (y in start.y.toInt()..endInclusive.y.toInt())
        for (x in start.x.toInt()..endInclusive.x.toInt())
            yield(Point(x, y))
}

fun ClosedRange<Point>.firstOrNull(predicate: (Point) -> Boolean): Point? = iterator().firstOrNull(predicate)

fun Iterator<Point>.firstOrNull(predicate: (Point) -> Boolean): Point? {
    forEach {
        if (predicate(it))
            return it
    }
    return null
}

val ClosedRange<Point>.x get() = start.x..endInclusive.x

val ClosedRange<Point>.y get() = start.y..endInclusive.y

val ClosedRange<Point>.width get() = sizePoint.xi + 1

val ClosedRange<Point>.height get() = sizePoint.yi + 1

val ClosedRange<Point>.sizePoint get() = endInclusive - start

val Point.Companion.Horizontal: Point get() = Point(1, 0)

val Point.Companion.Vertical: Point get() = Point(0, 1)

val Point.xi get() = x.toInt()

val Point.yi get() = y.toInt()

val Int.x get() = Point(this, 0)

val Int.y get() = Point(0, this)

val Double.x get() = Point(this, .0)

val Double.y get() = Point(.0, this)

fun <T: Number> Pair<T, T>.toPoint(): Point {
    if (first is Int && second is Int)
        return Point(first as Int, second as Int)

    if (first is Double && second is Double)
        return Point(first as Double, second as Double)

    if (first is Float && second is Float)
        return Point(first as Float, second as Float)

    throw ClassCastException()
}
