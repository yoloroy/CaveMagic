package mainModule.scenes.gameScenes.gameScene

import com.soywiz.korev.MouseButton
import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onMove
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.int
import ktres.TILE_CURSOR
import ktres.TILE_EMPTY
import ktres.TILE_MOVE_CURSOR
import lib.algorythms.pathFinding.getPath
import lib.extensions.setTo
import lib.extensions.xi
import lib.extensions.yi
import lib.tiledMapView.Layer
import logic.gameObjects.logic.Phasable
import logic.gameObjects.logic.hideAllPreviewActions
import logic.gameObjects.logic.showPreviewActions
import logic.gameObjects.player.ActionType

internal fun initMapActions(scene: GameScene) = scene.apply {
    val previewPath = mutableListOf<Pair<Point, Point>>()
    val lastCursorPos = Point(0)

    map.onMove { mouse ->
        val pos = (mouse.currentPosLocal / tilesManager.tileSize).int.p
        tilesManager[lastCursorPos.xi, lastCursorPos.yi, Layer.Cursor] = TILE_EMPTY

        if (actionType == ActionType.Move) {
            showPreviewPathOnMove(previewPath, pos)
        }

        gameObjects // TODO: refactor
            .firstOrNull { it.pos == pos }
            .takeIf { it is Phasable }
            .also {
                hideAllPreviewActions(tilesManager)
            }
            ?.let { it as Phasable
                it.showPreviewActions(tilesManager)
            }
        showMapCursor(pos)
        lastCursorPos.setTo(pos)
    }

    map.onClick {
        val pos = (it.currentPosLocal / tilesManager.tileSize).int.p

        if (it.button == MouseButton.RIGHT) {
            when {
                player.isAddingMoveEnabled -> {
                    previewPath.run { // TODO: refactor
                        forEach { (_, pathPoint) ->
                            tilesManager[pathPoint.xi, pathPoint.yi, Layer.Cursor] = TILE_EMPTY
                        }

                        clear()
                    }

                    player.addMoveTo(pos)
                    actionType = ActionType.Nothing
                }
                actionType == ActionType.Attack -> {
                    player.addAttackOn(pos)
                    actionType = ActionType.Nothing
                }
                savedMagicSymbol != null -> {
                    player.addCastMagicOn(pos, savedMagicSymbol!!)
                    savedMagicSymbol = null
                    actionType = ActionType.Nothing
                }
            }
        }
    }
}

private fun GameScene.showMapCursor(pos: Point) {
    tilesManager[pos.xi, pos.yi, Layer.Cursor] =
        if (actionType != ActionType.Attack || (pos - player.lastPreviewPos).length == 1.0)
            cursorTileId
        else
            TILE_CURSOR
}

private fun GameScene.showPreviewPathOnMove(
    previewPath: MutableList<Pair<Point, Point>>, pos: Point
) = previewPath.run {
    forEach { (_, pathPoint) ->
        tilesManager[pathPoint.xi, pathPoint.yi, Layer.Cursor] = TILE_EMPTY
    }

    clear()
    addAll(
        getPath(player.lastPreviewPos, pos, tilesManager[Layer.Walls])
            .take(player.remainingActionPoints)
    )

    forEach { (_, pathPoint) ->
        tilesManager[pathPoint.xi, pathPoint.yi, Layer.Cursor] = TILE_MOVE_CURSOR
    }
}
