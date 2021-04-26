package gameObjects.player

import com.soywiz.korev.Key

// TODO
fun getControls() = mapOf(
    Key.UP to Direction.Up,
    Key.DOWN to Direction.Down,
    Key.LEFT to Direction.Left,
    Key.RIGHT to Direction.Right
)
