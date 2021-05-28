package logic.gameObjects.units.simpleMeleeEnemy

import com.soywiz.kmem.toIntCeil
import com.soywiz.korge.view.Container
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korio.async.ObservableProperty
import com.soywiz.korma.geom.Point
import lib.algorythms.pathFinding.getPath
import lib.animations.animateMeleeAttackOn
import lib.extensions.setTo
import lib.extensions.sum
import lib.extensions.xi
import lib.extensions.yi
import lib.tiledMapView.Layer
import logic.gameObjects.gameObject.GameObject
import logic.gameObjects.gameObject.GameObjectId
import logic.gameObjects.gameObject.GameObjectModel
import logic.gameObjects.hero.ActionType
import logic.gameObjects.units.Enemy
import mainModule.scenes.gameScenes.gameScene.MapTilesManager

open class SimpleMeleeEnemy(
    pos: Point,
    healthLimit: Int,
    actionPointsLimit: Int,
    damage: Int,
    tilesManager: MapTilesManager,
    override val tile: GameObjectId,
    bitmap: Bitmap,
    container: Container,
    health: Int = healthLimit,
    corpseTile: Int? = null
) :
    GameObject(tilesManager, corpseTile = corpseTile, pos, bitmap = bitmap, container = container),
    Enemy
{
    override val model: SimpleMeleeEnemyModel = SimpleMeleeEnemyModel(healthLimit, actionPointsLimit, damage, health)

    private var state = States.Idle

    override var target: GameObject? = null
        set(value) {
            field = value
            state = States.Battle
        }

    private var isTurnCalculated: Boolean = false

    override val actions = mutableListOf<Pair<ActionType, *>>()
        get() = field.takeIf { isAlive } ?: mutableListOf()

    private val lastPreviewPos = pos.copy()

    override fun calculateTurn() {
        actions.clear()
        isTurnCalculated = true
        if (isAlive) {
            when (state) {
                States.Battle -> run calculating@{
                    repeat(model.actionPointsLimit.value) {
                        calculateBattleTurn().let {
                            actions += it

                            if (it.first == ActionType.Attack) {
                                return@calculating
                            }
                        }
                    }
                }
                States.Idle -> Unit
            }
        }
    }

    private fun calculateBattleTurn() = target?.let { target ->
        if (target.pos.distanceTo(lastPreviewPos).toIntCeil() == 1) {
            ActionType.Attack to null
        } else {
            val path = getPath(lastPreviewPos, target.pos, sum(tilesManager[Layer.Walls], tilesManager[Layer.GameObjects])) // TODO: add caching

            if (path.isNotEmpty()) {
                ActionType.Move to path.first().second.also { lastPreviewPos.setTo(it) }
            } else {
                ActionType.Nothing to null
            }
        }
    } ?: ActionType.Nothing to null

    override suspend fun makeTurn() {
        if (!isTurnCalculated) {
            calculateTurn()
        }
        isTurnCalculated = false

        actions.forEach { (type, data) ->
            if (!when(type) {
                    ActionType.Attack -> doAttack()
                    ActionType.Move -> doMove(data as Point)
                    else -> false
                }) {
                return@makeTurn
            }
        }
    }

    private suspend fun doAttack() = true.also {
        target?.let { target ->
            if (target.pos.distanceTo(pos).toIntCeil() == 1) {
                animateMeleeAttackOn(target)
                target.handleAttack(model.damage.value)
            }
        }
    }

    private suspend fun doMove(newPos: Point) =
            (tilesManager[Layer.GameObjects][newPos.xi, newPos.yi] == 0
            && tilesManager[Layer.Walls][newPos.xi, newPos.yi] == 0
            && newPos.distanceTo(pos).toIntCeil() == 1).also {
        if (it) moveTo(newPos)
    }

    override fun delete() {
        TODO("Not yet implemented")
    }

    enum class States {
        Idle, Battle
    }
}

open class SimpleMeleeEnemyModel(
    healthLimit: Int,
    actionPointsLimit: Int,
    damage: Int,
    health: Int = healthLimit
) : GameObjectModel(healthLimit, actionPointsLimit, health) {
    val damage = ObservableProperty(damage)
}
