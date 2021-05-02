package gameObjects.gameObject

import com.soywiz.korma.geom.Point
import gameObjects.GameObjectId
import gameObjects.logic.Turnable

abstract class GameObject : Turnable {
    abstract val tile: GameObjectId

    var isAlive: Boolean = true

    abstract val pos: Point

    abstract fun delete()
}
