package mainModule.scenes.gameScenes.gameScene.init

import com.soywiz.korev.Key
import com.soywiz.korge.input.keys
import com.soywiz.korge.view.Container
import com.soywiz.korma.geom.Point
import ktres.TILE_EMPTY
import lib.extensions.plus
import lib.extensions.xi
import lib.extensions.yi
import lib.tiledMapView.Layer
import logic.gameObjects.hero.ActionType
import mainModule.scenes.gameScenes.gameScene.GameScene

internal val collectedTurnActions = mutableMapOf<Key, suspend () -> Unit>()
internal lateinit var turnActions: MutableMap<Key, Boolean>

internal fun Container.initKeyActions(scene: GameScene) = scene.apply { // TODO: refactor
    mapOf(
        Key.LEFT to Point(-1, 0),
        Key.RIGHT to Point(1, 0),
        Key.UP to Point(0, -1),
        Key.DOWN to Point(0, 1)
    ).forEach { (key, shift) ->
        addKeyTurnAction(key, suspend { // todo: refactor
            val newPos = hero.lastPreviewPos + shift
            if (tilesManager[newPos.xi, newPos.yi, Layer.Walls] == TILE_EMPTY) { // todo: move to ext
                when {
                    hero.isAddingMoveEnabled -> { // && tilesManager[newPos.xi, newPos.yi, Layer.GameObjects] == TILE_EMPTY // if will be dead - we can go
                        hero.addMoveTo(newPos)
                        hero.isAddingMoveEnabled = true
                    }
                    actionType == ActionType.Attack -> {
                        hero.addAttackOn(newPos)
                        actionType = ActionType.Nothing
                    }
                }
            }
        })
    }

    turnActions = collectedTurnActions.mapValues { false }.toMutableMap()

    keys {
        collectedTurnActions.forEach { (key, onClick) ->
            down(key) {
                turnActions[key] = true
            }
            up(key) {
                if (turnActions[key] == true) {
                    try {
                        onClick()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    turnActions[key] = false
                }
            }
        }
    }
}

internal fun addKeyTurnAction(key: Key, onClick: suspend () -> Unit) {
    collectedTurnActions[key] = collectedTurnActions[key]?.plus(onClick) ?: onClick
}

internal fun addKeyTurnAction(key: Key, onClick: () -> Unit) = addKeyTurnAction(key, suspend { onClick() })
