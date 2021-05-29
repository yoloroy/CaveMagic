package logic.inventory.widgets

import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.*
import com.soywiz.korma.geom.Point
import lib.extensions.size
import lib.extensions.y
import logic.inventory.item.Item
import kotlin.math.min

fun Container.inventoryListElement(item: Item, elementSize: Point) = container {
    val icon = image(item.icon) {
        smoothing = false
        size(elementSize.run { Point(min(x, y)) })
    }

    val name = text(item.name) {
        position(Point(icon.size.x, .0))
        size(elementSize.x - icon.width, elementSize.y / 2)
    }
    text(item.description) {
        position(name.pos + name.height.y)
        size(elementSize.x - icon.width, elementSize.y / 2)
    }

    onClick {
        item.action()
    }
}
