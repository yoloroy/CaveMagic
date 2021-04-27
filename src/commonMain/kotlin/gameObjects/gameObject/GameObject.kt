package gameObjects.gameObject

import com.soywiz.korma.geom.Point
import gameObjects.logic.Turnable

abstract class GameObject : Turnable {
    abstract val pos: Point

    abstract fun delete()
}
