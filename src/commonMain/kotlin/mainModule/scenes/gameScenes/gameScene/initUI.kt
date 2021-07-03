package mainModule.scenes.gameScenes.gameScene

import com.soywiz.korge.input.onDown
import com.soywiz.korge.input.onUp
import com.soywiz.korge.view.*
import com.soywiz.korma.geom.Point
import lib.extensions.*
import logic.gameObjects.hero.ActionType
import logic.inventory.widgets.inventoryListView
import mainModule.MainModule
import mainModule.widgets.ValueBar
import mainModule.widgets.valueBar

internal fun Container.initUI(scene: GameScene) = scene.apply {
    initTurnActivityUI(this)
    initPlayerActivityButtons(this)
    initHeroCharacteristicsUI(this)
}

private fun Container.initHeroCharacteristicsUI(gameScene: GameScene) {
    this.initHeroHealthUI(gameScene)
    this.initHeroExperienceUI(gameScene)
}

private fun Container.initHeroExperienceUI(gameScene: GameScene) {
    val model = gameScene.hero.model

    val expBar = valueBar(
        10,
        gameScene.assetsManager.experienceBarBackgroundBitmap,
        model.experience.value,
        position = (ValueBar.DEFAULT_SIZE.x / 2).x + Point(0, 5)
    )
    model.apply {
        experience.observe {
            expBar.value = it
        }
    }
}

private fun Container.initHeroHealthUI(gameScene: GameScene) {
    val model = gameScene.hero.model

    val healthBar = valueBar(
        model.healthLimit.value,
        gameScene.assetsManager.healthBarBackgroundBitmap,
        model.health.value,
        position = (ValueBar.DEFAULT_SIZE.x / 2).x
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

private fun Container.initTurnActivityUI(gameScene: GameScene) {
    val nextTurnButton = image(gameScene.assetsManager.nextTurnBitmap) {
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
    val revertTurnButton = image(gameScene.assetsManager.revertTurnBitmap) {
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

    initAPViewerBar(gameScene, revertTurnButton)
}

private fun Container.initAPViewerBar(gameScene: GameScene, revertTurnButton: Image) {
    val hero = gameScene.hero
    val actionPointsLimit = hero.model.actionPointsLimit

    val bar = valueBar(
        actionPointsLimit.value,
        value = hero.remainingActionPoints, // TODO: refactor
        size = Point(12, 4),
        position = revertTurnButton.pos - revertTurnButton.size * revertTurnButton.anchor - Point(6, 4)
    )

    actionPointsLimit.observe {
        bar.limit = it
    }
    hero.actions.observe {
        bar.value = hero.remainingActionPoints
    }
}

private fun Container.initPlayerActivityButtons(gameScene: GameScene) {
    val bottomCenter = MainModule.size.size.p + MainModule.size.size.p * Point.Left.point / 2
    val buttonAttack = image(gameScene.assetsManager.buttonAttackBitmap) {
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
    val buttonMisc = image(gameScene.assetsManager.buttonMiscBitmap) {
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
                inventory = inventoryListView(
                    gameScene.hero.model.items,
                    pos + size * Point(1, -1) - resultSize * Point(0, 1),
                    resultSize
                )
            }
        }
    }
    image(gameScene.assetsManager.buttonMoveBitmap) {
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
    image(gameScene.assetsManager.buttonMagicBitmap) {
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
