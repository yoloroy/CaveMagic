package logic.inventory.widgets

import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.Bitmap32
import com.soywiz.korim.color.Colors.LIGHTGRAY
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korma.geom.Point
import lib.extensions.size
import logic.inventory.item.Item
import kotlin.math.min

fun Container.inventoryListElement(
    items: MutableCollection<Item>,
    item: Item,
    elementSize: Point
) = image(Bitmap32(1, 1, LIGHTGRAY)) {
    name = "inventoryListElement"

    val icon = image(item.icon) {
        name = "icon"
        anchor(0, 0)
        smoothing = false
        size(elementSize.run { Point(min(x, y) / 2) })
    }

    text(item.name) {
        name = "name"
        position(Point(icon.scaledWidth, .0))
        size(elementSize.x - icon.scaledWidth, elementSize.y / 2)
        textSize = height * 0.9
        alignment = TextAlignment.TOP_LEFT
    }
    text(item.description) {
        name = "description"
        position(elementSize * Point(.0, 0.5))
        size(elementSize.x, elementSize.y / 2)
        textSize = height * 0.9
        alignment = TextAlignment.TOP_LEFT
    }

    onClick {
        item.action()
        items.remove(item)
        removeFromParent()

        if (parent!!.numChildren == 0) {
            parent!!.removeFromParent()
        }
    }
}
