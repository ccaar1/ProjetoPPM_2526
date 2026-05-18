package konane

import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.Scene
import scalafx.scene.layout.{BorderPane, GridPane, VBox, HBox, StackPane, Priority, Region}
import scalafx.scene.shape.Circle
import scalafx.scene.paint.Color
import scalafx.scene.control.{Button, Label, ComboBox, Separator}
import scalafx.scene.text.{Font, FontWeight}
import scalafx.geometry.{Insets, Pos}
import scalafx.Includes.*
import scalafx.animation.{Timeline, KeyFrame}
import scalafx.event.ActionEvent
import scalafx.util.Duration
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer

// T8 GUI fixo a 6x6
object KonaneGUI extends JFXApp3:

  private val ROWS = 6
  private val COLS = 6
  private val CELL_SIZE = 80.0
  private val SAVE_FILE = "konane_save.txt"

  // oop
  case class UIState(
    selected: Option[Coord2D] = None,
    destinations: List[Coord2D] = Nil,
    chainPos: Option[Coord2D] = None,
    captureCount: Int = 0,
    preChainState: Option[GameState] = None,
    turnStart: Long = 0L
  ):
    def isChaining: Boolean = chainPos.isDefined
    def reset(time: Long): UIState = UIState(turnStart = time)

  // estados mutaveis
  private var gameState: GameState = _
  private var ui: UIState = UIState()

  // propridades da interface
  private val statusText = StringProperty("Bem-vindo ao Konane!")
  private val playerText = StringProperty("Jogador: Black")
  private val timerText = StringProperty("")

  private var cells: Array[Array[StackPane]] = _
  private var timer: Option[Timeline] = None

  override def start(): Unit =
    gameState = GameState.newGame(ROWS, COLS, 30, 1)
    ui = UIState(turnStart = System.currentTimeMillis())

    stage = new JFXApp3.PrimaryStage:
      title = "Konane"
      resizable = false
      scene = new Scene(COLS * CELL_SIZE + 240, ROWS * CELL_SIZE + 120):
        val mainLayout = new BorderPane():
          padding = Insets(10)
          style = "-fx-background-color: #fde4c6;"

        val grid = createBoard()
        mainLayout.center = grid
        BorderPane.setMargin(grid, Insets(10))

        val sidebar = createSidebar()
        mainLayout.right = sidebar
        BorderPane.setMargin(sidebar, Insets(10))

        mainLayout.top = createHeader()
        root = mainLayout

    updateBoard()
    startTimer()

  private def createHeader(): HBox =
    val titleLabel = new Label("KONANE"):
      font = Font.font("Calibri", FontWeight.Bold, 24)
      textFill = Color.Black
    val statusLabel = new Label():
      text <== statusText
      font = Font.font("Calibri", 14)
      style = "-fx-font-weight: bold;"
      textFill = Color.Black
    val spacer = new Region()
    HBox.setHgrow(spacer, Priority.Always)
    new HBox(10):
      alignment = Pos.CenterLeft
      padding = Insets(5, 10, 5, 10)
      children = Seq(titleLabel, spacer, statusLabel)

  private def createBoard(): GridPane =
    val grid = new GridPane()
    grid.hgap = 2
    grid.vgap = 2
    grid.padding = Insets(5)
    grid.style = "-fx-background-color: #c5aa8f; -fx-background-radius: 5;"

    cells = Array.ofDim[StackPane](ROWS, COLS)

    for c <- 0 until COLS do
      val label = new Label(('A' + c).toChar.toString):
        font = Font.font("Calibri", FontWeight.Bold, 14)
        textFill = Color.White
        prefWidth = CELL_SIZE
        alignment = Pos.Center
      grid.add(label, c + 1, 0)

    for r <- 0 until ROWS do
      val rowLabel = new Label(r.toString):
        font = Font.font("Calibri", FontWeight.Bold, 14)
        textFill = Color.White
        prefWidth = 20
        alignment = Pos.Center
      grid.add(rowLabel, 0, r + 1)

      for c <- 0 until COLS do
        val cell = createCell(r, c)
        cells(r)(c) = cell
        grid.add(cell, c + 1, r + 1)

    grid

  private def createCell(row: Int, col: Int): StackPane =
    new StackPane():
      prefWidth = CELL_SIZE
      prefHeight = CELL_SIZE
      style = "-fx-background-color: #e7ddd2; -fx-border-color: #8B4513; -fx-border-width: 1;"
      onMouseClicked = _ => handleCellClick(row, col)

  private def createSidebar(): VBox =
    val playerLabel = new Label():
      text <== playerText
      font = Font.font("Calibri", FontWeight.Bold, 16)
      style = "-fx-font-weight: bold;"
      textFill = Color.Black


    val timerLabel = new Label():
      text <== timerText
      font = Font.font("Calibri", 14)
      textFill = Color.web("#e74c3c")

    val timeLabel = new Label("Tempo por jogada:"):
      style = "-fx-font-weight: bold;"
      font = Font.font("Calibri", 12)
      textFill = Color.Black

    val timeCombo = new ComboBox[String](ObservableBuffer("Sem limite", "15s", "30s", "60s", "120s")):
      value = "30s"
      prefWidth = 120
      style = "-fx-background-color: #f9f6f3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12;-fx-border-radius: 12;-fx-cursor: hand;"
      onAction = _ =>
        val t = selectionModel().selectedIndex() match
          case 0 => 0L
          case 1 => 15L
          case 2 => 30L
          case 3 => 60L
          case _ => 120L
        gameState = gameState.copy(maxMoveTime = t)
        ui = ui.copy(turnStart = System.currentTimeMillis())
        statusText.value = s"Tempo: ${if t == 0 then "sem limite" else s"${t}s"}"

    val diffLabel = new Label("Dificuldade:"):
      style = "-fx-font-weight: bold;"
      font = Font.font("Calibri", 12)
      textFill = Color.Black

    val diffCombo = new ComboBox[String](ObservableBuffer("Facil", "Medio", "Dificil")):
      value = "Facil"
      prefWidth = 120
      style = "-fx-background-color: #f9f6f3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12;-fx-border-radius: 12;-fx-cursor: hand;"
      onAction = _ =>
        val d = selectionModel().selectedIndex() + 1
        gameState = gameState.copy(difficulty = d)
        statusText.value = s"Dificuldade: ${value.value}"

    val newGameBtn = new Button("Novo Jogo"):
      prefWidth = 120
      style = "-fx-background-color: #f4bcb0; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12;-fx-border-radius: 12;-fx-cursor: hand;"
      onAction = _ => newGame()

    val undoBtn = new Button("Voltar atrás"):
      prefWidth = 120
      style = "-fx-background-color: #e1abb5; -fx-text-fill: white; -fx-font-weight: bold;-fx-background-radius: 12;-fx-border-radius: 12;-fx-cursor: hand;"
      onAction = _ => performUndo()

    val saveBtn = new Button("Guardar"):
      prefWidth = 120
      style = "-fx-background-color: #c58f8f; -fx-text-fill: white; -fx-font-weight: bold;-fx-background-radius: 12;-fx-border-radius: 12;-fx-cursor: hand;"
      onAction = _ =>
        if GameState.save(gameState, SAVE_FILE) then
          statusText.value = "Jogo guardado."
        else
          statusText.value = "Erro ao guardar."

    val loadBtn = new Button("Carregar"):
      prefWidth = 120
      style = "-fx-background-color: #9d7272; -fx-text-fill: white; -fx-font-weight: bold;-fx-background-radius: 12;-fx-border-radius: 12;-fx-cursor: hand;"
      onAction = _ =>
        GameState.load(SAVE_FILE) match
          case Some(state) =>
            gameState = state
            ui = UIState(turnStart = System.currentTimeMillis())
            updateBoard()
            statusText.value = "Jogo carregado."
          case None =>
            statusText.value = "Nenhum jogo guardado."

    //T7 Parar captura: Caso tenhamos a oportunidade de puder fazer varias jogadas de capturas, o botao para as possibilidades de captura e passa para o proximo
    val stopCaptureBtn = new Button("Parar Captura"):
      prefWidth = 120
      style = "-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold;-fx-background-radius: 12;-fx-border-radius: 12;-fx-cursor: hand;"
      onAction = _ =>
        if ui.isChaining && ui.captureCount > 0 then //se tivermos em cadeia e ja tivermos feito pelo menos uma jogada, entao podemos parar
          finishChainCapture()

    new VBox(8):
      padding = Insets(10)
      prefWidth = 200
      alignment = Pos.TopCenter
      style = "-fx-background-color: #fbd2b9; -fx-background-radius: 5;"
      children = Seq(
        playerLabel, timerLabel, new Separator(),
        timeLabel, timeCombo, diffLabel, diffCombo, new Separator(),
        newGameBtn, undoBtn, saveBtn, loadBtn, new Separator(),
        stopCaptureBtn
      )

  private def newGame(): Unit =
    gameState = GameState.newGame(ROWS, COLS, gameState.maxMoveTime, gameState.difficulty)
    ui = ui.reset(System.currentTimeMillis())
    updateBoard()
    statusText.value = "Novo jogo iniciado!"

  private def performUndo(): Unit =
    if ui.isChaining then
      ui.preChainState match
        case Some(prev) =>
          gameState = prev
          ui = ui.reset(System.currentTimeMillis())
          updateBoard()
          statusText.value = "Captura em cadeia cancelada."
        case None => ()
    else
      GameState.undo(gameState) match
        case Some(prev) =>
          gameState = prev
          ui = ui.reset(System.currentTimeMillis())
          updateBoard()
          statusText.value = "Undo realizado."
        case None =>
          statusText.value = "Nada para desfazer."

  private def isTimeExpired: Boolean =
    gameState.maxMoveTime > 0 &&
      (System.currentTimeMillis() - ui.turnStart) / 1000 >= gameState.maxMoveTime

  private def handleCellClick(row: Int, col: Int): Unit =
    val coord: Coord2D = (row, col)

    if gameState.currentPlayer != Stone.Black then
      statusText.value = "Aguarde o computador..."
    else if Game.hasLost(gameState.board, gameState.currentPlayer, ROWS, COLS) then
      statusText.value = s"${gameState.currentPlayer.opponent.display} ganhou!"
    else if isTimeExpired then
      statusText.value = "Tempo esgotado! Perdeu a jogada."
      gameState = gameState.copy(currentPlayer = Stone.White)
      computerTurn()
    else if ui.isChaining then
      handleChainClick(coord)
    else
      handleNormalClick(coord)

  private def handleChainClick(coord: Coord2D): Unit =
    ui.chainPos match
      case Some(pos) if ui.destinations.contains(coord) =>
        val (newBoard, newOpen) = Game.executeCapture(gameState.board, pos, coord, gameState.lstOpenCoords)
        gameState = gameState.copy(board = newBoard, lstOpenCoords = newOpen)
        val newCount = ui.captureCount + 1

        val moreJumps = Game.getSingleJumps(gameState.board, Stone.Black, coord, ROWS, COLS)
        if moreJumps.isEmpty then
          ui = ui.copy(captureCount = newCount)
          finishChainCapture()
        else
          ui = ui.copy(chainPos = Some(coord), captureCount = newCount, destinations = moreJumps)
          updateBoard()
          statusText.value = s"Captura $newCount! Clique destino ou 'Parar Captura'"
      case Some(_) =>
        statusText.value = "Destino invalido. Clique num destino destacado."
      case None => ()

  private def handleNormalClick(coord: Coord2D): Unit =
    val movablePieces = Game.getMovablePieces(gameState.board, Stone.Black, ROWS, COLS)

    if gameState.board.seq.get(coord).contains(Stone.Black) && movablePieces.contains(coord) then
      val jumps = Game.getSingleJumps(gameState.board, Stone.Black, coord, ROWS, COLS)
      ui = ui.copy(selected = Some(coord), destinations = jumps)
      updateBoard()
      statusText.value = s"Peca selecionada: ${formatCoord(coord)}. Clique no destino."
    else if ui.selected.isDefined && ui.destinations.contains(coord) then
      val from = ui.selected.get
      val savedState = GameState.pushHistory(gameState)

      val (newBoard, newOpen) = Game.executeCapture(gameState.board, from, coord, gameState.lstOpenCoords)
      gameState = gameState.copy(board = newBoard, lstOpenCoords = newOpen)

      val moreJumps = Game.getSingleJumps(gameState.board, Stone.Black, coord, ROWS, COLS)
      if moreJumps.isEmpty then
        ui = ui.copy(selected = None, captureCount = 1, preChainState = Some(savedState))
        finishChainCapture()
      else
        ui = ui.copy(
          selected = None, chainPos = Some(coord), captureCount = 1,
          destinations = moreJumps, preChainState = Some(savedState)
        )
        updateBoard()
        statusText.value = s"Captura! Pode continuar ou clicar 'Parar Captura'"
    else
      ui = ui.copy(selected = None, destinations = Nil)
      updateBoard()
      if movablePieces.nonEmpty then
        statusText.value = "Selecione uma peca (destacada em verde)."
      else
        statusText.value = s"Sem jogadas! ${Stone.White.display} ganhou!"

  private def finishChainCapture(): Unit =
    val saveState = ui.preChainState.getOrElse(GameState.pushHistory(gameState))
    gameState = gameState.copy(currentPlayer = Stone.White, history = saveState.history)
    ui = ui.reset(ui.turnStart)
    updateBoard()
    statusText.value = "A vez do computador..."

    val delay = new Timeline:
      keyFrames = Seq(
        KeyFrame(Duration(500), onFinished = (_: ActionEvent) => computerTurn())
      )
    delay.play()

  private def computerTurn(): Unit =
    if Game.hasLost(gameState.board, Stone.White, ROWS, COLS) then
      statusText.value = "Black GANHOU! Computador sem jogadas."
      playerText.value = "JOGO TERMINADO"
      updateBoard()
    else
      val (resultOpt, newRand, newOpen, moveOpt) =
        Game.computerPlay(gameState.board, Stone.White, ROWS, COLS,
          gameState.lstOpenCoords, gameState.rand, gameState.difficulty)

      resultOpt match
        case Some(newBoard) =>
          val moveStr = moveOpt match
            case Some((from, to)) => s"${formatCoord(from)} -> ${formatCoord(to)}"
            case None => "?"
          gameState = gameState.copy(
            board = newBoard, currentPlayer = Stone.Black,
            lstOpenCoords = newOpen, rand = newRand
          )
          ui = ui.copy(turnStart = System.currentTimeMillis())

          if Game.hasLost(gameState.board, Stone.Black, ROWS, COLS) then
            statusText.value = s"White GANHOU! Computador jogou: $moveStr"
            playerText.value = "JOGO TERMINADO"
          else
            statusText.value = s"Computador jogou: $moveStr. Sua vez!"

          updateBoard()
        case None =>
          statusText.value = "Black GANHOU! Computador sem jogadas."
          playerText.value = "JOGO TERMINADO"
          updateBoard()

  private def updateBoard(): Unit =
    Platform.runLater {
      val movablePieces = if gameState.currentPlayer == Stone.Black && !ui.isChaining then
        Game.getMovablePieces(gameState.board, Stone.Black, ROWS, COLS)
      else Nil

      for r <- 0 until ROWS; c <- 0 until COLS do
        val cell = cells(r)(c)
        val coord: Coord2D = (r, c)
        cell.children.clear()

        val isSelected = ui.selected.contains(coord)
        val isValidDest = ui.destinations.contains(coord)
        val isMovable = movablePieces.contains(coord)
        val isChainPos = ui.chainPos.contains(coord)

        val bgColor = if isSelected || isChainPos then "#fbfdc6"
          else if isValidDest then "#90EE90"
          else if isMovable then "#98FB98"
          else "#e7ddd2"

        cell.style = s"-fx-background-color: $bgColor; -fx-border-color: #c5aa8f; -fx-border-width: 1;"

        gameState.board.seq.get(coord) match
          case Some(Stone.Black) =>
            val circle = new Circle:
              radius = CELL_SIZE * 0.35
              fill = Color.Black
              stroke = Color.web("#333333")
              strokeWidth = 2
            cell.children += circle
          case Some(Stone.White) =>
            val circle = new Circle:
              radius = CELL_SIZE * 0.35
              fill = Color.White
              stroke = Color.web("#999999")
              strokeWidth = 2
            cell.children += circle
          case None =>
            if isValidDest then
              val indicator = new Circle:
                radius = CELL_SIZE * 0.15
                fill = Color.web("#27ae60", 0.6)
              cell.children += indicator

      if !playerText.value.contains("TERMINADO") then
        playerText.value = s"Jogador: ${gameState.currentPlayer.display}"
    }

  private def startTimer(): Unit =
    val t = new Timeline:
      cycleCount = Timeline.Indefinite
      keyFrames = Seq(
        KeyFrame(Duration(1000), onFinished = (_: ActionEvent) => updateTimer())
      )
    t.play()
    timer = Some(t)

  private def updateTimer(): Unit =
    if gameState.maxMoveTime > 0 && gameState.currentPlayer == Stone.Black then
      val elapsed = (System.currentTimeMillis() - ui.turnStart) / 1000
      val remaining = gameState.maxMoveTime - elapsed
      if remaining > 0 then
        timerText.value = s"Tempo: ${remaining}s"
      else
        timerText.value = "TEMPO ESGOTADO!"
        if ui.isChaining && ui.captureCount > 0 then
          finishChainCapture()
        else if !ui.isChaining then
          gameState = gameState.copy(currentPlayer = Stone.White)
          ui = ui.reset(ui.turnStart)
          updateBoard()
          computerTurn()
    else
      timerText.value = if gameState.maxMoveTime == 0 then "" else "..."

  private def formatCoord(c: Coord2D): String = s"${c._1}${('A' + c._2).toChar}"


//style = "-fx-font-weight: bold;" para fazer fonte a negrito
  //style = "-fx-background-radius: 12;-fx-border-radius: 12;" botao arredondado
// -fx-cursor: hand; mudar cursor para mao quando em cima de um botao