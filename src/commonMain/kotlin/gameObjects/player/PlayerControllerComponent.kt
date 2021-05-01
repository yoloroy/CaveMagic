package gameObjects.player

import com.soywiz.korev.Key
import com.soywiz.korev.KeyEvent
import com.soywiz.korge.component.KeyComponent
import com.soywiz.korge.view.View
import com.soywiz.korge.view.Views

class PlayerControllerComponent(override val view: View) : KeyComponent {
    val direction: Direction get() = pressedKeys.withoutBlocks.lastOrNull()?.direction ?: Direction.Nowhere

    private var pressedKeys = mutableListOf<Key>()

    override fun Views.onKeyEvent(event: KeyEvent) {
        pressedKeys.add(event.key)
        when (event.type) {
            KeyEvent.Type.DOWN -> onPressed(event.key)
            KeyEvent.Type.UP -> onUnpressed(event.key)
            else -> Unit
        }
    }

    private fun onPressed(key: Key) = pressedKeys.add(key)

    private fun onUnpressed(key: Key) = pressedKeys.remove(key)
}
