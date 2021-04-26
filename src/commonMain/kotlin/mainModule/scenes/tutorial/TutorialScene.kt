package mainModule.scenes.tutorial

import com.soywiz.klock.seconds
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.tiled.TiledMapView
import com.soywiz.korge.tiled.tiledMapView
import com.soywiz.korge.ui.uiTextButton
import com.soywiz.korge.view.*
import com.soywiz.korge.view.tiles.TileMap
import com.soywiz.korim.color.Colors.WHITE
import com.soywiz.korma.geom.Point
import gameObjects.GameObjectsIds
import mainModule.MainModule
import mainModule.scenes.abstracts.AssetsManager
import gameObjects.player.Player
import gameObjects.sheep.Sheep
import magic.AreaMagic
import magic.getMagic
import utils.*
import recognazingFigure.figures.AreaFigure
import recognazingFigure.figures.Figure
import utils.tiledMapView.*

class TutorialScene : Scene(), AssetsManager {
    private val assetsManager = TutorialAssetsManager()

    internal lateinit var tilesManager: MapTilesManager

    private val figureRecognitionComponent = SceneFigureRecognitionComponent(this, onNewFigure)
    private val magicHandler = MagicHandler(this)

    private lateinit var camera: Camera
    private lateinit var map: TiledMapView

    private lateinit var player: Player
    internal val sheeps = mutableListOf<Sheep>()

    private val turns = mutableListOf<() -> Unit>()

    override suspend fun Container.sceneInit() {
        loadAssets()

        initComponents()
        initGame()
        initUpdaters()
    }

    override suspend fun loadAssets() {
        assetsManager.loadAssets()
        Figure.loadAssets()
    }

    private fun Container.initComponents() {
        figureRecognitionComponent.initFigureDrawing(this)
    }

    private fun Container.initGame() {
        scale(scale)

        initMap()
        initGameObjects()
        initUI()
    }

    private fun Container.initGameObjects() {
        tilesManager.forEachObject(Layer.GameObjects) { pos, id ->
            when(GameObjectsIds.getTypeById(id)) {
                GameObjectsIds.Player ->
                    player = Player(stage!!, map, assetsManager.playerBitmap, camera, tilesManager, pos)
                GameObjectsIds.Sheep ->
                    sheeps += Sheep(stage!!, map, assetsManager.sheepBitmap, pos)
                else -> Unit
            }
        }
    }

    private fun Container.initUI() {
        uiTextButton {
            size(28, 4)
            position(MainModule.size.size.p * Point.Right.point - sizePoint * (Point.Right).point)
            textSize = 3.8
            text = "ClickableButton"
            textColor = WHITE

            onUp { figureRecognitionComponent.enableObserving() }
        }
    }

    private fun Container.initMap() {
        fixedSizeContainer(stage!!.width, stage!!.height, clip = true) {
            position(0, 0)
            camera = camera {
                map = tiledMapView(assetsManager.tiledMap, smoothing = false)
                tilesManager = MapTilesManager(map)
            }
        }
    }

    private fun Container.initUpdaters() {
        addFixedUpdater(1.seconds) {
            player.makeTurn()
            sheeps.forEach { it.makeTurn() }
            if (turns.size > 0)
                turns.removeFirst()()
        }
    }

    private val onNewFigure get() = { figure: Figure ->
        when (figure) {
            is AreaFigure -> {
                val square = map.getTilesArea(figure.area) + player.pos
                square.start.clamp(Point(0)..(map[0] as TileMap).intMap.run { Point(width, height) })

                magicHandler.onAreaMagic(figure.getMagic() as AreaMagic, square)
            }
            else -> Unit
        }
    }

    internal fun addWork(work: List<() -> Unit>, from: Int = 0) {
        if (turns.size <= from + work.size)
            repeat(from + work.size - turns.size) {
                turns += {}
            }

        for (i in work.indices) {
            println(i)
            val previousWork = turns[i + from]
            val newWork = work[i]

            turns[i + from] = { previousWork(); newWork() }
        }
    }
}
