package lib.tiledMapView

import com.soywiz.korge.tiled.TiledMapView
import com.soywiz.korge.view.View
import com.soywiz.korge.view.tiles.TileMap
import com.soywiz.korma.geom.Point
import mainModule.MainModule
import lib.extensions.sizePoint

operator fun TiledMapView.set(x: Int, y: Int, layer: Int = 0, value: Int) {
    if (layer < size)
        (get(layer) as TileMap).intMap[x, y] = value
    else
        TODO("add adding new layers?")
}

val TiledMapView.tilesArea get() = sizePoint / tileset.run { Point(width, height) }

fun TiledMapView.getTilesArea(area: ClosedRange<Point>) = toTilePoint(area.start)..toTilePoint(area.endInclusive)

fun TiledMapView.toTilePoint(point: Point) = (point - MainModule.size.size.p / 2) / Point(tileset.width, tileset.height)

fun <R> TiledMapView.childrenMap(callback: (View) -> R) = List(size) { i -> callback(get(i)) }
