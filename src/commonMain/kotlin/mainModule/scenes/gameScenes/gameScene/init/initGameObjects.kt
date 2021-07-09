package mainModule.scenes.gameScenes.gameScene.init

import com.soywiz.korma.geom.Point
import lib.exceptions.UnknownUnitException
import lib.extensions.xi
import lib.extensions.yi
import lib.tiledMapView.Layer
import logic.gameObjects.gameObject.GameObjectId
import logic.gameObjects.hero.ActionType
import logic.gameObjects.hero.Hero
import logic.gameObjects.sheep.Sheep
import logic.gameObjects.units.enemies.melee.simpleMeleeEnemy.SimpleMeleeEnemy
import logic.inventory.item.SkullItem
import mainModule.scenes.gameScenes.gameScene.GameScene

fun initGameObjects(scene: GameScene, onPlayerDeath: () -> Unit) = scene.apply {
    var playerPos = Point()

    tilesManager.forEachObject(Layer.GameObjects) { pos, id ->
        val type = GameObjectId.getTypeById(id)

        if (type == GameObjectId.Hero) {
            playerPos = pos
        } else {
            gameObjects += when (type) {
                GameObjectId.Sheep ->
                    Sheep(tilesManager, assetsManager.sheepBitmap, map, pos)
                GameObjectId.Skeleton ->
                    object : SimpleMeleeEnemy(pos, 2, 2, 1, tilesManager, GameObjectId.Skeleton, assetsManager.skeletonBitmap, map) {
                        override fun onDeath() {
                            super.onDeath()

                            tilesManager[pos.xi, pos.yi, Layer.Storage] = SkullItem.TILE_ID
                        }
                    }
                else -> {
                    println("$id: ${GameObjectId.getTypeById(id)}")
                    throw UnknownUnitException()
                }
            }
        }
    }


    hero = Hero(map, camera, gameObjects, tilesManager, assetsManager.heroBitmap, map, playerPos, actionType == ActionType.Move)
    hero.model.health.observe { // TODO: refactor
        if (it <= 0) {
            onPlayerDeath()
        }
    }

    gameObjects.add(0, hero)
}
