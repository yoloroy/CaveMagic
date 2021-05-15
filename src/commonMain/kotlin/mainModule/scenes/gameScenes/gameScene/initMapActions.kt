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

internal fun initMapActions(scene: GameScene) = scene.apply {
    val previewPath = mutableListOf<Pair<Point, Point>>()
    val lastCursorPos = Point(0)
    map.onMove {
        val pos = (it.currentPosLocal / tilesManager.tileSize).int.p
        tilesManager[lastCursorPos.xi, lastCursorPos.yi, Layer.Cursor] = MapTilesManager.EMPTY

        if (actionType == ActionType.Move) {
            previewPath
                .forEach { (_, pathPoint) ->
                    tilesManager[pathPoint.xi, pathPoint.yi, Layer.Cursor] = MapTilesManager.EMPTY
                }

            previewPath.clear()
            previewPath.addAll(
                getPath(
                    player.lastPreviewPos,
                    pos,
                    tilesManager[Layer.Walls]
                ).take(player.remainingActionPoints)
            )

            previewPath
                .forEach { (_, pathPoint) ->
                    tilesManager[pathPoint.xi, pathPoint.yi, Layer.Cursor] = MapTilesManager.TILE_MOVE_CURSOR
                }
        }

        tilesManager[pos.xi, pos.yi, Layer.Cursor] =
            if (actionType != ActionType.Attack || (pos - player.lastPreviewPos).length == 1.0)
                cursorTileId
            else
                MapTilesManager.TILE_CURSOR
        lastCursorPos.setTo(pos)
    }

    map.onClick {
        val pos = (it.currentPosLocal / tilesManager.tileSize).int.p

        if (player.isAddingMoveEnabled && it.button == MouseButton.RIGHT) {// TODO: refactor
            player.actions += getPath(
                player.lastPreviewPos,
                pos,
                tilesManager[Layer.Walls]
            )
                .take(player.remainingActionPoints)
                .also { path ->
                    player.lastPreviewPos.setTo(path.last().second)
                    player.showPath(path)
                }
                .map { pathPart -> ActionType.Move to pathPart }
            actionType = ActionType.Nothing
        } else if (actionType == ActionType.Attack) {
            player.actions += ActionType.Attack to pos
            actionType = ActionType.Nothing
        } else if (savedMagicSymbol != null) {
            player.addCastMagicOn(pos, savedMagicSymbol!!)
            actionType = ActionType.Nothing
        }
    }
}
