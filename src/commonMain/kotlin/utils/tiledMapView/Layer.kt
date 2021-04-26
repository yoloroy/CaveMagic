package utils.tiledMapView

import com.soywiz.korge.view.Container

enum class Layer {
    Floor {
        override val index = 0
    },
    Walls {
        override val index = 1
    },
    BottomDecorations {
        override val index = 2
    },
    TopDecorations {
        override val index = 3
    },
    Events {
        override val index = 4
    },
    Storages {
        override val index = 5
    },
    GameObjects {
        override val index = 6
    },
    MasonGameObjects {
        override val index = 7
    };

    abstract val index: Int
}

operator fun List<Int>.get(layer: Layer) = get(layer.index)

operator fun Container.get(layer: Layer) = get(layer.index)
