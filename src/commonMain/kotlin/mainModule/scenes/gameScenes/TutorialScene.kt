package mainModule.scenes.gameScenes

import com.soywiz.korge.input.onUp
import com.soywiz.korge.ui.uiTextButton
import com.soywiz.korge.view.position
import lib.extensions.size
import lib.extensions.sizePoint
import lib.tiledMapView.Layer
import mainModule.MainModule
import mainModule.scenes.gameScenes.gameScene.GameScene
import mainModule.scenes.gameScenes.gameScene.init.initGameObjects
import mainModule.widgets.DeathScreen

class TutorialScene : GameScene("gfx/sampleMap.tmx") {
    override fun initGameContent() {
        initGameObjects(this) {
            DeathScreen(this, TutorialScene::class).show()
        }
        initEvents()
    }
}

private fun TutorialScene.initEvents() {
    tilesManager.forEachObject(Layer.Events) { pos, id ->
        when (id) {
            1 -> events += {
                if (hero.pos == pos) {
                    sceneContainer.uiTextButton {
                        text = "Go into door for start test level"
                        textSize = 8.0
                        size(MainModule.size.size.p * 2 / 3)
                        position((MainModule.size.size.p - sizePoint) / 2)

                        onUp {
                            sceneContainer.removeChild(this)
                        }
                    }
                }
            }
        }
    }
}
