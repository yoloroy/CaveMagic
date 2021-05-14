package lib.widgets

import com.soywiz.korge.view.*
import com.soywiz.korim.color.RGBA
import com.soywiz.korma.geom.Point
import lib.extensions.x
import lib.extensions.xi

private val DEFAULT_SIZE = Point(20, 4)
private val DEFAULT_POSITION = Point(0) + DEFAULT_SIZE.xi.x / 2
private val DEFAULT_COLOR = RGBA(0xff, 0xff, 0xff, 0xff)

fun Container.valueBar(
    limit: Int, value: Int = limit,
    size: Point = DEFAULT_SIZE,
    position: Point = DEFAULT_POSITION,
    color: RGBA = DEFAULT_COLOR
) = ValueBar(this, limit, value, size, position, color)

open class ValueBar(
    container: Container,
    limit: Int,
    value: Int = limit,
    val size: Point = DEFAULT_SIZE,
    position: Point = DEFAULT_POSITION,
    color: RGBA = DEFAULT_COLOR
) {
    var limit = limit
        set(value) {
            field = value
            notifyView()
        }
    var value = value
        set(value) {
            field = value
            notifyView()
        }

    private val string get() = "$value/$limit"

    private val text: Text = container.text(string, color = color) {
        size(this@ValueBar.size.x, this@ValueBar.size.y)
        position(position)
    }

    private fun notifyView() {
        text.text = string
    }
}
