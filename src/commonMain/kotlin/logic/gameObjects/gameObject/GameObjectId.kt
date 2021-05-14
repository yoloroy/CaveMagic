package logic.gameObjects.gameObject

enum class GameObjectId(val id: Int) {
    Empty(0),
    Player(831),
    Sheep(832),
    Skeleton(833);

    companion object {
        fun getTypeById(id: Int) = when(id) {
            Player.id -> Player
            Sheep.id -> Sheep
            Skeleton.id -> Skeleton
            else -> Empty
        }
    }
}
