package logic.gameObjects.player

import com.soywiz.korge.tiled.TiledMapView
import com.soywiz.korge.view.*
import com.soywiz.korma.geom.Point
import logic.gameObjects.gameObject.GameObjectId
import logic.gameObjects.gameObject.GameObjectModel
import mainModule.scenes.tutorial.MapTilesManager
import logic.gameObjects.gameObject.GameObject
import utils.*
import utils.tiledMapView.Layer

class Player(
    private val map: TiledMapView,
    private val camera: Camera,
    override val tilesManager: MapTilesManager,
    override var pos: Point = tilesManager.playerPos,
    var isAddingMoveEnabled: Boolean = false
) : GameObject(tilesManager) {
    override val tile = GameObjectId.Player

    override val model = GameObjectModel(10, 3)

    val remainingActionPoints get() = maxOf(model.actionPointsLimit.value - actions.size, 0)
    val actions = mutableListOf<Pair<ActionType, *>>()
    val lastPreviewPos = pos.copy()

    init {
        updateCamera()
    }

    fun addPath(path: Collection<Pair<Point, Point>>) {

    }

    fun showPath(path: Collection<Pair<Point, Point>>) {
        path.forEach {
            val (x, y) = it.second
            tilesManager[x.toInt(), y.toInt(), Layer.StepsPreview] = MapTilesManager.TILE_MOVE_CURSOR
        }
    }

    override fun makeTurn() {
        if (actions.isNotEmpty()) {
            repeat(actions.size) {
                doAction()
            }
        }
    }

    private fun doAction() {
        val (type, value) = actions.removeFirst()

        @Suppress("UNCHECKED_CAST")
        when (type) {
            ActionType.Move -> doMove(value as Pair<Point, Point>)
            else -> Unit
        }
    }

    private fun doMove(pathPart: Pair<Point, Point>) {
        lastTeleportId = null
        tilesManager[pathPart.second.xi, pathPart.second.yi, Layer.StepsPreview] = MapTilesManager.EMPTY
        tilesManager.updatePos(pathPart.second)
    }

    private fun MapTilesManager.updatePos(newPos: Point) {
        val deltaPos = newPos - pos

        updatePos(pos, newPos, tile)

        camera.pos = camera.pos - tilesManager.tileSize * deltaPos
        lastPreviewPos.setTo(newPos)
    }

    override fun teleportTo(point: Point, teleportId: Int) = (lastTeleportId != teleportId).also {
        if (it) {
            lastTeleportId = teleportId
            tilesManager.updatePos(point)
        }
    }

    private fun updateCamera() {
        camera.setPositionRelativeTo(map, (-pos + Point(-0.5)) * tilesManager.tileSize + camera.sizePoint * Point(1.0, 0.5) / 2)
    }

    override fun delete() {
    }
}