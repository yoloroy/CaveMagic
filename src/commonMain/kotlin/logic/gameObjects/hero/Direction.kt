package logic.gameObjects.hero

import com.soywiz.korma.geom.Point
import lib.extensions.point
import lib.extensions.unaryMinus

enum class Direction {
    Nowhere {
        override val vector = Point.Zero.point

        override fun isOppositeTo(other: Direction) = true
    },
    Up {
        override val vector = -Point.Up.point

        override fun isOppositeTo(other: Direction) = other == Down
    },
    Down {
        override val vector = -Point.Down.point

        override fun isOppositeTo(other: Direction) = other == Up
    },
    Left {
        override val vector = Point.Left.point

        override fun isOppositeTo(other: Direction) = other == Right
    },
    Right {
        override val vector = Point.Right.point

        override fun isOppositeTo(other: Direction) = other == Left
    };

    val key get() = keysFromControls[this]!!

    abstract val vector: Point

    abstract infix fun isOppositeTo(other: Direction): Boolean
}
