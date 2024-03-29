package mainModule.widgets

import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.color.RGBA
import com.soywiz.korma.geom.Point
import lib.extensions.size
import lib.extensions.x
import lib.extensions.xi

fun Container.valueBar(
    limit: Int,
    backgroundTexture: Bitmap? = null,
    value: Int = limit,
    size: Point = ValueBar.DEFAULT_SIZE,
    position: Point = ValueBar.DEFAULT_POSITION,
    color: RGBA = ValueBar.DEFAULT_COLOR
) = ValueBar(this, limit, backgroundTexture, value, size, position + size.xi.x / 2, color)

open class ValueBar(
    container: Container,
    limit: Int,
    backgroundTexture: Bitmap? = null,
    value: Int = limit,
    val size: Point = DEFAULT_SIZE,
    position: Point = DEFAULT_POSITION,
    color: RGBA = DEFAULT_COLOR
) {
    companion object {
        val DEFAULT_SIZE get() = Point(20, 4)
        val DEFAULT_POSITION get() = Point(0)
        val DEFAULT_COLOR get() = RGBA(0xff, 0xff, 0xff, 0xff)
    }

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

    private val background = backgroundTexture?.let {
        container.image(backgroundTexture) {
            smoothing = false
            position(position)
            size(this@ValueBar.size * Point(value.toDouble() / limit, 1.0))
        }
    }

    private val text = container.text(string, color = color) {
        size(this@ValueBar.size.x, this@ValueBar.size.y)
        position(position)
    }

    private fun notifyView() {
        text.text = string
        background?.scaledWidth = size.x * value.toDouble() / limit
    }
}
