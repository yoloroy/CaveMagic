package logic.magic

import com.soywiz.korio.lang.UnexpectedException
import lib.algorythms.recognazingFigure.figures.AreaFigure
import lib.algorythms.recognazingFigure.figures.Figure
import lib.algorythms.recognazingFigure.figures.SymbolFigure

val Figure.magic: Magic
    get() = when (this) {
        AreaFigure.Square -> AreaMagic.Square
        AreaFigure.Circle -> AreaMagic.Circle
        AreaFigure.Triangle -> AreaMagic.Wave
        SymbolFigure.Lightning -> DamageMagic.Lightning
        else -> throw UnexpectedException()
    }
