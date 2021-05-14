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
        val type = GameObjectId.getTypeById(id)

        if (type == GameObjectId.Player) {
            player = Player(map, camera, gameObjects, tilesManager, pos, actionType == ActionType.Move)
        } else {
            gameObjects += when (type) {
                GameObjectId.Sheep ->
                    Sheep(tilesManager, pos)
                GameObjectId.Skeleton ->
                    SimpleMeleeEnemy(pos, 2, 2, 1, tilesManager, GameObjectId.Skeleton, corpseTile = 109)
                else -> {
                    println("$id: ${GameObjectId.getTypeById(id)}")
                    throw UnknownUnitException()
                }
            }
        }
    }

    gameObjects.add(0, player)
}
