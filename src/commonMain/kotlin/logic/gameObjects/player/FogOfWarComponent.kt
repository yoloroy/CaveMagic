package logic.gameObjects.player

import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.int
import ktres.TILE_FOG_BORDER
import ktres.TILE_FOG_FULL
import ktres.TILE_FOG_VISITED
import ktres.TILE_NO_FOG
import lib.extensions.fourNeighbours
import lib.extensions.xi
import lib.extensions.yi
import lib.math.MCircle
import lib.math.intPointsLineIterator
import lib.tiledMapView.Layer
import mainModule.scenes.gameScenes.gameScene.MapTilesManager

class FogOfWarComponent(private val tilesManager: MapTilesManager, private val pos: Point) {
    private var circle = MCircle(pos.copy(), 5.0)

    init {
        updateViewArea()
    }

    fun updateViewArea() {
        circle.points.forEach {
            try {
                if (tilesManager[it.xi, it.yi, Layer.FogOfWar] != TILE_FOG_FULL) {
                    tilesManager[it.xi, it.yi, Layer.FogOfWar] = TILE_FOG_VISITED
                }
            } catch (e: IndexOutOfBoundsException) {
                e.printStackTrace()
            }
        }

        circle = MCircle(pos.copy(), 5.0)

        circle.points.forEach { end ->
            run line@{
                intPointsLineIterator(pos.int, end.int).forEach linePoint@{
                    if (it.p !in tilesManager) {
                        return@linePoint
                    }

                    tilesManager[it.x, it.y, Layer.FogOfWar] = TILE_NO_FOG

                    if (tilesManager[it.x, it.y, Layer.Walls] != 0 && it != pos.int) {
                        return@line
                    }
                }
            }
        }
        circle.points.forEach {
            try {
                if (it in tilesManager
                    && tilesManager[it.xi, it.yi, Layer.FogOfWar] == TILE_NO_FOG
                    && it.int.fourNeighbours.any { neighbour ->
                        neighbour.p !in tilesManager ||
                                tilesManager[neighbour.x, neighbour.y, Layer.FogOfWar] == TILE_FOG_FULL
                    }
                ) {
                    tilesManager[it.xi, it.yi, Layer.FogOfWar] = TILE_FOG_BORDER
                }
            } catch (e: IndexOutOfBoundsException) {
                e.printStackTrace()
            }
        }
    }
}
