package mainModule.scenes.gameScenes.gameScene

import algorythms.pathFinding.getPath
import com.soywiz.korev.MouseButton
import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onMove
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.int
import logic.gameObjects.player.ActionType
import utils.setTo
import utils.tiledMapView.Layer
import utils.xi
import utils.yi

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
            if (actionType != ActionType.Attack || (pos - player.pos).length == 1.0)
                cursorTileId
            else
                MapTilesManager.TILE_CURSOR
        lastCursorPos.setTo(pos)
    }

    map.onClick {
        if (player.isAddingMoveEnabled && it.button == MouseButton.RIGHT) {// TODO: refactor
            player.actions += getPath(
                player.lastPreviewPos,
                (it.currentPosLocal / tilesManager.tileSize).int.p,
                tilesManager[Layer.Walls]
            )
                .take(player.remainingActionPoints)
                .also { path ->
                    player.lastPreviewPos.setTo(path.last().second)
                    player.showPath(path)
                }
                .map { pathPart -> ActionType.Move to pathPart }
        }
    }
}
