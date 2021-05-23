package mainModule.scenes.menuScene

import com.soywiz.korge.input.onUp
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.uiTextButton
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.position
import com.soywiz.korge.view.size
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korio.async.launchImmediately
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.Size
import lib.extensions.y
import mainModule.MainModule
import mainModule.scenes.gameScenes.TutorialScene

class MainMenuScene : Scene() {
    private val textButtons = listOf(
        "Start Tutorial Level" to { goScene<TutorialScene>() },
        "Exit" to { views.stage.gameWindow.close() }
    )

    override suspend fun Container.sceneInit() {
        val buttonSizePoint = Point(60.0, 6.4)
        val buttonSize = Size(buttonSizePoint)
        val textSize = 6.0

        val deltaPoint = (buttonSizePoint.y + 2).y
        val centerPoint = MainModule.size.size.p / 2
        val firstButtonPosition = centerPoint - buttonSizePoint / 2 - deltaPoint * textButtons.size / 2.0

        textButtons.forEachIndexed { i, (text, action) ->
            uiTextButton {
                this.text = text
                size(buttonSize.width, buttonSize.height)
                textAlignment = TextAlignment.MIDDLE_CENTER
                this.textSize = textSize
                position(firstButtonPosition + deltaPoint * i)

                onUp { action() }
            }
        }
    }

    private inline fun <reified SceneType : Scene> goScene() {
        launchImmediately(coroutineContext) { sceneContainer.changeTo(SceneType::class) }
    }
}
