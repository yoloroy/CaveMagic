package mainModule.scenes.gameScenes.gameScene

import lib.algorythms.recognazingFigure.figures.AreaFigure
import lib.algorythms.recognazingFigure.figures.Figure
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.tiled.TiledMapView
import com.soywiz.korge.tiled.tiledMapView
import com.soywiz.korge.view.*
import com.soywiz.korge.view.tiles.TileMap
import com.soywiz.korma.geom.Point
import lib.algorythms.recognazingFigure.figures.SymbolFigure
import logic.gameObjects.gameObject.GameObject
import logic.gameObjects.player.ActionType
import logic.gameObjects.player.Player
import logic.magic.AreaMagic
import logic.magic.magic
import mainModule.scenes.abstracts.AssetsManager
import lib.extensions.clamp
import lib.extensions.plus
import lib.tiledMapView.Layer
import lib.tiledMapView.getTilesArea
import logic.magic.DamageMagic
import logic.magic.Magic
import mainModule.scenes.gameScenes.gameScene.MapTilesManager.Companion.TILE_LIGHTNING_CAST_CURSOR

@Suppress("LeakingThis")
open class GameScene(tiledMapPath: String) : Scene(), AssetsManager {
    internal val assetsManager = GameSceneAssetsManager(tiledMapPath)

    internal lateinit var tilesManager: MapTilesManager

    private val figureRecognitionComponent = SceneFigureRecognitionComponent(this, onNewFigure)
    private val magicHandler = MagicHandler(this)

    internal lateinit var camera: Camera
    internal lateinit var map: TiledMapView

    internal lateinit var player: Player
    internal val gameObjects = mutableListOf<GameObject>()
    private val teleports = mutableMapOf<Int, Pair<Point, Point>>()

    private val turns = mutableListOf<() -> Unit>()

    internal var actionType: ActionType = ActionType.Nothing
        set(value) {
            figureRecognitionComponent.unableObserving()
            player.isAddingMoveEnabled = false
            cursorTileId = MapTilesManager.TILE_CURSOR

            when (value) {
                ActionType.MagicDrawing -> {
                    figureRecognitionComponent.enableObserving()
                }
                ActionType.Move -> {
                    player.isAddingMoveEnabled = true
                }
                ActionType.Attack -> {
                    cursorTileId = MapTilesManager.TILE_ATTACK_CURSOR
                }
                else -> {}
            }

            field = value
        }

    internal var cursorTileId = MapTilesManager.TILE_CURSOR

    internal var savedMagicSymbol: Magic? = null

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

    private fun Container.initGame(){
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

        initMapActions()

        initGameObjects()
        initTeleports()
    }

    private fun initMapActions() = initMapActions(this)

    private fun initTeleports() {
        val starts = mutableMapOf<Int, Point>()

        tilesManager.forEachObject(Layer.Teleports) { pos, id ->
            if (id !in starts)
                starts[id] = pos
            else
                teleports[id] = starts[id]!! to pos
        }
    }

    private fun initGameObjects() = initGameObjects(this)

    private fun Container.initUI() = initUI(this@GameScene)

    internal fun makeTurn() {
        actionType = ActionType.Nothing

        gameObjects.forEach {
            if (it.isAlive) {
                it.makeTurn()
            }
        }

        checkTeleports()

        if (turns.size > 0)
            turns.removeFirst()()
    }

    private fun checkTeleports() {
        fun List<GameObject>.checkTeleport(from: Point, destination: Point, id: Int) =
            filter { it.pos == from }
                .forEach { it.teleportTo(destination, id) }

        teleports.forEach { (id, teleportPoints) ->
            gameObjects.run {
                checkTeleport(teleportPoints.first, teleportPoints.second, id)
                checkTeleport(teleportPoints.second, teleportPoints.first, id)
            }
        }
    }

    private val onNewFigure get() = { figure: Figure ->
        when (figure) {
            is AreaFigure -> { // TODO
                val square = map.getTilesArea(figure.area) + player.pos
                square.start.clamp(Point(0)..(map[0] as TileMap).intMap.run { Point(width, height) })

                magicHandler.onAreaMagic(figure.magic as AreaMagic, square)
            }
            is SymbolFigure -> {
                savedMagicSymbol = figure.magic
                cursorTileId = when (savedMagicSymbol) {
                    DamageMagic.Lightning -> TILE_LIGHTNING_CAST_CURSOR
                    else -> throw Error("Unknown magic")
                }
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
