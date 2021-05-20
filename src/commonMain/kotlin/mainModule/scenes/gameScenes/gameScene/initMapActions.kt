package mainModule.scenes.gameScenes.gameScene

import lib.algorythms.pathFinding.getPath
import com.soywiz.korev.MouseButton
import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onMove
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.int
import logic.gameObjects.player.ActionType
import lib.extensions.setTo
import lib.tiledMapView.Layer
import lib.extensions.xi
import lib.extensions.yi
import logic.gameObjects.logic.Phasable
import logic.gameObjects.logic.hideAllPreviewActions
import logic.gameObjects.logic.showPreviewActions

internal fun initMapActions(scene: GameScene) = scene.apply {
    val previewPath = mutableListOf<Pair<Point, Point>>()
    val lastCursorPos = Point(0)

    map.onMove { mouse ->
        val pos = (mouse.currentPosLocal / tilesManager.tileSize).int.p
        tilesManager[lastCursorPos.xi, lastCursorPos.yi, Layer.Cursor] = MapTilesManager.EMPTY

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

        if (player.isAddingMoveEnabled && it.button == MouseButton.RIGHT) {
            player.addMoveTo(pos)
            actionType = ActionType.Nothing
        } else if (actionType == ActionType.Attack) {
            player.addAttackOn(pos)
            actionType = ActionType.Nothing
        } else if (savedMagicSymbol != null) {
            player.addCastMagicOn(pos, savedMagicSymbol!!)
            actionType = ActionType.Nothing
        }
    }
}

private fun GameScene.showMapCursor(pos: Point) {
    tilesManager[pos.xi, pos.yi, Layer.Cursor] =
        if (actionType != ActionType.Attack || (pos - player.lastPreviewPos).length == 1.0)
            cursorTileId
        else
            MapTilesManager.TILE_CURSOR
}

private fun GameScene.showPreviewPathOnMove(
    previewPath: MutableList<Pair<Point, Point>>, pos: Point
) = previewPath.run {
    forEach { (_, pathPoint) ->
        tilesManager[pathPoint.xi, pathPoint.yi, Layer.Cursor] = MapTilesManager.EMPTY
    }

    clear()
    addAll(
        getPath(player.lastPreviewPos, pos, tilesManager[Layer.Walls])
            .take(player.remainingActionPoints)
    )

    forEach { (_, pathPoint) ->
        tilesManager[pathPoint.xi, pathPoint.yi, Layer.Cursor] = MapTilesManager.TILE_MOVE_CURSOR
    }
}
