package gameObjects.gameObject

import gameObjects.logic.Turnable

abstract class GameObject : Turnable {
    abstract fun delete()
}
