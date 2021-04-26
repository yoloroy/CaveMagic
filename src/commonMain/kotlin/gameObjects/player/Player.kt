package gameObjects.player

import com.soywiz.korge.tiled.TiledMapView
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korma.geom.Point
import mainModule.MainModule
import mainModule.scenes.tutorial.MapTilesManager
import gameObjects.gameObject.GameObject
import utils.tiledMapView.Layer
import utils.*

class Player(
    stage: Stage,
    private val map: TiledMapView,
    bitmap: Bitmap,
    private val camera: Camera,
    private val tilesManager: MapTilesManager,
    val pos: Point = tilesManager.playerPos
) : GameObject() {
    private var view: Image = stage.image(bitmap) {
        smoothing = false
        size(16, 16) // TODO
        setPositionRelativeTo(map, Point(-0.5) * scaledSize + MainModule.size.size.p / 2)
    }

    private val controllerComponent = PlayerControllerComponent(view)

    init {
        camera.setPositionRelativeTo(view, -pos * view.size)
        view.addComponent(controllerComponent)
    }

    override fun delete() {
    }

    override fun makeTurn() {
        if ((controllerComponent.direction != Direction.Nowhere) && (pos + controllerComponent.direction.vector).run { tilesManager[xi, yi, Layer.Walls] == 0 }) {
            pos += controllerComponent.direction.vector
            camera.setPositionRelativeTo(view, -pos * view.size)
        }
    }
}