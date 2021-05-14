package lib.math

import com.soywiz.korma.geom.Point
import lib.extensions.iterator
import kotlin.math.pow

data class MCircle(val center: Point, val radius: Double) {
    val points get() = iterator {
        val area = (center - Point(radius, radius))..(center + Point(radius, radius))

        for (p in area)
            if (this@MCircle.contains(p))
                yield(p)
    }

    operator fun contains(point: Point) = (point.x-center.x).pow(2) + (point.y - center.y).pow(2) < radius.pow(2)
}
