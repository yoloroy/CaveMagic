package mainModule.scenes.tutorial

import com.soywiz.korev.MouseButton
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.tiled.TiledMapView
import com.soywiz.korge.tiled.tiledMapView
import com.soywiz.korge.view.*
import com.soywiz.korge.view.tiles.TileMap
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.int
import exceptions.UnknownUnitException

import mainModule.MainModule
import mainModule.scenes.abstracts.AssetsManager
import logic.gameObjects.gameObject.GameObjectId
import logic.gameObjects.gameObject.GameObject
import logic.gameObjects.player.ActionType
import logic.gameObjects.player.Player
import logic.gameObjects.sheep.Sheep
import logic.magic.AreaMagic
import logic.magic.magic
import algorythms.pathFinding.getPath
import algorythms.recognazingFigure.figures.AreaFigure
import algorythms.recognazingFigure.figures.Figure
import utils.tiledMapView.*
import utils.*
import widgets.valueBar

@Suppress("FunctionName")
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

    private var actionType: ActionType = ActionType.Nothing
        set(value) {
            figureRecognitionComponent.unableObserving()
            player.isAddingMoveEnabled = false
            cursorTileId = MapTilesManager.TILE_CURSOR

            when (value) {
                ActionType.Magic -> {
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

    private var cursorTileId = MapTilesManager.TILE_CURSOR

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

    private fun initMapActions() {
        // TODO: move to GameMapComponent
        val previewPath = mutableListOf<Pair<Point, Point>>()
        val lastCursorPos = Point(0)
        map.onMove {
            val pos = (it.currentPosLocal / tilesManager.tileSize).int.p
            tilesManager[lastCursorPos.xi, lastCursorPos.yi, Layer.Cursor] = MapTilesManager.EMPTY

            if (actionType == ActionType.Move) {
                previewPath
                    .forEach { (_, pathPoint) ->
                        tilesManager[pathPoint.xi, pathPoint.yi, Layer.Cursor] = MapTilesManager.EMPTY
                    }

                previewPath.clear()
                previewPath.addAll(
                    getPath(
                        player.lastPreviewPos,
                        pos,
                        tilesManager[Layer.Walls]
                    ).take(player.remainingActionPoints)
                )

                previewPath
                    .forEach { (_, pathPoint) ->
                        tilesManager[pathPoint.xi, pathPoint.yi, Layer.Cursor] = MapTilesManager.TILE_MOVE_CURSOR
                    }
            }

            tilesManager[pos.xi, pos.yi, Layer.Cursor] =
                if (actionType != ActionType.Attack || (pos - player.pos).length == 1.0)
                    cursorTileId
                else
                    MapTilesManager.TILE_CURSOR
            lastCursorPos.setTo(pos)
        }

        map.onClick {
            if (player.isAddingMoveEnabled && it.button == MouseButton.RIGHT) {// TODO: refactor
                player.actions += getPath(
                    player.lastPreviewPos,
                    (it.currentPosLocal / tilesManager.tileSize).int.p,
                    tilesManager[Layer.Walls]
                )
                    .take(player.remainingActionPoints)
                    .also { path ->
                        player.lastPreviewPos.setTo(path.last().second)
                        player.showPath(path)
                    }
                    .map { pathPart -> ActionType.Move to pathPart }
            }
        }
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
                    Player(map, camera, tilesManager, pos, actionType == ActionType.Move).also { player = it }
                GameObjectId.Sheep ->
                    Sheep(tilesManager, pos)
                else ->
                    throw UnknownUnitException()
            }
        }
    }

    private fun Container.initUI() {
        fun Image.flip() {
            scale(-scaleX, -scaleY)
            anchor(1 - anchorX, 1 - anchorY)
        }

        image(assetsManager.nextTurnBitmap) {
            smoothing = false
            anchor(2, 1)
            size(16, 16)
            position(MainModule.size.size.p - Point(4) + Point(.0, 0.5))

            onDown { flip() }
            onUp {
                flip()
                makeTurn()
            }
        }

        val bottomCenter = MainModule.size.size.p + MainModule.size.size.p * Point.Left.point / 2
        val buttonAttack = image(assetsManager.buttonAttackBitmap) {
            smoothing = false
            anchor(1, 1)
            size(16, 16)
            position(bottomCenter + Point.Down.point * 2 + Point.Left.point * 1)

            onDown { flip() }
            onUp {
                flip()
                actionType = ActionType.Attack
            }
        }
        val buttonMisc = image(assetsManager.buttonMiscBitmap) {
            smoothing = false
            anchor(0, 1)
            size(16, 16)
            position(bottomCenter + Point.Down.point * 2 + Point.Right.point * 1)

            onDown { flip() }
            onUp { flip() }
        }
        image(assetsManager.buttonMoveBitmap) {
            smoothing = false
            anchor(1, 1)
            size(16, 16)
            setPositionRelativeTo(buttonAttack, -scaledSize * Point.Horizontal + Point.Left.point * 2)

            onDown { flip() }
            onUp {
                flip()
                actionType = ActionType.Move
            }
        }
        image(assetsManager.buttonMagicBitmap) {
            smoothing = false
            anchor(0, 1)
            size(16, 16)
            setPositionRelativeTo(buttonMisc, +scaledSize * Point.Horizontal + Point.Right.point * 2)

            onDown { flip() }
            onUp {
                flip()
                actionType = ActionType.Magic
            }
        }

        val healthBar = valueBar(player.model.healthLimit.value, player.model.health.value)
        player.model.apply {
            health.observe {
                healthBar.value = it
            }
            healthLimit.observe {
                healthBar.limit = it
            }
        }
    }

    private fun makeTurn() {
        actionType = ActionType.Nothing

        gameObjects.forEach {
            it.makeTurn()
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
