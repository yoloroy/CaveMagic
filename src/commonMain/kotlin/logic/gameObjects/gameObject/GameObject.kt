package logic.gameObjects.gameObject

import com.soywiz.korma.geom.Point
import lib.extensions.xi
import lib.extensions.yi
import lib.tiledMapView.Layer
import logic.gameObjects.logic.MessageHandler
import logic.gameObjects.logic.Turnable
import mainModule.scenes.gameScenes.gameScene.MapTilesManager

abstract class GameObject(open val tilesManager: MapTilesManager, val corpseTile: Int? = null) : Turnable, MessageHandler {
    abstract val model: GameObjectModel

    override val messages = mutableListOf<Int>()

    abstract val tile: GameObjectId

    var lastTeleportId: Int? = null

    var isAlive: Boolean = true

    open var pos: Point = Point()

    open fun teleportTo(point: Point, teleportId: Int): Boolean {
        if (lastTeleportId != teleportId) {
            lastTeleportId = teleportId
            tilesManager.updatePos(pos, point, tile)

            return true
        }

        return false
    }

    open fun handleAttack(damage: Int) {
        model.health.value -= damage

        if (model.health.value <= 0) {
            isAlive = false
            tilesManager[pos.xi, pos.yi, Layer.GameObjects] = GameObjectId.Empty.id
            corpseTile?.let {
                tilesManager[pos.xi, pos.yi, Layer.BottomDecorations] = it // TODO: add layer for corpses?
            }
        }
    }

    abstract fun delete()

    override fun toString(): String {
        return "${super.toString()} {tile=${tile.name} pos=$pos ${if (isAlive) "alive" else "dead"}}"
    }
}
