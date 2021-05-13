package logic.gameObjects

data class GameObjectModel(
    var healthLimit: Int,
    var actionPointsLimit: Int,
    var health: Int = healthLimit
)
