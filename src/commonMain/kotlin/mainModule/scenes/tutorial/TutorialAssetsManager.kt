package mainModule.scenes.tutorial

import com.soywiz.korge.tiled.TiledMap
import com.soywiz.korge.tiled.readTiledMap
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import mainModule.scenes.abstracts.AssetsManager

class TutorialAssetsManager : AssetsManager {
    lateinit var tiledMap: TiledMap
    lateinit var playerBitmap: Bitmap
    lateinit var sheepBitmap: Bitmap

    override suspend fun loadAssets() {
        tiledMap = resourcesVfs["gfx/sampleMap.tmx"].readTiledMap()
        playerBitmap = resourcesVfs["korge.png"].readBitmap()
        sheepBitmap = resourcesVfs["sheep.png"].readBitmap()
    }
}