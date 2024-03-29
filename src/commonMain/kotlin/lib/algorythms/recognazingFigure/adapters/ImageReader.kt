package lib.algorythms.recognazingFigure.adapters

import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import lib.algorythms.recognazingFigure.core.ColorMatrix
import lib.algorythms.recognazingFigure.core.setRGB
import lib.algorythms.recognazingFigure.figures.Figure

suspend fun getFigureMask(figure: Figure): ColorMatrix = resourcesVfs[figure.path].readBitmap().toColorMatrix()

private fun Bitmap.toColorMatrix(): Array<Array<Color>> = ColorMatrix(width, height).apply {
    this@toColorMatrix.forEach { _, x, y ->
        setRGB(x, y, getRgba(x, y).rgb)
    }
}
