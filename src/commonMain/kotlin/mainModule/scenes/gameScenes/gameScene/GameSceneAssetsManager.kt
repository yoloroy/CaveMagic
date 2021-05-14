package mainModule.scenes.gameScenes.gameScene

import com.soywiz.korge.tiled.TiledMap
import com.soywiz.korge.tiled.readTiledMap
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import mainModule.scenes.abstracts.AssetsManager

open class GameSceneAssetsManager(private val tiledMapPath: String) : AssetsManager {
    lateinit var tiledMap: TiledMap
    lateinit var playerBitmap: Bitmap
    lateinit var sheepBitmap: Bitmap

    lateinit var nextTurnBitmap: Bitmap
    lateinit var buttonMoveBitmap: Bitmap
    lateinit var buttonAttackBitmap: Bitmap
    lateinit var buttonMiscBitmap: Bitmap
    lateinit var buttonMagicBitmap: Bitmap

    override suspend fun loadAssets() {
        tiledMap = resourcesVfs[tiledMapPath].readTiledMap()
        playerBitmap = resourcesVfs["gfx/korge.png"].readBitmap()
        sheepBitmap = resourcesVfs["gfx/sheep.png"].readBitmap()

        nextTurnBitmap = resourcesVfs["gfx/nextTurnButton.png"].readBitmap()
        buttonMoveBitmap = resourcesVfs["gfx/buttonMove.png"].readBitmap()
        buttonAttackBitmap = resourcesVfs["gfx/buttonAttack.png"].readBitmap()
        buttonMiscBitmap = resourcesVfs["gfx/buttonMisc.png"].readBitmap()
        buttonMagicBitmap = resourcesVfs["gfx/buttonMagic.png"].readBitmap()
    }
}