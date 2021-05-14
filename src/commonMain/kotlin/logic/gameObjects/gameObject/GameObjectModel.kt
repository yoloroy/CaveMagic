package logic.gameObjects.gameObject

import com.soywiz.korio.async.ObservableProperty

open class GameObjectModel(
    healthLimit: Int,
    actionPointsLimit: Int,
    health: Int = healthLimit
) {
    val health = ObservableProperty(health)
    val actionPointsLimit = ObservableProperty(actionPointsLimit)
    val healthLimit = ObservableProperty(healthLimit)
}
