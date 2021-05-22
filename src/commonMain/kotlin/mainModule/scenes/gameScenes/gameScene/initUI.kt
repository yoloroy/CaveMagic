package mainModule.scenes.gameScenes.gameScene

import com.soywiz.korge.input.onDown
import com.soywiz.korge.input.onUp
import com.soywiz.korge.view.*
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korma.geom.Point
import lib.extensions.*
import logic.gameObjects.player.ActionType
import mainModule.MainModule
import mainModule.widgets.valueBar

internal fun Container.initUI(scene: GameScene) = scene.apply {
    fun Image.flip() {
        scale(-scaleX, -scaleY)
        anchor(1 - anchorX, 1 - anchorY)
    }

    val nextTurnButton = image(assetsManager.nextTurnBitmap) {
        smoothing = false
        anchor(2, 1)
        size(16, 16)
        position(MainModule.size.size.p - Point(4) + Point(.0, 0.5))

        onDown { flip() }
        onUp {
            flip()
            makeTurn()
        }
    }
    image(assetsManager.revertTurnBitmap) {
        smoothing = false
        anchor(1, 1)
        size(12, 12)
        position(nextTurnButton.pos + nextTurnButton.size * Point.Left.point * 2 - 1.x)

        onDown { flip() }
        onUp {
            flip()
            player.removeLastAction()
        }
    }

    text(oCurrentPhase.value.text) {
        oCurrentPhase.observe {
            text = it.text
        }

        textSize = 3.8
        size(nextTurnButton.width - 4, 4.0)

        alignment = TextAlignment.TOP_CENTER

        setPositionRelativeTo(nextTurnButton,
            - nextTurnButton.size * nextTurnButton.anchor
            + sizePoint * Point(0.5, -1.0)
        )
    }

    val bottomCenter = MainModule.size.size.p + MainModule.size.size.p * Point.Left.point / 2
    val buttonAttack = image(assetsManager.buttonAttackBitmap) {
        smoothing = false
        anchor(1, 1)
        size(16, 16)
        position(bottomCenter + Point.Down.point * 2 + Point.Left.point * 1)

        onDown { flip() }
        onUp {
            flip()
            actionType = ActionType.Attack
        }
    }
    val buttonMisc = image(assetsManager.buttonMiscBitmap) {
        smoothing = false
        anchor(0, 1)
        size(16, 16)
        position(bottomCenter + Point.Down.point * 2 + Point.Right.point * 1)

        onDown { flip() }
        onUp { flip() }
    }
    image(assetsManager.buttonMoveBitmap) {
        smoothing = false
        anchor(1, 1)
        size(16, 16)
        setPositionRelativeTo(buttonAttack, -scaledSize * Point.Horizontal + Point.Left.point * 2)

        onDown { flip() }
        onUp {
            flip()
            actionType = ActionType.Move
        }
    }
    image(assetsManager.buttonMagicBitmap) {
        smoothing = false
        anchor(0, 1)
        size(16, 16)
        setPositionRelativeTo(buttonMisc, +scaledSize * Point.Horizontal + Point.Right.point * 2)

        onDown { flip() }
        onUp {
            flip()
            actionType = ActionType.MagicDrawing
        }
    }

    val healthBar = valueBar(player.model.healthLimit.value, player.model.health.value)
    player.model.apply {
        health.observe {
            healthBar.value = it
        }
        healthLimit.observe {
            healthBar.limit = it
        }
    }
}
