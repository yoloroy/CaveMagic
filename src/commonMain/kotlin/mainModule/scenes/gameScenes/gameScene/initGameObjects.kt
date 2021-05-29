package mainModule.scenes.gameScenes.gameScene

import com.soywiz.korma.geom.Point
import lib.exceptions.UnknownUnitException
import lib.tiledMapView.Layer
import logic.gameObjects.gameObject.GameObjectId
import logic.gameObjects.hero.ActionType
import logic.gameObjects.hero.Hero
import logic.gameObjects.sheep.Sheep
import logic.gameObjects.units.enemies.melee.simpleMeleeEnemy.SimpleMeleeEnemy

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
                    SimpleMeleeEnemy(pos, 2, 2, 1, tilesManager, GameObjectId.Skeleton, assetsManager.skeletonBitmap, map, corpseTile = 109)
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
