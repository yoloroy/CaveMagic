package logic.gameObjects.sheep

import com.soywiz.korma.geom.Point
import logic.gameObjects.gameObject.GameObjectId
import logic.gameObjects.gameObject.GameObjectModel
import logic.gameObjects.gameObject.GameObject
import mainModule.scenes.gameScenes.gameScene.MapTilesManager
import utils.*
import utils.tiledMapView.Layer
import kotlin.math.absoluteValue
import kotlin.math.sign

class Sheep(
    override val tilesManager: MapTilesManager,
    override var pos: Point = Point(0, 0)
) : GameObject(tilesManager) {
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

    override fun makeTurn() {
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
