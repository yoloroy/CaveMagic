package mainModule.scenes.gameScenes.gameScene

import com.soywiz.korev.Key
import com.soywiz.korge.input.keys
import com.soywiz.korge.view.Container
import lib.extensions.plus

val collectedTurnActions = mutableMapOf<Key, suspend () -> Unit>()
lateinit var turnActions: MutableMap<Key, Boolean>

fun Container.initKeyActions() {
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

fun addKeyTurnAction(key: Key, onClick: suspend () -> Unit) {
    collectedTurnActions[key] = collectedTurnActions[key]?.plus(onClick) ?: onClick
}

fun addKeyTurnAction(key: Key, onClick: () -> Unit) = addKeyTurnAction(key, suspend { onClick() })
