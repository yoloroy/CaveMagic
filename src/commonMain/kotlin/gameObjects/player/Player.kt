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
    map: TiledMapView,
    private val camera: Camera,
    private val tilesManager: MapTilesManager,
    override val pos: Point = tilesManager.playerPos
) : GameObject() {
    override val tile = GameObjectId.Player

    private val controllerComponent = PlayerControllerComponent(map)

    init {
        camera.setPositionRelativeTo(map, (-pos + Point(-0.5)) * tilesManager.tileSize + camera.sizePoint * Point(1.0, 0.5) / 2)
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
            tilesManager.updatePos(pos + controllerComponent.direction.vector)
        }
    }

    private fun MapTilesManager.updatePos(newPos: Point) {
        updatePos(pos, newPos, tile)

        camera.pos = camera.pos - tilesManager.tileSize * controllerComponent.direction.vector
    }
}