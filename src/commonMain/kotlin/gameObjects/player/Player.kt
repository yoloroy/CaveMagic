package gameObjects.player

import com.soywiz.korev.MouseButton
import com.soywiz.korge.input.onClick
import com.soywiz.korge.tiled.TiledMapView
import com.soywiz.korge.view.*
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.int
import gameObjects.GameObjectId
import mainModule.scenes.tutorial.MapTilesManager
import gameObjects.gameObject.GameObject
import utils.*
import utils.tiledMapView.Layer

class Player(
    private val map: TiledMapView,
    private val camera: Camera,
    override val tilesManager: MapTilesManager,
    override var pos: Point = tilesManager.playerPos
) : GameObject(tilesManager) {
    override val tile = GameObjectId.Player

    private var path = mutableListOf<Pair<Point, Point>>()

    init {
        updateCamera()

        map.onClick {
            if (it.button == MouseButton.RIGHT) // TODO: refactor
                path = getPath(pos, (it.currentPosLocal / tilesManager.tileSize).int.p, tilesManager[Layer.Walls]).toMutableList()
        }
    }

    override fun delete() {
    }

    override fun makeTurn() {
        if (path.isNotEmpty() && path.first().first == pos) {
            val nextPos = path.removeFirst().second

            lastTeleportId = null
            tilesManager.updatePos(nextPos)
        }
    }

    private fun MapTilesManager.updatePos(newPos: Point) {
        val deltaPos = newPos - pos

        updatePos(pos, newPos, tile)

        camera.pos = camera.pos - tilesManager.tileSize * deltaPos
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
}