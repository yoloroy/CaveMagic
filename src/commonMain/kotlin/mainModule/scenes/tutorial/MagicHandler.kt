package mainModule.scenes.tutorial

import com.soywiz.kds.IntArray2
import com.soywiz.korma.geom.Point
import magic.AreaMagic
import utils.*
import utils.math.MCircle
import utils.tiledMapView.Layer
import kotlin.math.abs

class MagicHandler(private val tutorialScene: TutorialScene) {
    private val tilesManager get() = tutorialScene.tilesManager

    fun onAreaMagic(magic: AreaMagic, drawingSquare: ClosedRange<Point>) = when(magic) {
        AreaMagic.Square -> onSquare(drawingSquare)
        AreaMagic.Circle -> onCircle(drawingSquare)
        AreaMagic.Wave -> onWave(drawingSquare)
    }

    private fun onSquare(square: ClosedRange<Point>) {
        tutorialScene.sheeps
            .filter { it.pos in square }
            .forEach { it.delete() } // TODO: move objects work to tiles manager

        tilesManager[square.iterator(), Layer.Walls] = 563 // kust
    }

    private fun onCircle(square: ClosedRange<Point>) {
        val center = (square.start to square.endInclusive).center
        val radius = abs(square.start.x - center.x)
        val circle = MCircle(center, radius)

        tutorialScene.sheeps
            .filter { it.pos in circle }
            .forEach { it.delete() } // TODO: move to tiles manager

        tilesManager[circle.points, Layer.Walls] = 563
    }

    private fun onWave(square: ClosedRange<Point>) {
        val size = (square.endInclusive - square.start)
        val saved = IntArray(size.yi)

        tutorialScene.addWork(List(size.xi + 1) { x -> onTurn@{
            val topPoint = Point(square.start.x + x, square.start.y)
            val bottomPoint = Point(square.start.x + x, square.start.y + size.yi - 1)

            val range = (topPoint..bottomPoint)

            if (x > 0) // returning tiles back
                tilesManager[range + Point(-1, 0), Layer.Walls] = IntArray2(range.width, range.height, saved)

            if (x == size.x.toInt())
                return@onTurn

            saved.setAll(tilesManager[range, Layer.Walls].map { it.first() })

            tilesManager[range.iterator(), Layer.Walls] = 563
        }})
    }
}
