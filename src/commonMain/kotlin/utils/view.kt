package utils

import com.soywiz.korge.view.View
import com.soywiz.korma.geom.Point

val View.size get() = Point(width, height)

val View.scaledSize get() = size * scalePoint

val View.scalePoint get() = Point(scaleX,  scaleY)
