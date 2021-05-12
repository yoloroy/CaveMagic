package logic.magic

import com.soywiz.korio.lang.UnexpectedException
import algorythms.recognazingFigure.figures.AreaFigure
import algorythms.recognazingFigure.figures.Figure

val Figure.magic: Magic
    get() = when (this) {
        AreaFigure.Square -> AreaMagic.Square
        AreaFigure.Circle -> AreaMagic.Circle
        AreaFigure.Triangle -> AreaMagic.Wave
        else -> throw UnexpectedException()
    }
