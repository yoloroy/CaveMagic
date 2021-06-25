package logic.gameObjects.units.enemies

import logic.gameObjects.gameObject.GameObject
import logic.gameObjects.logic.Phasable

interface Enemy : Phasable {
    var target: GameObject?
}