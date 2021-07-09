package logic.gameObjects.logic

import com.soywiz.korma.geom.Point
import ktres.TILE_ATTACK_CURSOR
import ktres.TILE_EMPTY
import ktres.TILE_MOVE_CURSOR
import lib.extensions.xi
import lib.extensions.yi
import lib.tiledMapView.Layer
import logic.gameObjects.hero.ActionType
import logic.gameObjects.units.enemies.Enemy
import mainModule.scenes.gameScenes.gameScene.managers.MapTilesManager

interface TurnCalculator {
    val actions: MutableList<Pair<ActionType, *>>

    fun calculateTurn()
}

private val involvedPoints = mutableListOf<Point>()

fun TurnCalculator.showPreviewActions(tilesManager: MapTilesManager) {
    actions.forEach { (type, data) ->
        @Suppress("UNCHECKED_CAST")
        when(type) {
            ActionType.Move -> showMoveAction(data as Point, tilesManager) // TODO: unify all data for actions
            ActionType.Attack -> showAttackAction((this as Enemy).target!!.pos, tilesManager) // TODO: refactor target issue
            else -> Unit
        }
    }
}

fun hideAllPreviewActions(tilesManager: MapTilesManager) {
    involvedPoints.forEach { involvedPoint ->
        involvedPoint.run {
            tilesManager[xi, yi, Layer.EnemyStepsPreview] = TILE_EMPTY
        }
    }
}

private fun showMoveAction(pos: Point, tilesManager: MapTilesManager) {
    val (x, y) = pos
    involvedPoints += pos.copy()

    tilesManager[x.toInt(), y.toInt(), Layer.EnemyStepsPreview] = TILE_MOVE_CURSOR
}

private fun showAttackAction(pos: Point, tilesManager: MapTilesManager) {
    val (x, y) = pos
    involvedPoints += pos.copy()

    tilesManager[x.toInt(), y.toInt(), Layer.EnemyStepsPreview] = TILE_ATTACK_CURSOR
}
