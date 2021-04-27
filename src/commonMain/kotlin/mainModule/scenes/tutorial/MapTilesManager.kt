package mainModule.scenes.tutorial

import com.soywiz.kds.IntArray2
import com.soywiz.korge.tiled.TiledMapView
import com.soywiz.korge.view.tiles.TileMap
import com.soywiz.korma.geom.Point
import utils.tiledMapView.Layer
import utils.*

class MapTilesManager(private val map: TiledMapView) {
    companion object {
        const val EMPTY = 0
    }

    val playerPos get() = get(Layer.GameObjects).getPositionsWithValue(1).first().toPoint() // TODO: remove magic value 1

    fun forEachObject(layer: Layer, callback: (Point, Int) -> Unit) = get(layer).run {
        repeat(height) { y ->
            repeat(width) { x ->
                if (get(x, y) != EMPTY)
                    callback(Point(x, y), get(x, y))
            }
        }
    }

    operator fun get(layer: Layer) = (map[layer.index] as TileMap).intMap

    operator fun get(x: Int, y: Int, layer: Layer) = get(layer)[x, y]

    operator fun get(square: ClosedRange<Point>, layer: Layer) =
        List(square.height) { y ->
            List(square.width) { x ->
                (Point(x, y) + square.start).run {
                    get(xi, yi, layer)
                }
            }
        }

    operator fun set(x: Int, y: Int, layer: Layer, value: Int) = get(layer).set(x, y, value).also { map.invalidate() }

    operator fun set(points: Iterator<Point>, layer: Layer, value: Int) = get(layer).run {
        points.forEach { p ->
            set(p.xi, p.yi, value)
        }
    }.also {
        map.invalidate()
    }

    operator fun set(points: Iterable<Point>, layer: Layer, value: Int) = set(points.iterator(), layer, value)

    operator fun set(points: ClosedRange<Point>, layer: Layer, values: IntArray2) {
        points.iterator().forEach { p ->
            set(p.xi, p.yi, layer, (p - points.start).run { values[xi, yi] })
        }
    }
}