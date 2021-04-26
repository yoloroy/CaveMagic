package gameObjects.player

import com.soywiz.korev.KeyEvent
import com.soywiz.korge.component.KeyComponent
import com.soywiz.korge.view.View
import com.soywiz.korge.view.Views

class PlayerControllerComponent(override val view: View) : KeyComponent {
    var direction: Direction = Direction.Nowhere

    override fun Views.onKeyEvent(event: KeyEvent) {
        when (event.type) {
            KeyEvent.Type.DOWN -> onPressed(event.key.direction)
            KeyEvent.Type.UP -> onUnpressed(event.key.direction)
            else -> Unit
        }
    }

    private fun onPressed(direction: Direction) {
        this.direction = if (direction isOppositeTo this.direction)
            Direction.Nowhere
        else
            direction
    }

    private fun onUnpressed(direction: Direction) {
        if (this.direction == direction)
            this.direction = Direction.Nowhere
    }
}
