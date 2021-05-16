package mainModule.scenes.gameScenes

import mainModule.widgets.DeathScreen
import mainModule.scenes.gameScenes.gameScene.GameScene
import mainModule.scenes.gameScenes.gameScene.initGameObjects

class TutorialScene : GameScene("gfx/sampleMap.tmx") {
    override fun initGameObjects() {
        initGameObjects(this) {
            DeathScreen(this, TutorialScene::class).show()
        }
    }
}
