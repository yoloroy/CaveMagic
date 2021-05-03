package gameObjects.player

import com.soywiz.korge.tiled.TiledMapView
import com.soywiz.korge.view.*
import com.soywiz.korma.geom.Point
import gameObjects.GameObjectId
import mainModule.scenes.tutorial.MapTilesManager
import gameObjects.gameObject.GameObject
import utils.tiledMapView.Layer
import utils.*

class Player(
    stage: Stage,
    private val map: TiledMapView,
    private val camera: Camera,
    override val tilesManager: MapTilesManager,
    override var pos: Point = tilesManager.playerPos
) : GameObject(tilesManager) {
    override val tile = GameObjectId.Player

    private val controllerComponent = PlayerControllerComponent(map)

    init {
        updateCamera()
        stage.addComponent(controllerComponent)
    }

    override fun delete() {
    }

    override fun makeTurn() {
        val isMoving = controllerComponent.direction != Direction.Nowhere
        val isMovePossible = (pos + controllerComponent.direction.vector)
            .run {
                tilesManager[xi, yi, Layer.Walls] == GameObjectId.Empty.id &&
                tilesManager[xi, yi, Layer.GameObjects] == GameObjectId.Empty.id
            }

        if (isMoving && isMovePossible) {
            lastTeleportId = null
            tilesManager.updatePos(pos + controllerComponent.direction.vector)
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