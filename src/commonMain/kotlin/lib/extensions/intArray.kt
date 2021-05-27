package lib.extensions

import com.soywiz.kds.IntArray2
import com.soywiz.korma.geom.Point

fun IntArray.setAll(values: List<Int>) = values.forEachIndexed { index, value -> set(index, value) }

fun sum(array1: IntArray2, array2: IntArray2) = array1.clone().apply {
    for (y in 0 until height)
    for (x in 0 until width) {
        set(x, y, get(x, y) + array2[x, y])
    }
}

operator fun IntArray2.contains(point: Point) = point.xi in 0 until width && point.yi in 0 until height
