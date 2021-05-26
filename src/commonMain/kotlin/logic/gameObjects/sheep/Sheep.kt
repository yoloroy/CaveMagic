package logic.gameObjects.sheep

import com.soywiz.korge.view.Container
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korma.geom.Point
import lib.extensions.*
import lib.tiledMapView.Layer
import logic.gameObjects.gameObject.GameObject
import logic.gameObjects.gameObject.GameObjectId
import logic.gameObjects.gameObject.GameObjectModel
import mainModule.scenes.gameScenes.gameScene.MapTilesManager
import kotlin.math.absoluteValue
import kotlin.math.sign

class Sheep(
    override val tilesManager: MapTilesManager,
    bitmap: Bitmap,
    container: Container,
    pos: Point = Point(0, 0)
) : GameObject(tilesManager, bitmap = bitmap, container = container) {
    init {
        this.pos = pos
    }

    override val model = GameObjectModel(1, 1)

    override val tile = GameObjectId.Sheep

    private var destination: Point = pos
        get() {
            return if (field == pos) {
                val newPos = ((Point(-1)..Point(+1)) + pos).firstOrNull {
                    tilesManager[it.xi, it.yi, Layer.GameObjects] == GameObjectId.Empty.id &&
                    tilesManager[it.xi, it.yi, Layer.Walls] == GameObjectId.Empty.id
                }
                field = newPos ?: pos
                return field
            } else
                field
        }

    override fun delete() {
        tilesManager[pos.xi, pos.yi, Layer.GameObjects] = GameObjectId.Empty.id
        isAlive = false
    }

    override suspend fun makeTurn() {
        if (isAlive) {
            val delta = destination - pos
            val dPos = if (delta.x.absoluteValue > delta.y.absoluteValue) {
                Point.Horizontal * delta.x.sign
            } else {
                Point.Vertical * delta.y.sign
            }

            tilesManager.updatePos(pos, pos + dPos, tile)
        }
    }
}
