package mainModule.scenes.menuScene

import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.uiTextButton
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.position
import com.soywiz.korge.view.size
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korio.async.launchImmediately
import lib.extensions.sizePoint
import lib.extensions.y
import mainModule.MainModule
import mainModule.scenes.gameScenes.TutorialScene

class MainMenuScene : Scene() {
    private val textButtons = listOf(
        "Start Tutorial Level" to { goScene<TutorialScene>() },
        "Exit" to { views.stage.gameWindow.close() }
    )

    override suspend fun Container.sceneInit() {
        textButtons.forEachIndexed { i, (text, action) ->
            uiTextButton {
                this.text = text
                size(60.0, 6.4)
                textAlignment = TextAlignment.MIDDLE_CENTER
                textSize = 6.0
                position(MainModule.size.size.p / 2 - sizePoint / 2 + (sizePoint.y + 2).y * (i - textButtons.size.toDouble() / 2))

                onClick { action() }
            }
        }
    }

    private inline fun <reified SceneType : Scene> goScene() {
        launchImmediately(coroutineContext) { sceneContainer.changeTo(SceneType::class) }
    }
}
