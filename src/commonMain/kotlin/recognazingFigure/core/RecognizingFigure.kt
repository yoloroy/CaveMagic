package recognazingFigure.core

import recognazingFigure.adapters.Color
import recognazingFigure.adapters.color
import recognazingFigure.figures.MASK_SIZES
import recognazingFigure.core.*
import recognazingFigure.figures.Figure
import recognazingFigure.figures.figureMasks

val List<Point>.figure: Figure
    get() {
        val start = Point(
            minByOrNull { it.x }!!.x,
            minByOrNull { it.y }!!.y
        )
        val size = Point(
            maxByOrNull { it.x }!!.x + 1 - start.x,
            maxByOrNull { it.y }!!.y + 1 - start.y
        )
        println("$start $size")

        val pic = ColorMatrix(size.x, size.y, Color.WHITE.rgb).apply {
            this@figure.forEach {
                setRGB(it.x - start.x, it.y - start.y, Color.BLACK.rgb)
            }
        }

        return pic.figure
    }

val ColorMatrix.figure: Figure
    get() {
        val compressedPic = ColorMatrix(MASK_SIZES, MASK_SIZES).apply {
            loadFrom(this@figure)

            fitToTheEdges()
        }

        val points = buildList<Point> {
            compressedPic.foreachIndexed { x, y ->
                if (value.rgb == Color.BLACK.value.rgb)
                    add(Point(x, y))
            }
        }

        return Figure.figures.maxByOrNull { figure ->
            val figurePic = figureMasks[figure]!!

            getSimilarityValue(points, figurePic)
        }!!
    }

private fun getSimilarityValue(points: List<Point>, pic: ColorMatrix): Double {
    var counter = 0
    points.forEach {
        if (pic.getRGB(it.x, it.y).color.rgb == Color.BLACK.rgb) {
            pic.setRGB(it.x, it.y, Color.RED.rgb)
            counter++
        } else {
            pic.setRGB(it.x, it.y, Color.GRAY.rgb)
        }
    }

    return counter.toDouble() / points.size
}

fun ColorMatrix.loadFrom(pic: ColorMatrix) {
    val sizeRatio = Point(pic.width / MASK_SIZES, pic.height / MASK_SIZES)

    foreachIndexed { x, y ->
        setRGB(x, y, Color.WHITE.rgb)
    }

    pic.foreachIndexed { xi, yi ->
        val x = (xi.toDouble() / sizeRatio.x).toInt().let {
            if (it >= MASK_SIZES)
                MASK_SIZES - 1
            else
                it
        }
        val y = (yi.toDouble() / sizeRatio.y).toInt().let {
            if (it >= MASK_SIZES)
                MASK_SIZES - 1
            else
                it
        }

        if (red < 128 && green < 128 && blue < 128)
            setRGB(x, y, Color.BLACK.rgb)
    }

}

fun ColorMatrix.fitToTheEdges() {
    val (topLeft, bottomRight) = findBounds()
    val boundsSize = bottomRight - topLeft
    val boundsRatioX = MASK_SIZES / (boundsSize.getX())
    val boundsRatioY = MASK_SIZES / (boundsSize.getY())

    val matrix = Array(MASK_SIZES) { Array(MASK_SIZES) { 0xffffff } }

    foreachIndexed { x, y ->
        var point = Point(x, y)

        if (point in topLeft to bottomRight && rgb == Color.BLACK.rgb) {
            point -= topLeft
            point.x = (point.x * boundsRatioX).toInt().let {
                if (it >= MASK_SIZES)
                    MASK_SIZES - 1
                else
                    it
            }
            point.y = (point.y * boundsRatioY).toInt().let {
                if (it >= MASK_SIZES)
                    MASK_SIZES - 1
                else
                    it
            }

            matrix[point] = Color.BLACK.rgb
        }
    }

    foreachIndexed { x, y ->
        setRGB(x, y, matrix[y][x])
    }
}

fun ColorMatrix.findBounds(): Pair<Point, Point> {
    var topBound = height
    var bottomBound = 0
    var leftBound = width
    var rightBound = 0

    foreachIndexed { x, y ->
        if (rgb == Color.BLACK.rgb) {
            if (topBound > y) topBound = y
            if (bottomBound < y) bottomBound = y
            if (leftBound > x) leftBound = x
            if (rightBound < x) rightBound = x
        }
    }

    return Point(leftBound, topBound) to Point(rightBound, bottomBound)
}

inline fun <E> buildList(block: MutableList<E>.() -> Unit): MutableList<E> = mutableListOf<E>().apply(block)
