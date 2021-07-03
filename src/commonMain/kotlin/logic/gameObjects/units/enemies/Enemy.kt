package logic.gameObjects.units.enemies

import logic.gameObjects.gameObject.GameObject
import logic.gameObjects.logic.TurnCalculator

interface Enemy : TurnCalculator {
    var target: GameObject?
}
