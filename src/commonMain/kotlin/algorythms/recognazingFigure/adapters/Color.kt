package algorythms.recognazingFigure.adapters

import com.soywiz.korim.color.RGBA

val Int.color: Color get() = Color(this shr 16 and 0xFF, this shr 8 and 0xFF, this shr 0 and 0xFF)

class Color(rgb: Int = 0) {
    constructor(r: Int, g: Int, b: Int): this() {
        value = RGBA(r, g, b)
    }

    companion object {
        val GRAY get() = Color(0x888888)
        val WHITE get() = Color(0xffffff)
        val RED get() = Color(0xff0000)
        val BLACK get() = Color(0x000000)
    }

    var value: RGBA = RGBA(rgb)

    val rgb: Int get() = value.rgb

    val red: Int get() = value.r
    val green: Int get() = value.g
    val blue: Int get() = value.b

    override fun equals(other: Any?) = if (other is Color) value == other.value else super.equals(other)

    override fun hashCode(): Int = value.hashCode()
}
