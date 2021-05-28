package mainModule.widgets

import com.soywiz.korge.view.Container
import com.soywiz.korge.view.View
import com.soywiz.korge.view.container
import com.soywiz.korge.view.position
import com.soywiz.korma.geom.Point
import lib.extensions.size
import lib.extensions.y

inline fun <T> Container.listView(
    values: Collection<T>,
    position: Point,
    resultSize: Point,
    ySpacing: Double = 0.0,
    createView: Container.(T) -> View
) = container {
    position(position)

    val elementSize = resultSize.copy().apply { y /= values.size }
    val lastPosition = Point()

    values.forEach { value ->
        createView(value).apply {
            size(elementSize)
            position(lastPosition)
            lastPosition += elementSize * Point(0, 1) + ySpacing.y
        }
    }
}
