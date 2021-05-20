package logic.gameObjects.player

import com.soywiz.korge.tiled.TiledMapView
import com.soywiz.korge.view.Camera
import com.soywiz.korge.view.setPositionRelativeTo
import com.soywiz.korio.async.ObservableProperty
import com.soywiz.korma.geom.Point
import ktres.TILE_EMPTY
import ktres.TILE_MOVE_CURSOR
import lib.algorythms.pathFinding.getPath
import lib.extensions.*
import lib.tiledMapView.Layer
import logic.gameObjects.gameObject.GameObject
import logic.gameObjects.gameObject.GameObjectId
import logic.gameObjects.gameObject.GameObjectModel
import logic.gameObjects.units.Enemy
import logic.magic.DamageMagic
import logic.magic.Magic
import mainModule.scenes.gameScenes.gameScene.MapTilesManager

class Player(
    private val map: TiledMapView,
    private val camera: Camera,
    private val gameObjects: List<GameObject>,
    override val tilesManager: MapTilesManager,
    pos: Point = tilesManager.playerPos,
    var isAddingMoveEnabled: Boolean = false
) : GameObject(tilesManager) {
    init {
        this.pos = pos
    }

    private val positionObservers = mutableListOf<(old: Point, new: Point) -> Unit>()

    override val tile = GameObjectId.Player

    override val model = PlayerModel(10, 3, 2)

    val remainingActionPoints get() = maxOf(model.actionPointsLimit.value - actions.size, 0)
    private val actions = mutableListOf<Pair<ActionType, *>>()
    val lastPreviewPos = pos.copy()

    init {
        updateCamera()
    }

    private fun showPath(path: Collection<Pair<Point, Point>>) {
        path.forEach {
            val (x, y) = it.second
            tilesManager[x.toInt(), y.toInt(), Layer.StepsPreview] = TILE_MOVE_CURSOR
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
            ActionType.Attack -> {
                if (value is Point)
                    doAttack(value)
                else
                    (value as Pair<Point, Int>).let { doAttack(it.first, it.second) }
            }
            else -> Unit
        }
    }

    private fun doAttack(point: Point, damage: Int = model.damage.value) {
        val target = gameObjects.firstOrNull { it.pos == point }
        target?.handleAttack(damage)
        println(target?.model?.health?.value)
    }

    private fun doMove(pathPart: Pair<Point, Point>) {
        lastTeleportId = null
        tilesManager[pathPart.second.xi, pathPart.second.yi, Layer.StepsPreview] = TILE_EMPTY
        tilesManager.updatePos(pathPart.second)

        notifyNearbyGameObjects() // TODO
    }

    private fun notifyNearbyGameObjects() = gameObjects // TODO
        .filter { it.pos.distanceTo(pos) < 5 }
        .forEach {
            if (it is Enemy) {
                it.target = this
            }
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

    fun addCastMagicOn(pos: Point, magicSymbol: Magic) {
        when (magicSymbol) {
            DamageMagic.Lightning -> {
                actions += ActionType.Attack to (pos to 2)
            }
        }
    }

    fun addMoveTo(pos: Point) {
        actions += getPath(lastPreviewPos, pos, tilesManager[Layer.Walls])
            .take(remainingActionPoints)
            .also { path ->
                lastPreviewPos.setTo(path.last().second)
                showPath(path)
            }
            .map { pathPart -> ActionType.Move to pathPart }
    }

    fun addAttackOn(pos: Point) {
        actions += ActionType.Attack to pos
    }

    fun observePos(callback: (old: Point, new: Point) -> Unit) {
        positionObservers += callback
    }

    fun callbackPosObservers(old: Point, new: Point) = positionObservers.callAll(old, new)
}

class PlayerModel(
    healthLimit: Int,
    actionPointsLimit: Int,
    damage: Int,
    health: Int = healthLimit
) : GameObjectModel(healthLimit, actionPointsLimit, health) {
    val damage = ObservableProperty(damage)
}
