package logic.gameObjects.gameObject

import com.soywiz.korma.geom.Point
import logic.gameObjects.logic.MessageHandler
import logic.gameObjects.logic.Turnable
import mainModule.scenes.tutorial.MapTilesManager

abstract class GameObject(open val tilesManager: MapTilesManager) : Turnable, MessageHandler {
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

    abstract fun delete()

    override fun toString(): String {
        return "${super.toString()} {tile=${tile.name} pos=$pos ${if (isAlive) "alive" else "dead"}}"
    }
}
