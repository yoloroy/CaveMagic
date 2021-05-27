package logic.gameObjects.player

import com.soywiz.klock.seconds
import com.soywiz.korge.tiled.TiledMapView
import com.soywiz.korge.tween.get
import com.soywiz.korge.tween.tween
import com.soywiz.korge.tween.tweenAsync
import com.soywiz.korge.view.Camera
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.setPositionRelativeTo
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korio.async.ObservableProperty
import com.soywiz.korma.geom.Point
import com.soywiz.korma.interpolation.Easing
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
    tilesManager: MapTilesManager,
    bitmap: Bitmap,
    container: Container,
    pos: Point = tilesManager.playerPos,
    var isAddingMoveEnabled: Boolean = false
) : GameObject(tilesManager, bitmap = bitmap, container = container, pos = pos) {
    override val tile = GameObjectId.Player

    override val model = PlayerModel(10, 3, 2)

    private val fogOfWarComponent = FogOfWarComponent(tilesManager, pos, gameObjects)

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

    override suspend fun makeTurn() {
        clearPreview()

        actions.forEach {
            it()
            fogOfWarComponent.updateViewArea()
        }
        actions.clear()
    }

    private suspend fun doAttack(point: Point, damage: Int = model.damage.value) {
        val target = gameObjects.firstOrNull { it.isAlive && it.pos == point }
        target?.handleAttack(damage)
    }

    private suspend fun doMove(pathPart: Pair<Point, Point>) {
        lastTeleportId = null
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

    private suspend fun MapTilesManager.updatePos(newPos: Point) {
        moveTo(newPos)

        lastPreviewPos.setTo(newPos)
    }

    @Suppress("DeferredResultUnused")
    override suspend fun moveTo(newPos: Point) {
        val viewPos = newPos * view.size
        view.tweenAsync(view::pos[view.pos, viewPos], time = 0.4.seconds, easing = Easing.LINEAR)

        val deltaPos = newPos - pos
        camera.tween(camera::pos[camera.pos, camera.pos - tilesManager.tileSize * deltaPos], time = 0.4.seconds, easing = Easing.EASE_OUT_QUAD)

        tilesManager.updatePos(pos, newPos, tile)
    }

    override suspend fun teleportTo(point: Point, teleportId: Int) = (lastTeleportId != teleportId).also {
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
            addAll(getPath(lastPreviewPos, pos, sum(tilesManager[Layer.Walls], tilesManager[Layer.GameObjects]))
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
        abstract suspend operator fun invoke()

        fun show() {
            tilesManager[position.xi, position.yi, Layer.StepsPreview] = tile
        }

        fun hide() {
            tilesManager[position.xi, position.yi, Layer.StepsPreview] = TILE_EMPTY
        }
    }

    inner class MoveAction(val start: Point, val end: Point) : Action(end, TILE_MOVE_CURSOR) {
        override suspend fun invoke() = doMove(start to end)
    }

    inner class AttackAction(
        private val destination: Point,
        private val damage: Int = model.damage.value,
        tile: Int = TILE_ATTACK_CURSOR
    ) : Action(destination, tile) {
        override suspend fun invoke() = doAttack(destination, damage)
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
