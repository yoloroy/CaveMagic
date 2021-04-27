package gameObjects

enum class GameObjectId(val id: Int) {
    Empty(0),
    Player(1),
    Sheep(2);

    companion object {
        fun getTypeById(id: Int) = when(id) {
            Player.id -> Player
            Sheep.id -> Sheep
            else -> Empty
        }
    }
}
