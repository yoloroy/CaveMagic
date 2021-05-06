package mainModule.scenes.tutorial

import com.soywiz.korge.input.*
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.tiled.TiledMapView
import com.soywiz.korge.tiled.tiledMapView
import com.soywiz.korge.ui.uiTextButton
import com.soywiz.korge.view.*
import com.soywiz.korge.view.tiles.TileMap
import com.soywiz.korim.color.Colors.WHITE
import com.soywiz.korma.geom.Point
import exceptions.UnknownUnitException

import mainModule.MainModule
import mainModule.scenes.abstracts.AssetsManager
import logic.gameObjects.GameObjectId
import logic.gameObjects.gameObject.GameObject
import logic.gameObjects.player.Player
import logic.gameObjects.sheep.Sheep
import logic.magic.AreaMagic
import logic.magic.magic
import recognazingFigure.figures.AreaFigure
import recognazingFigure.figures.Figure
import utils.tiledMapView.*
import utils.*

class TutorialScene : Scene(), AssetsManager {
    private val assetsManager = TutorialAssetsManager()

    internal lateinit var tilesManager: MapTilesManager

    private val figureRecognitionComponent = SceneFigureRecognitionComponent(this, onNewFigure)
    private val magicHandler = MagicHandler(this)

    private lateinit var camera: Camera
    private lateinit var map: TiledMapView

    private lateinit var player: Player
    internal val gameObjects = mutableListOf<GameObject>()
    private val teleports = mutableMapOf<Int, Pair<Point, Point>>()

    private val turns = mutableListOf<() -> Unit>()

    override suspend fun Container.sceneInit() {
        loadAssets()

        initComponents()
        initGame()
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
        initUI()
    }

    private fun Container.initMap() {
        fixedSizeContainer(stage!!.width, stage!!.height, clip = true) {
            position(0, 0)
            camera = camera {
                map = tiledMapView(assetsManager.tiledMap, smoothing = false)
                tilesManager = MapTilesManager(map)
            }
        }

        initGameObjects()
        initTeleports()
    }

    private fun initTeleports() {
        val starts = mutableMapOf<Int, Point>()

        tilesManager.forEachObject(Layer.Teleports) { pos, id ->
            if (id !in starts)
                starts[id] = pos
            else
                teleports[id] = starts[id]!! to pos
        }
    }

    private fun initGameObjects() {
        tilesManager.forEachObject(Layer.GameObjects) { pos, id ->
            gameObjects += when (GameObjectId.getTypeById(id)) {
                GameObjectId.Player ->
                    Player(map, camera, tilesManager, pos).also { player = it }
                GameObjectId.Sheep ->
                    Sheep(tilesManager, pos)
                else ->
                    throw UnknownUnitException()
            }
        }
    }

    private fun Container.initUI() {
        uiTextButton {
            size(28, 4)
            position(MainModule.size.size.p * Point.Right.point + sizePoint * Point.Left.point * 1.5)
            textSize = 3.8
            text = "ClickableButton"
            textColor = WHITE

            onUp {
                figureRecognitionComponent.enableObserving()
            }
        }

        image(assetsManager.nextTurnBitmap) {
            smoothing = false
            anchor(0, 0)
            size(8, 8)
            position(10,0)

            onUp {
                makeTurn()
            }
        }
    }

    private fun makeTurn() {
        gameObjects.forEach {
            it.makeTurn()
        }

        teleports.forEach { (id, teleportPoints) ->
            gameObjects.run checkTeleport@{
                filter { it.pos == teleportPoints.first }.onNotEmpty {
                    forEach {
                        it.teleportTo(teleportPoints.second, id)
                    }

                    return@checkTeleport
                }
                filter { it.pos == teleportPoints.second }.onNotEmpty {
                    forEach {
                        it.teleportTo(teleportPoints.first, id)
                    }

                    return@checkTeleport
                }
            }
        }

        if (turns.size > 0)
            turns.removeFirst()()
    }

    private val onNewFigure get() = { figure: Figure ->
        when (figure) {
            is AreaFigure -> {
                val square = map.getTilesArea(figure.area) + player.pos
                square.start.clamp(Point(0)..(map[0] as TileMap).intMap.run { Point(width, height) })

                magicHandler.onAreaMagic(figure.magic as AreaMagic, square)
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
