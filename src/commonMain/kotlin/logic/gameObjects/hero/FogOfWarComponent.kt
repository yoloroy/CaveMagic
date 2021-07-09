package logic.gameObjects.hero

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
import logic.gameObjects.gameObject.GameObject
import mainModule.scenes.gameScenes.gameScene.managers.MapTilesManager

class FogOfWarComponent(private val tilesManager: MapTilesManager, private val pos: Point, val gameObjects: List<GameObject>) {
    private var circle = MCircle(pos.copy(), 5.0)
    private var notShadowedGameObjects = mutableSetOf<GameObject>()

    init {
        gameObjects.forEach { it.visible = false }
        updateViewArea()
    }

    fun updateViewArea() {
        circle.points.forEach { point ->
            notShadowedGameObjects
                .forEach { it.visible = false }

            try {
                if (tilesManager[point.xi, point.yi, Layer.FogOfWar] != TILE_FOG_FULL) {
                    tilesManager[point.xi, point.yi, Layer.FogOfWar] = TILE_FOG_VISITED
                }
            } catch (e: IndexOutOfBoundsException) {
                println(e.message)
            }
        }

        circle = MCircle(pos.copy(), 8.0)

        circle.points.forEach { end ->
            run line@{
                intPointsLineIterator(pos.int, end.int).forEach linePoint@{ point ->
                    if (point.p !in tilesManager) {
                        return@linePoint
                    }

                    tilesManager[point.x, point.y, Layer.FogOfWar] = TILE_NO_FOG
                    notShadowedGameObjects.clear()
                    notShadowedGameObjects.addAll(
                        gameObjects
                            .filter { it.pos == point.p }
                            .onEach { go -> go.visible = true }
                    )

                    if (tilesManager[point.x, point.y, Layer.Walls] != 0 && point != pos.int) {
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
                println(e.message)
            }
        }
    }
}
