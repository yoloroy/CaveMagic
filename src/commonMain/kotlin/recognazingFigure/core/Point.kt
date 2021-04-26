package recognazingFigure.core

data class Point(var x_: Double, var y_: Double) {
    constructor(x: Int, y: Int) : this(x.toDouble(), y.toDouble())

    var x: Int
        get() = x_.toInt()
        set(value) { x_ = value.toDouble() }

    var y: Int
        get() = y_.toInt()
        set(value) { y_ = value.toDouble() }

    fun getX() = x_
    fun getY() = y_

    operator fun plus(other: Point): Point = Point(this.x + other.x, this.y + other.y)

    operator fun minus(other: Point): Point = Point(this.x - other.x, this.y - other.y)

    operator fun div(i: Int): Point = Point(x / i, y / i)
}

operator fun Pair<Point, Point>.contains(point: Point): Boolean =
    point.x in first.x..second.x && point.y in first.y..second.y
