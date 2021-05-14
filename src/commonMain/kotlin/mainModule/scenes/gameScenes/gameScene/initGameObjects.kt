package mainModule.scenes.gameScenes.gameScene

import lib.exceptions.UnknownUnitException
import logic.gameObjects.gameObject.GameObjectId
import logic.gameObjects.player.ActionType
import logic.gameObjects.player.Player
import logic.gameObjects.sheep.Sheep
import logic.gameObjects.units.simpleMeleeEnemy.SimpleMeleeEnemy
import lib.tiledMapView.Layer

fun initGameObjects(scene: GameScene) = scene.apply {
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
