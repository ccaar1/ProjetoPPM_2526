package konane

import javafx.animation.KeyFrame
import javafx.animation.Timeline

import javafx.fxml.{FXML, Initializable}

import javafx.scene.control.*
import javafx.scene.layout.{GridPane, StackPane}

import javafx.scene.paint.Color
import javafx.scene.shape.Circle

import javafx.util.Duration

import java.net.URL
import java.util.ResourceBundle

import scala.jdk.CollectionConverters.*

class KonaneController extends Initializable:

  //constantes

  private val ROWS = 6
  private val COLS = 6

  private val CELL_SIZE = 80.0

  private val SAVE_FILE =
    "konane_save.txt"

  //componentes fxml

  @FXML var boardGrid: GridPane = _

  @FXML var titleLabel: Label = _

  @FXML var statusLabel: Label = _

  @FXML var playerLabel: Label = _

  @FXML var timerLabel: Label = _

  @FXML var timeCombo: ComboBox[String] = _

  @FXML var diffCombo: ComboBox[String] = _

  @FXML var newGameBtn: Button = _

  @FXML var undoBtn: Button = _

  @FXML var saveBtn: Button = _

  @FXML var loadBtn: Button = _

  @FXML var stopCaptureBtn: Button = _

  @FXML var hintBtn: Button = _

  //estado da interface

  case class UIState(
                      selected: Option[Coord2D] = None,
                      destinations: List[Coord2D] = Nil,
                      chainPos: Option[Coord2D] = None,
                      captureCount: Int = 0,
                      preChainState: Option[GameState] = None
                    ):
    def isChaining: Boolean =
      chainPos.isDefined

  //estado do jogo

  private var gameState: GameState = _

  private var ui: UIState =
    UIState()

  private var cells:
    Array[Array[StackPane]] = _

  //timer

  private var moveTimeLeft = 30

  private var timer: Timeline = _

  //inicializacao

  override def initialize(
                           url: URL,
                           rb: ResourceBundle
                         ): Unit =

    gameState =
      GameState.newGame(
        ROWS,
        COLS,
        30,
        1
      )

    setupCombos()

    setupButtons()

    createBoard()

    updateBoard()

    updateTimerLabel()

    startTimer()

    statusLabel.setText(
      "Bem-vindo ao Konane!"
    )

  //configuracao das comboboxes

  private def setupCombos(): Unit =

    timeCombo.getItems.addAll(
      List(
        "Sem limite",
        "15s",
        "30s",
        "60s",
        "120s"
      ).asJava
    )

    timeCombo.setValue("30s")

    timeCombo.setOnAction(_ =>

      moveTimeLeft =
        timeCombo.getValue match

          case "15s" => 15

          case "30s" => 30

          case "60s" => 60

          case "120s" => 120

          case _ => 999999

      updateTimerLabel()
    )

    diffCombo.getItems.addAll(
      List(
        "Facil",
        "Medio",
        "Dificil"
      ).asJava
    )

    diffCombo.setValue("Facil")

    // Aplica a dificuldade escolhida ao estado do jogo
    diffCombo.setOnAction(_ =>
      val diff = diffCombo.getValue match
        case "Medio"  => 2
        case "Dificil" => 3
        case _        => 1
      gameState = gameState.copy(difficulty = diff)
    )

  //configuracao dos botoes

  private def setupButtons(): Unit =

    newGameBtn.setOnAction(_ =>
      newGame()
    )

    undoBtn.setOnAction(_ =>
      performUndo()
    )

    saveBtn.setOnAction(_ =>

      if GameState.save(
        gameState,
        SAVE_FILE
      ) then

        statusLabel.setText(
          "Jogo guardado."
        )

      else

        statusLabel.setText(
          "Erro ao guardar."
        )
    )

    loadBtn.setOnAction(_ =>

      GameState.load(
        SAVE_FILE
      ) match

        case Some(state) =>

          gameState = state

          ui = UIState()

          updateBoard()

          statusLabel.setText(
            "Jogo carregado."
          )

        case None =>

          statusLabel.setText(
            "Nenhum jogo guardado."
          )
    )

    stopCaptureBtn.setOnAction(_ =>

      if ui.isChaining
        && ui.captureCount > 0
      then

        finishChainCapture()
    )

    // "Jogar por mim" — o computador escolhe a melhor jogada para jogador
    hintBtn.setOnAction(_ => playForHuman())

  //criacao do tabuleiro

  private def createBoard(): Unit =

    cells =
      Array.ofDim[StackPane](
        ROWS,
        COLS
      )

    for r <- 0 until ROWS do
      for c <- 0 until COLS do

        val cell =
          new StackPane()

        cell.setPrefSize(
          CELL_SIZE,
          CELL_SIZE
        )

        cell.setStyle(

          "-fx-background-color: #e7ddd2;" +
            "-fx-border-color: #8B4513;" +
            "-fx-border-width: 1;"
        )

        cell.setOnMouseClicked(_ =>
          handleCellClick(r, c)
        )

        cells(r)(c) = cell

        boardGrid.add(cell, c, r)

  //novo jogo

  private def newGame(): Unit =

    gameState =
      GameState.newGame(
        ROWS,
        COLS,
        moveTimeLeft,
        gameState.difficulty
      )

    ui = UIState()

    updateBoard()

    statusLabel.setText(
      "Novo jogo iniciado!"
    )

  //undo

  private def performUndo(): Unit =

    GameState.undo(
      gameState
    ) match

      case Some(prev) =>

        gameState = prev

        ui = UIState()

        updateBoard()

        statusLabel.setText(
          "Undo realizado."
        )

      case None =>

        statusLabel.setText(
          "Nada para desfazer."
        )

  //clique numa celula

  private def handleCellClick(
                               row: Int,
                               col: Int
                             ): Unit =

    val coord: Coord2D =
      (row, col)

    if gameState.currentPlayer
      != Stone.Black
    then

      statusLabel.setText(
        "Aguarde o computador..."
      )

    else

      handleNormalClick(coord)

  //clique normal

  private def handleNormalClick(
                                 coord: Coord2D
                               ): Unit =

    val movablePieces =

      Game.getMovablePieces(
        gameState.board,
        Stone.Black,
        ROWS,
        COLS
      )

    if gameState.board
      .get(coord)
      .contains(Stone.Black)
      && movablePieces.contains(coord)
    then

      val jumps =

        Game.getSingleJumps(
          gameState.board,
          Stone.Black,
          coord,
          ROWS,
          COLS
        )

      ui = ui.copy(
        selected = Some(coord),
        destinations = jumps
      )

      updateBoard()

      statusLabel.setText(
        "Peca selecionada."
      )

    else if ui.destinations
      .contains(coord)
    then

      playMove(coord)

  //realizar jogada — executa captura do humano e passa a vez ao computador

  private def playMove(
                        destination: Coord2D
                      ): Unit =

    ui.selected match

      case Some(from) =>

        // Guarda o estado atual no histórico ANTES de alterar o board
        val stateBeforeMove = GameState.pushHistory(gameState)

        val (
          resultOpt,
          newOpen
          ) =

          Game.play(
            gameState.board,
            Stone.Black,
            from,
            destination,
            gameState.lstOpenCoords
          )

        resultOpt match

          case Some(newBoard) =>

            gameState = stateBeforeMove.copy(
              board = newBoard,
              currentPlayer = Stone.White,
              lstOpenCoords = newOpen
            )

            ui = UIState()

            updateBoard()

            statusLabel.setText(
              "Jogada realizada."
            )

            computerTurn()

          case None =>

            statusLabel.setText(
              "Jogada invalida."
            )

      case None => ()

  //jogada do computador — delega em Game.computerPlay que respeita a dificuldade configurada
  // Usa um Timeline de 2s de delay para o jogador ver o que aconteceu antes do computador jogar

  private def computerTurn(): Unit =

    statusLabel.setText(
      "Computador a jogar..."
    )

    // Verifica primeiro se o computador tem jogadas; se nao tiver, o jogador humano ganhou
    if Game.hasLost(gameState.board, Stone.White, ROWS, COLS) then
      statusLabel.setText("Computador sem jogadas. Preto ganhou!")
    else
      // Delay de 2 segundos antes de executar a jogada do computador
      val delay = new Timeline(
        new KeyFrame(Duration.seconds(2), _ => executeComputerMove())
      )
      delay.setCycleCount(1)
      delay.play()

  // Executa de facto a jogada do computador após o delay
  private def executeComputerMove(): Unit =
    val (resultOpt, newRand, newOpen, moveOpt) =
      Game.computerPlay(
        gameState.board,
        Stone.White,
        ROWS,
        COLS,
        gameState.lstOpenCoords,
        gameState.rand,
        gameState.difficulty
      )

    resultOpt match

      case Some(newBoard) =>

        gameState =
          gameState.copy(
            board = newBoard,
            currentPlayer = Stone.Black,
            lstOpenCoords = newOpen,
            rand = newRand
          )

        updateBoard()

        // Verifica se o humano ainda tem jogadas após a jogada do computador
        if Game.hasLost(gameState.board, Stone.Black, ROWS, COLS) then
          statusLabel.setText("Branco ganhou! Sem jogadas para Preto.")
        else
          val moveDesc = moveOpt.map { case (f, t) =>
            val colFrom = ('A' + f._2).toChar
            val colTo   = ('A' + t._2).toChar
            s"${f._1}$colFrom → ${t._1}$colTo"
          }.getOrElse("")
          statusLabel.setText(s"Computador jogou $moveDesc. Sua vez.")

      case None =>

        statusLabel.setText(
          "Computador sem jogadas. Preto ganhou!"
        )

  // "Jogar por mim" usa computerPlay no modo Preto para sugerir e executar a melhor jogada
  private def playForHuman(): Unit =
    if gameState.currentPlayer != Stone.Black then
      statusLabel.setText("Nao e a tua vez...")
    else if Game.hasLost(gameState.board, Stone.Black, ROWS, COLS) then
      statusLabel.setText("Sem jogadas disponíveis.")
    else
      val stateBeforeMove = GameState.pushHistory(gameState)
      val (resultOpt, newRand, newOpen, moveOpt) =
        Game.computerPlay(
          gameState.board,
          Stone.Black,
          ROWS,
          COLS,
          gameState.lstOpenCoords,
          gameState.rand,
          gameState.difficulty
        )
      resultOpt match
        case Some(newBoard) =>
          gameState = stateBeforeMove.copy(
            board = newBoard,
            currentPlayer = Stone.White,
            lstOpenCoords = newOpen,
            rand = newRand
          )
          ui = UIState()
          updateBoard()
          val moveDesc = moveOpt.map { case (f, t) =>
            val colFrom = ('A' + f._2).toChar
            val colTo   = ('A' + t._2).toChar
            s"${f._1}$colFrom → ${t._1}$colTo"
          }.getOrElse("")
          statusLabel.setText(s"Joguei por ti: $moveDesc")
          computerTurn()
        case None =>
          statusLabel.setText("Sem jogadas possíveis.")

  //temporizador

  private def startTimer(): Unit =

    if timer != null then
      timer.stop()

    timer =
      new Timeline(
        new KeyFrame(
          Duration.seconds(1),
          _ =>

            if moveTimeLeft
              < 999999
            then

              moveTimeLeft -= 1

              updateTimerLabel()

              if moveTimeLeft <= 0
              then

                statusLabel.setText(
                  "Tempo esgotado!"
                )

                moveTimeLeft = 30
        )
      )

    // repete o timer indefinidamente
    timer.setCycleCount(-1)

    timer.play()

  private def updateTimerLabel(): Unit =

    if moveTimeLeft >= 999999
    then

      timerLabel.setText(
        "Tempo: ∞"
      )

    else

      timerLabel.setText(
        s"Tempo: ${moveTimeLeft}s"
      )

  //parar captura

  private def finishChainCapture(): Unit =

    gameState =
      gameState.copy(
        currentPlayer =
          Stone.White
      )

    ui = UIState()

    updateBoard()

    statusLabel.setText(
      "Vez do computador."
    )

  //atualizacao visual do tabuleiro
  // Destaca a verde-claro as pecas que tem jogadas disponíveis no início do turno do humano

  private def updateBoard(): Unit =

    // Calcula as pecas movíveis apenas quando e a vez do humano e nao esta em cadeia
    val movablePieces =
      if gameState.currentPlayer == Stone.Black && !ui.isChaining then
        Game.getMovablePieces(gameState.board, Stone.Black, ROWS, COLS)
      else Nil

    for r <- 0 until ROWS do
      for c <- 0 until COLS do

        val cell =
          cells(r)(c)

        cell.getChildren.clear()

        val coord: Coord2D =
          (r, c)

        val bgColor =
          if ui.selected.contains(coord) || ui.chainPos.contains(coord) then
            "#f6c177" // laranja — peca selecionada ou posicao atual na cadeia
          else if ui.destinations.contains(coord) then
            "#90EE90" // verde escuro — destinos possíveis
          else if movablePieces.contains(coord) then
            "#c8f0c8" // verde claro — peca tem jogadas disponíveis
          else
            "#e7ddd2" // cor normal da celula

        cell.setStyle(
          s"-fx-background-color: $bgColor;" +
            "-fx-border-color: #8B4513;" +
            "-fx-border-width: 1;"
        )

        gameState.board
          .get(coord) match

          case Some(Stone.Black) =>

            val circle =
              new Circle()

            circle.setRadius(
              CELL_SIZE * 0.35
            )

            circle.setFill(
              Color.BLACK
            )

            cell.getChildren
              .add(circle)

          case Some(Stone.White) =>

            val circle =
              new Circle()

            circle.setRadius(
              CELL_SIZE * 0.35
            )

            circle.setFill(
              Color.WHITE
            )

            circle.setStroke(
              Color.BLACK
            )

            cell.getChildren
              .add(circle)

          case None => ()

    playerLabel.setText(
      s"Jogador: ${gameState.currentPlayer.display}"
    )

    updateTimerLabel()