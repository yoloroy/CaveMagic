package logic.gameObjects.player

import com.soywiz.korge.tiled.TiledMapView
import com.soywiz.korge.view.Camera
import com.soywiz.korge.view.setPositionRelativeTo
import com.soywiz.korio.async.ObservableProperty
import com.soywiz.korma.geom.Point
import ktres.TILE_ATTACK_CURSOR
import ktres.TILE_EMPTY
import ktres.TILE_LIGHTNING_CAST_CURSOR
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

    override val tile = GameObjectId.Player

    override val model = PlayerModel(10, 3, 2)

    private val fogOfWarComponent = FogOfWarComponent(tilesManager, pos)

    val remainingActionPoints get() = maxOf(model.actionPointsLimit.value - actions.size, 0)

    private val actions = mutableListOf<Action>()

    val lastPreviewPos = pos.copy()

    init {
        updateCamera()

        notifyNearbyGameObjects()
    }

    private fun showPath(path: Collection<Pair<Point, Point>>) {
        path.forEach {
            val (x, y) = it.second
            tilesManager[x.toInt(), y.toInt(), Layer.StepsPreview] = TILE_MOVE_CURSOR
        }
    }

    override fun makeTurn() {
        clearPreview()

        actions.forEach { it() }
        actions.clear()

        fogOfWarComponent.updateViewArea()
    }

    private fun doAttack(point: Point, damage: Int = model.damage.value) {
        val target = gameObjects.firstOrNull { it.pos == point }
        target?.handleAttack(damage)
    }

    private fun doMove(pathPart: Pair<Point, Point>) {
        lastTeleportId = null
        tilesManager.updatePos(pathPart.second)

        notifyNearbyGameObjects() // TODO
    }

    private fun notifyNearbyGameObjects() = gameObjects // TODO
        .filter { it.pos.distanceTo(pos) < 8 }
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
            fogOfWarComponent.updateViewArea()
        }
    }

    private fun updateCamera() {
        camera.setPositionRelativeTo(map, (-pos + Point(-0.5)) * tilesManager.tileSize + camera.sizePoint * Point(1.0, 0.5) / 2)
    }

    override fun delete() {
    }

    fun addCastMagicOn(pos: Point, magicSymbol: Magic) {
        updateActionsPreview {
            when (magicSymbol) {
                DamageMagic.Lightning -> {
                    add(AttackAction(pos, 2, TILE_LIGHTNING_CAST_CURSOR))
                }
            }
        }
    }

    fun addMoveTo(pos: Point) {
        isAddingMoveEnabled = false
        updateActionsPreview {
            addAll(getPath(lastPreviewPos, pos, tilesManager[Layer.Walls])
                .take(remainingActionPoints)
                .also { path ->
                    lastPreviewPos.setTo(path.last().second)
                    showPath(path)
                }
                .map { MoveAction(it.first, it.second) })
        }
    }

    fun addAttackOn(pos: Point) {
        updateActionsPreview {
            add(AttackAction(pos))
        }
    }

    fun removeLastAction() {
        updateActionsPreview {
            removeLastOrNull()?.let {
                if (it is MoveAction) {
                    lastPreviewPos.setTo(it.start)
                }
            }
        }
    }

    private inline fun updateActionsPreview(block: MutableList<Action>.() -> Unit = {}) {
        clearPreview(actions)
        actions.block()
        showPreview(actions)
    }

    private fun showPreview(actions: List<Action> = this.actions) = actions.forEach { it.show() }

    private fun clearPreview(actions: List<Action> = this.actions) = actions.forEach { it.hide() }

    abstract inner class Action(private val position: Point, private val tile: Int) {
        abstract operator fun invoke()

        fun show() {
            tilesManager[position.xi, position.yi, Layer.StepsPreview] = tile
        }

        fun hide() {
            tilesManager[position.xi, position.yi, Layer.StepsPreview] = TILE_EMPTY
        }
    }

    inner class MoveAction(val start: Point, val end: Point) : Action(end, TILE_MOVE_CURSOR) {
        override fun invoke() = doMove(start to end)
    }

    inner class AttackAction(
        private val destination: Point,
        private val damage: Int = model.damage.value,
        tile: Int = TILE_ATTACK_CURSOR
    ) : Action(destination, tile) {
        override fun invoke() = doAttack(destination, damage)
    }
}

class PlayerModel(
    healthLimit: Int,
    actionPointsLimit: Int,
    damage: Int,
    health: Int = healthLimit
) : GameObjectModel(healthLimit, actionPointsLimit, health) {
    val damage = ObservableProperty(damage)
}
