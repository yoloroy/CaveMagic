package utils.tiledMapView

import com.soywiz.korge.view.Container

enum class Layer(val index: Int) {
    Floor(0),
    Walls(1),
    BottomDecorations(2),
    TopDecorations(3),
    Events(4),
    Storages(5),
    GameObjects(6),
    MasonGameObjects(7),
    Teleports(8),
    StepsPreview(9);
}

operator fun List<Int>.get(layer: Layer) = get(layer.index)

operator fun Container.get(layer: Layer) = get(layer.index)
