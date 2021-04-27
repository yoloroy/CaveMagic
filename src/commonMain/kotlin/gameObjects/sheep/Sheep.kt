package gameObjects.sheep

import com.soywiz.korge.tiled.TiledMapView
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korma.geom.Point
import gameObjects.gameObject.GameObject
import utils.*
import utils.tiledMapView.tilesArea
import kotlin.math.absoluteValue
import kotlin.math.sign

class Sheep(
    stage: Stage,
    private val map: TiledMapView,
    bitmap: Bitmap,
    override val pos: Point = Point(0, 0)
) : GameObject() {
    private var view: Image = stage.image(bitmap) {
        smoothing = false
        size(16, 16)
        scale(scale)
        setPositionRelativeTo(map, this@Sheep.pos * size * scale + map.pos)
    }

    private var isDeleted = false

    private var destination: Point = pos
        get() {
            return if (field == pos)
                map.tilesArea.run { // move to controller
                    (pos + Point((-5..+5).random(), (-5..+5).random()))
                        .clamp(Point(0, 0)..Point(x, y))
                }.also {
                    field = it
                }
            else
                field
        }

    override fun delete() {
        view.removeFromParent()
        view.removeAllComponents()
        isDeleted = true
    }

    override fun makeTurn() {
        if (!isDeleted) {
            val delta = destination - pos

            pos += if (delta.x.absoluteValue > delta.y.absoluteValue) {
                Point.Horizontal * delta.x.sign
            } else {
                Point.Vertical * delta.y.sign
            }

            view.apply {
                setPositionRelativeTo(map, this@Sheep.pos * size + map.pos)
            }
        }
    }
}
