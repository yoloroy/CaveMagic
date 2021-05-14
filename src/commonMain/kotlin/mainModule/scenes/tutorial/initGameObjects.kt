package mainModule.scenes.tutorial

import exceptions.UnknownUnitException
import logic.gameObjects.gameObject.GameObjectId
import logic.gameObjects.player.ActionType
import logic.gameObjects.player.Player
import logic.gameObjects.sheep.Sheep
import logic.gameObjects.units.simpleMeleeEnemy.SimpleMeleeEnemy
import utils.tiledMapView.Layer

fun initGameObjects(scene: TutorialScene) = scene.apply {
    tilesManager.forEachObject(Layer.GameObjects) { pos, id ->
        gameObjects += when (GameObjectId.getTypeById(id)) {
            GameObjectId.Player ->
                Player(map, camera, gameObjects, tilesManager, pos, actionType == ActionType.Move).also { player = it }
            GameObjectId.Sheep ->
                Sheep(tilesManager, pos)
            GameObjectId.Skeleton ->
                SimpleMeleeEnemy(pos, 2, 2, 1, tilesManager, GameObjectId.Skeleton)
            else -> {
                println("$id: ${GameObjectId.getTypeById(id)}")
                throw UnknownUnitException()
            }
        }
    }
}
