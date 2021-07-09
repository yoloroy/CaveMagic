package logic.inventory.item

import logic.gameObjects.gameObject.GameObject
import mainModule.scenes.gameScenes.gameScene.managers.GameSceneAssetsManager
import kotlin.reflect.KClass

class SkullItem(
    assetsManager: GameSceneAssetsManager,
    private val user: GameObject
) : Item(
    TILE_ID,
    assetsManager.skullBitmap,
    NAME,
    DESCRIPTION,
    { user.model.health.value += 1 }
) {
    companion object {
        const val TILE_ID = 109
        const val NAME = "Skull"
        const val DESCRIPTION = "Heal 1hp"
    }
}

val Int.itemClass: KClass<*> get() = when(this) {
    SkullItem.TILE_ID -> SkullItem::class
    else -> throw Exception("Unknown item")
}
