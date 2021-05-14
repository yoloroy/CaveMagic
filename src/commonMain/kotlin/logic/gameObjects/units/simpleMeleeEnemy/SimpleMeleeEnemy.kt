package logic.gameObjects.units.simpleMeleeEnemy

import lib.algorythms.pathFinding.getPath
import com.soywiz.korio.async.ObservableProperty
import com.soywiz.korma.geom.Point
import logic.gameObjects.gameObject.GameObject
import logic.gameObjects.gameObject.GameObjectId
import logic.gameObjects.gameObject.GameObjectModel
import logic.gameObjects.units.Enemy
import mainModule.scenes.gameScenes.gameScene.MapTilesManager
import lib.tiledMapView.Layer

open class SimpleMeleeEnemy(
    override var pos: Point,
    healthLimit: Int,
    actionPointsLimit: Int,
    damage: Int,
    tilesManager: MapTilesManager,
    override val tile: GameObjectId,
    health: Int = healthLimit
) : GameObject(tilesManager), Enemy {
    override val model: SimpleMeleeEnemyModel = SimpleMeleeEnemyModel(healthLimit, actionPointsLimit, damage, health)

    private var state = States.Idle

    override var target: GameObject? = null
        set(value) {
            field = value
            state = States.Battle
        }

    override fun makeTurn() {
        when (state) {
            States.Battle -> makeBattleTurn()
            States.Idle -> makeIdleTurn()
        }
    }

    private fun makeBattleTurn() {
        target?.let { target ->
            if (target.pos.distanceTo(pos).toInt() == 1) {
                target.handleAttack(model.damage.value)
            } else {
                println("finding path")
                val path = getPath(pos, target.pos, tilesManager[Layer.Walls])
                println(path)

                tilesManager.updatePos(pos, path.first().second, tile)
            }
        }
    }

    private fun makeIdleTurn() {

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
