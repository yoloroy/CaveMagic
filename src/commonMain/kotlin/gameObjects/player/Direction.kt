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

    val key get() = keysFromControls[this]!!

    abstract val vector: Point

    abstract infix fun isOppositeTo(other: Direction): Boolean
}

val Key.direction get() = controls.getOrElse(this) { Direction.Nowhere }

val Collection<Key>.isHorizontalBlocked get() = contains(Direction.Left.key) && contains(Direction.Right.key)

val Collection<Key>.isVerticalBlocked get() = contains(Direction.Up.key) && contains(Direction.Down.key)

val MutableList<Key>.withoutBlocks: Collection<Key> get() = toList().apply {
    if (isHorizontalBlocked) {
        remove(Direction.Left.key)
        remove(Direction.Right.key)
    }
    if (isVerticalBlocked) {
        remove(Direction.Up.key)
        remove(Direction.Down.key)
    }
}
