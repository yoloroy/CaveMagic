package logic.gameObjects.player

import com.soywiz.kds.flip
import com.soywiz.korev.Key

// TODO
val controls get() = mapOf(
    Key.UP to Direction.Up,
    Key.DOWN to Direction.Down,
    Key.LEFT to Direction.Left,
    Key.RIGHT to Direction.Right
)

val keysFromControls get() = controls.flip()
