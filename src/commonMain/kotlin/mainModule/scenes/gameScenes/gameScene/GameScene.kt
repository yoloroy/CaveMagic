package mainModule.scenes.gameScenes.gameScene

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.tiled.TiledMapView
import com.soywiz.korge.tiled.tiledMapView
import com.soywiz.korge.view.*
import com.soywiz.korge.view.tiles.TileMap
import com.soywiz.korio.async.ObservableProperty
import com.soywiz.korma.geom.Point
import ktres.TILE_ATTACK_CURSOR
import ktres.TILE_CURSOR
import ktres.TILE_LIGHTNING_CAST_CURSOR
import lib.algorythms.recognazingFigure.figures.AreaFigure
import lib.algorythms.recognazingFigure.figures.Figure
import lib.algorythms.recognazingFigure.figures.SymbolFigure
import lib.extensions.clamp
import lib.extensions.plus
import lib.tiledMapView.Layer
import lib.tiledMapView.getTilesArea
import logic.gameObjects.gameObject.GameObject
import logic.gameObjects.logic.Phasable
import logic.gameObjects.player.ActionType
import logic.gameObjects.player.Player
import logic.magic.AreaMagic
import logic.magic.DamageMagic
import logic.magic.Magic
import logic.magic.magic
import mainModule.scenes.abstracts.AssetsManager

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

    val events = mutableListOf<GameScene.() -> Unit>()

    private val turns = mutableListOf<() -> Unit>()

    private var currentPhase = TurnPhase.First
    internal val oCurrentPhase = ObservableProperty(::currentPhase)

    internal var actionType: ActionType = ActionType.Nothing
        set(value) {
            figureRecognitionComponent.unableObserving()
            player.isAddingMoveEnabled = false
            cursorTileId = TILE_CURSOR

            when (value) {
                ActionType.MagicDrawing -> {
                    figureRecognitionComponent.enableObserving()
                }
                ActionType.Move -> {
                    player.isAddingMoveEnabled = true
                }
                ActionType.Attack -> {
                    cursorTileId = TILE_ATTACK_CURSOR
                }
                else -> {}
            }

            field = value
        }

    internal var cursorTileId = TILE_CURSOR

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

        initGameContent()
        initTeleports()
    }

    private fun initMapActions() {
        initMapActions(this)
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

    internal open fun initGameContent() {
        initGameObjects(this) {}
    }

    private fun Container.initUI() {
        initUI(this@GameScene)
    }

    internal suspend fun makeTurn() {
        actionType = ActionType.Nothing

        if (oCurrentPhase.value == TurnPhase.First) {
            player.makeTurn()
            gameObjects.forEach {
                if (it.isAlive && it is Phasable) {
                    it.calculateTurn()
                }
            }
        } else {
            gameObjects.forEach {
                if (it.isAlive) {
                    it.makeTurn()
                }
            }
        }

        checkEvents()
        checkTeleports()

        if (turns.size > 0)
            turns.removeFirst()()

        oCurrentPhase.apply {
            value = value.opposite
        }
    }

    private fun checkEvents() = events.forEach { it() }

    private suspend fun checkTeleports() {
        suspend fun List<GameObject>.checkTeleport(from: Point, destination: Point, id: Int) =
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

enum class TurnPhase(val text: String) {
    First("First phase"),
    Second("Second Phase");

    override fun toString() = text

    val opposite get() = when(this) {
        First -> Second
        Second -> First
    }
}
