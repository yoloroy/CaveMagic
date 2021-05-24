package lib.extensions

import com.soywiz.kds.IntArray2

fun IntArray.setAll(values: List<Int>) = values.forEachIndexed { index, value -> set(index, value) }

fun sum(array1: IntArray2, array2: IntArray2) = array1.clone().apply {
    for (y in 0 until height)
    for (x in 0 until width) {
        set(x, y, get(x, y) + array2[x, y])
    }
}
