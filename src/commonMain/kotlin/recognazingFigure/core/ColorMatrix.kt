package recognazingFigure.core

import recognazingFigure.adapters.Color
import recognazingFigure.adapters.color

typealias ColorMatrix = Array<Array<Color>>

fun ColorMatrix(width: Int, height: Int, rgb: Int = 0): ColorMatrix = Array(height) { Array(width) { Color(rgb) } }

val ColorMatrix.height get() = size
val ColorMatrix.width get() = first().size

fun ColorMatrix.foreachIndexed(callback: Color.(x: Int, y: Int) -> Unit) {
    for (x in 0 until width)
        for (y in 0 until height)
            getRGB(x, y).color.callback(x, y)
}

fun ColorMatrix.getRGB(x: Int, y: Int) = get(x, y).rgb

operator fun ColorMatrix.get(x: Int, y: Int) = get(y)[x]

fun ColorMatrix.setRGB(x: Int, y: Int, rgb: Int) = set(Point(x, y), rgb.color)

operator fun <T> Array<Array<T>>.set(point: Point, value: T) {
    this[point.y][point.x] = value
}
