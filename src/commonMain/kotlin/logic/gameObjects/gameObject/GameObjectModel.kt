package logic.gameObjects.gameObject

import com.soywiz.korio.async.ObservableProperty

open class GameObjectModel(
    healthLimit: Int,
    actionPointsLimit: Int,
    health: Int = healthLimit
) {
    var health = ObservableProperty(health)
    var actionPointsLimit = ObservableProperty(actionPointsLimit)
    var healthLimit = ObservableProperty(healthLimit)
}
