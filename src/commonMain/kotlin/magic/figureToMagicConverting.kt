package magic

import com.soywiz.korio.lang.UnexpectedException
import recognazingFigure.figures.AreaFigure
import recognazingFigure.figures.Figure

fun Figure.getMagic(): Magic = when(this) {
    AreaFigure.Square -> AreaMagic.Square
    AreaFigure.Circle -> AreaMagic.Circle
    AreaFigure.Triangle -> AreaMagic.Wave
    else -> throw UnexpectedException()
}
