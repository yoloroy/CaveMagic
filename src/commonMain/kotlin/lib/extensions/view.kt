package lib.extensions

import com.soywiz.korge.view.*
import com.soywiz.korma.geom.Point

val View.size get() = Point(width, height)

val Anchorable.anchor get() = Point(anchorX, anchorY)

val View.scaledSize get() = size * scalePoint

val View.scalePoint get() = Point(scaleX,  scaleY)

fun View.size(point: Point) = size(point.x, point.y)
