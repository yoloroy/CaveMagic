package mainModule.scenes.gameScenes.gameScene.managers

import com.soywiz.korge.tiled.TiledMap
import com.soywiz.korge.tiled.readTiledMap
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import mainModule.scenes.abstracts.AssetsManager

open class GameSceneAssetsManager(private val tiledMapPath: String) : AssetsManager {
    lateinit var tiledMap: TiledMap

    lateinit var heroBitmap: Bitmap
    lateinit var sheepBitmap: Bitmap
    lateinit var skeletonBitmap: Bitmap

    lateinit var nextTurnBitmap: Bitmap
    lateinit var revertTurnBitmap: Bitmap

    lateinit var buttonMoveBitmap: Bitmap
    lateinit var buttonAttackBitmap: Bitmap
    lateinit var buttonMiscBitmap: Bitmap
    lateinit var buttonMagicBitmap: Bitmap

    lateinit var healthBarBackgroundBitmap: Bitmap
    lateinit var experienceBarBackgroundBitmap: Bitmap

    lateinit var skullBitmap: Bitmap

    override suspend fun loadAssets() {
        tiledMap = resourcesVfs[tiledMapPath].readTiledMap()

        heroBitmap = resourcesVfs["gfx/hero.png"].readBitmap()
        sheepBitmap = resourcesVfs["gfx/sheep.png"].readBitmap()
        skeletonBitmap = resourcesVfs["gfx/skeleton.png"].readBitmap()

        nextTurnBitmap = resourcesVfs["gfx/nextTurnButton.png"].readBitmap()
        revertTurnBitmap = resourcesVfs["gfx/buttonReturnTurn.png"].readBitmap()

        buttonMoveBitmap = resourcesVfs["gfx/buttonMove.png"].readBitmap()
        buttonAttackBitmap = resourcesVfs["gfx/buttonAttack.png"].readBitmap()
        buttonMiscBitmap = resourcesVfs["gfx/buttonMisc.png"].readBitmap()
        buttonMagicBitmap = resourcesVfs["gfx/buttonMagic.png"].readBitmap()

        skullBitmap = resourcesVfs["gfx/skull.png"].readBitmap()

        healthBarBackgroundBitmap = resourcesVfs["gfx/texture_bar_health.png"].readBitmap()
        experienceBarBackgroundBitmap = resourcesVfs["gfx/texture_bar_experience.png"].readBitmap()
    }
}