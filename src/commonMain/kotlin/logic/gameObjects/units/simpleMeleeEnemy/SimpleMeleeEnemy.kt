package logic.gameObjects.units.simpleMeleeEnemy

import com.soywiz.kmem.toIntCeil
import com.soywiz.korio.async.ObservableProperty
import com.soywiz.korma.geom.Point
import lib.algorythms.pathFinding.getPath
import lib.extensions.setTo
import lib.extensions.sum
import lib.tiledMapView.Layer
import logic.gameObjects.gameObject.GameObject
import logic.gameObjects.gameObject.GameObjectId
import logic.gameObjects.gameObject.GameObjectModel
import logic.gameObjects.player.ActionType
import logic.gameObjects.units.Enemy
import mainModule.scenes.gameScenes.gameScene.MapTilesManager

open class SimpleMeleeEnemy(
    pos: Point,
    healthLimit: Int,
    actionPointsLimit: Int,
    damage: Int,
    tilesManager: MapTilesManager,
    override val tile: GameObjectId,
    health: Int = healthLimit,
    corpseTile: Int? = null
) :
    GameObject(tilesManager, corpseTile = corpseTile, pos),
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

    private val lastPreviewPos = pos.copy()

    override fun calculateTurn() {
        actions.clear()
        isTurnCalculated = true
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

    private fun calculateBattleTurn() = target?.let { target ->
        if (target.pos.distanceTo(lastPreviewPos).toIntCeil() == 1) {
            ActionType.Attack to null
        } else {
            val path = getPath(lastPreviewPos, target.pos, sum(tilesManager[Layer.Walls], tilesManager[Layer.GameObjects])) // TODO: add caching

            ActionType.Move to path.first().second.also { lastPreviewPos.setTo(it) }
        }
    } ?: ActionType.Nothing to null

    override fun makeTurn() {
        if (!isTurnCalculated) {
            calculateTurn()
        }

        actions.forEach { (type, data) ->
            when(type) {
                ActionType.Attack -> doAttack()
                ActionType.Move -> doMove(data as Point)
                else -> Unit
            }
        }

        isTurnCalculated = false
    }

    private fun doAttack() = target?.let { target ->
        if (target.pos.distanceTo(pos).toIntCeil() == 1) {
            target.handleAttack(model.damage.value)
        }
    } ?: Unit

    private fun doMove(newPos: Point) = tilesManager.updatePos(pos, newPos, tile)

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
