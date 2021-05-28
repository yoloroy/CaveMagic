package mainModule.scenes.menuScene

import com.soywiz.korge.input.onUp
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.uiTextButton
import com.soywiz.korge.view.Container
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korio.async.launchImmediately
import com.soywiz.korma.geom.Point
import lib.extensions.y
import mainModule.MainModule
import mainModule.scenes.gameScenes.TutorialScene
import mainModule.widgets.listView

class MainMenuScene : Scene() {
    private val buttonsData = listOf(
        "Start Tutorial Level" to { goScene<TutorialScene>() },
        "Exit" to { views.stage.gameWindow.close() }
    )

    override suspend fun Container.sceneInit() {
        val buttonSizePoint = Point(60.0, 6.4)
        val textSize = 6.0
        val paddingY = 2.0

        val deltaPoint = (buttonSizePoint.y + paddingY).y
        val centerPoint = MainModule.size.size.p / 2
        val firstButtonPosition = centerPoint - buttonSizePoint / 2 - deltaPoint * buttonsData.size / 2.0

        listView(
            buttonsData,
            firstButtonPosition,
            buttonSizePoint * Point(1, buttonsData.size),
            paddingY
        ) { (text, action) ->
            uiTextButton {
                this.text = text
                textAlignment = TextAlignment.MIDDLE_CENTER
                this.textSize = textSize

                onUp { action() }
            }
        }
    }

    private inline fun <reified SceneType : Scene> goScene() {
        launchImmediately(coroutineContext) { sceneContainer.changeTo(SceneType::class) }
    }
}
