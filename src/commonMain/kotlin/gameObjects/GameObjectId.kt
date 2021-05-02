package gameObjects

enum class GameObjectId(val id: Int) {
    Empty(0),
    Player(831),
    Sheep(832);

    companion object {
        fun getTypeById(id: Int) = when(id) {
            Player.id -> Player
            Sheep.id -> Sheep
            else -> Empty
        }
    }
}
