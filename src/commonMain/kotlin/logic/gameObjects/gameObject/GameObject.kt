package logic.gameObjects.gameObject

import com.soywiz.klock.seconds
import com.soywiz.korge.view.*
import com.soywiz.korge.view.tween.moveTo
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korma.geom.Point
import com.soywiz.korma.interpolation.Easing
import lib.extensions.size
import lib.extensions.xi
import lib.extensions.yi
import lib.tiledMapView.Layer
import logic.gameObjects.logic.MessageHandler
import logic.gameObjects.logic.Turnable
import mainModule.scenes.gameScenes.gameScene.MapTilesManager

abstract class GameObject(
    open val tilesManager: MapTilesManager,
    val corpseTile: Int? = null,
    var pos: Point = Point(),
    val bitmap: Bitmap,
    val container: Container
) : Turnable, MessageHandler {
    abstract val model: GameObjectModel

    override val messages = mutableListOf<Int>()

    abstract val tile: GameObjectId

    var lastTeleportId: Int? = null
    var isAlive: Boolean = true
        set(value) {
            field = value
            visible = value
        }

    var visible
        get() = view.visible
        set(value) { view.visible(value) }

    val view = container.image(bitmap) {
        smoothing = false
        size(16, 16)
        anchor(0, 0)
        position(this@GameObject.pos * size)
    }

    open suspend fun teleportTo(point: Point, teleportId: Int): Boolean {
        if (lastTeleportId != teleportId) {
            lastTeleportId = teleportId
            moveTo(point)

            return true
        }

        return false
    }

    open suspend fun handleAttack(damage: Int) {
        model.health.value -= damage

        if (model.health.value <= 0) {
            isAlive = false
            tilesManager[pos.xi, pos.yi, Layer.GameObjects] = GameObjectId.Empty.id
            corpseTile?.let {
                tilesManager[pos.xi, pos.yi, Layer.BottomDecorations] = it // TODO: add layer for corpses?
            }
            visible = false
            view.scale *= 0
        }
    }

    open suspend fun moveTo(newPos: Point) {
        val viewPos = newPos * view.size
        view.moveTo(viewPos.x, viewPos.y, 0.4.seconds, Easing.EASE_OUT)

        tilesManager.updatePos(pos, newPos, tile)
    }

    abstract fun delete()

    override fun toString(): String {
        return "${super.toString()} {tile=${tile.name} pos=$pos ${if (isAlive) "alive" else "dead"}}"
    }
}
