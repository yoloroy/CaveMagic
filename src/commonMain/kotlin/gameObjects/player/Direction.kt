package gameObjects.player

import com.soywiz.korev.Key
import com.soywiz.korma.geom.Point
import utils.point
import utils.unaryMinus

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

    abstract val vector: Point

    abstract infix fun isOppositeTo(other: Direction): Boolean
}

val Key.direction get() = getControls().getOrElse(this) { Direction.Nowhere }
