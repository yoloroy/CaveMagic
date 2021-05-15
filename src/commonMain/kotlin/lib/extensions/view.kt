package lib.extensions

import com.soywiz.korge.ui.UIView
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.View
import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.size
import com.soywiz.korma.geom.Point

val View.size get() = Point(width, height)

val View.scaledSize get() = size * scalePoint

val View.scalePoint get() = Point(scaleX,  scaleY)

fun View.size(point: Point) = size(point.x, point.y)
