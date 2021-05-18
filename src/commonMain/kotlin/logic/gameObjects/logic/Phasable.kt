package logic.gameObjects.logic

import logic.gameObjects.player.ActionType

interface Phasable {
    val actions: MutableList<Pair<ActionType, *>>

    fun calculateTurn()
}
