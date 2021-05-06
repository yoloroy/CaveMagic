package logic.pathFinding

import com.soywiz.kds.IntArray2
import com.soywiz.korma.geom.Point
import utils.*

fun getPath(start: Point, end: Point, walls: IntArray2): List<Pair<Point, Point>> {
    val matrix = IntArray2(walls.width, walls.height, 1000)
    var frontier = mutableListOf(start)

    matrix[start] = 0
    while (frontier.isNotEmpty()) {
        val newFrontier = mutableListOf<Point>()

        frontier.forEach { cur ->
            cur.surroundings.forEach surroundings@{
                if (walls[it] != 0)
                    return@surroundings

                if (matrix[it] > matrix[cur] + 1) {
                    matrix[it] = matrix[cur] + 1
                    newFrontier += it
                }
            }
        }

        frontier = newFrontier.distinct().toMutableList()
    }

    val index = end.copy()
    val path = mutableListOf<Pair<Point, Point>>()

    while (index != start) {
        val previousPoint = index.surroundings.minByOrNull { matrix[it] } ?: return emptyList()//TODO

        path.add(previousPoint to index.copy())
        index.setTo(previousPoint)
    }

    return path.reversed()
}

private operator fun IntArray2.get(point: Point): Int = get(point.xi, point.yi)

val Point.surroundings get() = listOf(plus((-1).x), plus((+1).x), plus((-1).y), plus((+1).y))

private operator fun IntArray2.set(point: Point, value: Int) = set(point.xi, point.yi, value)
