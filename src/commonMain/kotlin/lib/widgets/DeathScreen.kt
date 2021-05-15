package lib.widgets

import com.soywiz.korge.input.onClick
import com.soywiz.korge.ui.uiTextButton
import com.soywiz.korge.view.*
import com.soywiz.korio.async.launchImmediately
import com.soywiz.korma.geom.Point
import lib.extensions.point
import lib.extensions.size
import lib.extensions.sizePoint
import lib.extensions.y
import mainModule.MainModule
import mainModule.scenes.gameScenes.gameScene.GameScene

class DeathScreen(val scene: GameScene) {
    inline fun <reified SceneType : GameScene> show() = scene.sceneView.apply container@{
        val center = MainModule.size.size.p / 2

        val text = text("YOU DIED") {
            size(MainModule.size.width / 3, MainModule.size.height / 5)
            position(center + sizePoint * Point(-1, -2) / 2)
        }

        uiTextButton(text = "Go menu") {
            size(text.sizePoint / 3)
            textSize = height * 0.8
            position(center + text.height.y / 2 + sizePoint * Point.Left.point)

            onClick {
                // TODO: create menu scene
            }
        }

        uiTextButton(text = "Restart") {
            size(text.sizePoint / 3)
            textSize = height * 0.8
            position(center + text.height.y / 2)

            onClick {
                launchImmediately(scene.coroutineContext) { scene.sceneContainer.changeTo<SceneType>() }
            }
        }
    }
}
