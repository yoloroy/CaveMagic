package lib.extensions

import com.soywiz.korge.view.Container
import com.soywiz.korma.geom.Point

val Container.sizePoint get() = Point(width, height)
