package lib.math

import com.soywiz.korma.geom.PointInt
import lib.extensions.delta
import kotlin.math.sign

// algorithm: https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
fun intPointsLineIterator(start: PointInt, end: PointInt) = iterator { // TODO: refactor
    val delta = (end to start).delta

    run {
        var error = 0.0
        val deltaError = (delta.y + 1).toDouble() / (delta.x + 1)

        var y = start.y
        val dirY = (end.y - start.y).sign

        for (x in (start.x..end.x).includeBackwards) {
            yield(PointInt(x, y))

            error += deltaError
            if (error >= 1) {
                y += dirY
                error--
            }
        }
    }
    run {
        var error = 0.0
        val deltaError = (delta.x + 1).toDouble() / (delta.y + 1)

        var x = start.x
        val dirX = (end.x - start.x).sign

        for (y in (start.y..end.y).includeBackwards) {
            yield(PointInt(x, y))

            error += deltaError
            if (error >= 1) {
                x += dirX
                error--
            }
        }
    }
}

val IntRange.includeBackwards get() = iterator {
    val unit = (endInclusive - start).sign

    var i = start
    do {
        yield(i)

        i += unit
    } while (i != endInclusive)
}
