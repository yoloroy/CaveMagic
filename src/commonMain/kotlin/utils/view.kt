package utils

import com.soywiz.korge.ui.UIView
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.View
import com.soywiz.korge.view.addTo
import com.soywiz.korma.geom.Point

val View.size get() = Point(width, height)

val View.scaledSize get() = size * scalePoint

val View.scalePoint get() = Point(scaleX,  scaleY)

inline fun Container.uiView(
    width: Double = 90.0,
    height: Double = 32.0,
    block: UIView.() -> Unit
): UIView = UIView(width, height).addTo(this).apply(block)
