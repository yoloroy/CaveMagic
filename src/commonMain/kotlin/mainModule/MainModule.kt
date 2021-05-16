package mainModule

import com.soywiz.korge.scene.Module
import com.soywiz.korinject.AsyncInjector
import com.soywiz.korma.geom.ScaleMode
import com.soywiz.korma.geom.SizeInt
import mainModule.scenes.gameScenes.TutorialScene
import mainModule.scenes.menuScene.MainMenuScene

object MainModule : Module() {
    override val mainScene = MainMenuScene::class

    override val fullscreen: Boolean get() = true

    override val size: SizeInt = SizeInt(210, 120)

    override val scaleMode: ScaleMode
        get() = ScaleMode.COVER

    override suspend fun AsyncInjector.configure() {
        mapPrototype { MainMenuScene() }
        mapPrototype { TutorialScene() }
    }
}