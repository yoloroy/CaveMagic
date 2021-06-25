package mainModule.scenes.gameScenes.gameScene

import com.soywiz.korge.input.onDown
import com.soywiz.korge.input.onUp
import com.soywiz.korge.view.*
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korma.geom.Point
import lib.extensions.*
import logic.gameObjects.hero.ActionType
import logic.inventory.widgets.inventoryListView
import mainModule.MainModule
import mainModule.widgets.valueBar

internal fun Container.initUI(scene: GameScene) = scene.apply {
    initTurnActivityButtons(this@initUI, this)
    initPlayerActivityButtons(this@initUI, this)
    initHeroCharacteristicsUI(this@initUI, this)
}

private fun initHeroCharacteristicsUI(container: Container, gameScene: GameScene) {
    val model = gameScene.hero.model

    val healthBar = container.valueBar(
        model.healthLimit.value,
        gameScene.assetsManager.healthBarBackgroundBitmap,
        model.health.value
    )
    model.apply {
        health.observe {
            healthBar.value = it
        }
        healthLimit.observe {
            healthBar.limit = it
        }
    }
}

private fun initTurnActivityButtons(container: Container, gameScene: GameScene) {
    val nextTurnButton = container.image(gameScene.assetsManager.nextTurnBitmap) {
        smoothing = false
        anchor(2, 1)
        size(16, 16)
        position(MainModule.size.size.p - Point(4) + Point(.0, 0.5))

        var clickable = true
        onDown {
            if (clickable) {
                flip()
            }
        }
        onUp {
            if (clickable) {
                clickable = false
                try {
                    gameScene.makeTurn()
                } catch (e: Exception) {
                    print(e.stackTraceToString())
                }
                flip()
                clickable = true
            }
        }
    }
    container.image(gameScene.assetsManager.revertTurnBitmap) {
        smoothing = false
        anchor(1, 1)
        size(12, 12)
        position(nextTurnButton.pos + nextTurnButton.size * Point.Left.point * 2 - 1.x)

        onDown { flip() }
        onUp {
            flip()
            gameScene.hero.removeLastAction()
        }
    }

    container.text(gameScene.oCurrentPhase.value.text) {
        gameScene.oCurrentPhase.observe {
            text = it.text
        }

        textSize = 3.8
        size(nextTurnButton.width - 4, 4.0)

        alignment = TextAlignment.TOP_CENTER

        setPositionRelativeTo(
            nextTurnButton,
            -nextTurnButton.size * nextTurnButton.anchor
                    + sizePoint * Point(0.5, -1.0)
        )
    }
}

private fun initPlayerActivityButtons(container: Container, gameScene: GameScene) {
    val bottomCenter = MainModule.size.size.p + MainModule.size.size.p * Point.Left.point / 2
    val buttonAttack = container.image(gameScene.assetsManager.buttonAttackBitmap) {
        smoothing = false
        anchor(1, 1)
        size(16, 16)
        position(bottomCenter + Point.Down.point * 2 + Point.Left.point * 1)

        onDown { flip() }
        onUp {
            flip()
            gameScene.actionType = ActionType.Attack
        }
    }
    val buttonMisc = container.image(gameScene.assetsManager.buttonMiscBitmap) {
        smoothing = false
        anchor(0, 1)
        size(16, 16)
        position(bottomCenter + Point.Down.point * 2 + Point.Right.point * 1)

        onDown { flip() }

        var inventory: Container? = null
        onUp {
            flip()

            inventory?.run {
                removeChildren()
                removeFromParent()

                inventory = null
                return@onUp
            }

            if (gameScene.hero.model.items.isNotEmpty()) {
                val resultSize = Point(48, 20 * gameScene.hero.model.items.size)
                inventory = container.inventoryListView(
                    gameScene.hero.model.items,
                    pos + size * Point(1, -1) - resultSize * Point(0, 1),
                    resultSize
                )
            }
        }
    }
    container.image(gameScene.assetsManager.buttonMoveBitmap) {
        smoothing = false
        anchor(1, 1)
        size(16, 16)
        setPositionRelativeTo(buttonAttack, -scaledSize * Point.Horizontal + Point.Left.point * 2)

        onDown { flip() }
        onUp {
            flip()
            gameScene.actionType = ActionType.Move
        }
    }
    container.image(gameScene.assetsManager.buttonMagicBitmap) {
        smoothing = false
        anchor(0, 1)
        size(16, 16)
        setPositionRelativeTo(buttonMisc, +scaledSize * Point.Horizontal + Point.Right.point * 2)

        onDown { flip() }
        onUp {
            flip()
            gameScene.actionType = ActionType.MagicDrawing
        }
    }
}

private fun Image.flip() {
    scale(-scaleX, -scaleY)
    anchor(1 - anchorX, 1 - anchorY)
}
