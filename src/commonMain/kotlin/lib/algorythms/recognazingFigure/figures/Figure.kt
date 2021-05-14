package lib.algorythms.recognazingFigure.figures

import com.soywiz.korma.geom.Point
import mainModule.scenes.abstracts.AssetsManager
import lib.algorythms.recognazingFigure.adapters.getFigureMask
import lib.algorythms.recognazingFigure.core.ColorMatrix

const val MASK_SIZES = 16

interface Figure {
    val path: String

    companion object : AssetsManager {
        val figures: List<Figure> get() = listOf(AreaFigure.Triangle, AreaFigure.Circle, AreaFigure.Square)

        override suspend fun loadAssets() {
            figureMasks = mutableMapOf()
            figures.forEach { figure ->
                figureMasks[figure] = getFigureMask(figure)
            }
        }
    }
}

enum class AreaFigure : Figure {
    Triangle {
        override val path = "figures/triangle_mask.png"

        override lateinit var area: ClosedRange<Point>
    },
    Circle {
        override val path = "figures/circle_mask.png"

        override lateinit var area: ClosedRange<Point>
    },
    Square {
        override val path = "figures/square_mask.png"

        override lateinit var area: ClosedRange<Point>
    };

    abstract var area: ClosedRange<Point>
}

lateinit var figureMasks: MutableMap<Figure, ColorMatrix>
