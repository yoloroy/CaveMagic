package mainModule.scenes.gameScenes.gameScene

import com.soywiz.korge.input.mouse
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.vector.lineTo
import lib.algorythms.recognazingFigure.core.figure
import lib.algorythms.recognazingFigure.figures.AreaFigure
import lib.algorythms.recognazingFigure.figures.Figure
import lib.algorythms.recognazingFigure.core.Point as RecPoint
import mainModule.MainModule
import lib.extensions.area

class SceneFigureRecognitionComponent(
    private val scene: Scene,
    private val response: (Figure) -> Unit = {}
) {
    private var mouseObservingEnabled = false
    private var mouseObserving = false

    private val mousePositions = mutableListOf<Point>()

    private val figureDrawing = mutableListOf<Graphics>()

    private inline val scale get() = MainModule.size.size.p / scene.sceneView.windowBounds.size.p

    fun enableObserving() {
        mouseObservingEnabled = true
    }

    fun unableObserving() {
        mouseObservingEnabled = false
    }

    fun initFigureDrawing(container: Container) {
        container.mouse.apply {
            onDown {
                mouseObserving = mouseObservingEnabled
            }

            onMove {
                if (mouseObserving) {
                    mousePositions += scene.views.input.mouse.copy() * scale
                    container.displayNewSegment(mousePositions, figureDrawing)
                }
            }

            onUp {
                if (mouseObserving) {
                    unableObserving()
                    mouseObserving = false

                    sendResponse(mousePositions.toRecognitionPoints().figure)
                    mousePositions.clear()
                    clear(figureDrawing)
                }
            }
        }
    }

    private fun sendResponse(figure: Figure) {
        response(
            when (figure) {
                is AreaFigure -> figure.apply { area = mousePositions.area }
                else -> figure
            }
        )
    }

    private fun clear(figureDrawing: MutableList<Graphics>) {
        figureDrawing.apply {
            forEach {
                it.clear()
                it.removeFromParent()
                it.invalidate()
            }
            clear()
        }
    }

    private fun Container.displayNewSegment(
        mousePositions: MutableList<Point>,
        figureDrawing: MutableList<Graphics>
    ) {
        if (mousePositions.size > 2) {
            figureDrawing += graphics {
                scale(0.5, 0.5)
                stroke(RGBA(0xff, 0xff, 0xff), StrokeInfo()) {
                    lineTo(mousePositions[mousePositions.size - 3] * 2 + Point(18, 0))
                    lineTo(mousePositions[mousePositions.size - 2] * 2 + Point(18, 0))
                    lineTo(mousePositions[mousePositions.size - 1] * 2 + Point(18, 0))
                }
            }
        }
    }

    private fun List<Point>.toRecognitionPoints() = map { RecPoint(it.x, it.y) }
}
