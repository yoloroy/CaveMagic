package logic.inventory.widgets

import com.soywiz.korge.view.Container
import com.soywiz.korma.geom.Point
import logic.inventory.item.Item
import mainModule.widgets.listView

fun Container.inventoryListView(
    items: MutableCollection<Item>,
    position: Point,
    resultSize: Point
) = listView(items, position, resultSize) {
    inventoryListElement(items, it, resultSize / Point(1, items.size))
}
