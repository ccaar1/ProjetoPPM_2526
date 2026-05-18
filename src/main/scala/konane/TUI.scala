package konane

import scala.annotation.tailrec
import scala.io.StdIn

// T7: Interface de utilizador textual (Text-based User Interface)
object TUI:

  private val SAVE_FILE = "konane_save.txt"

  // Ponto de entrada da TUI — mostra cabecalho e abre o menu principal
  def start(): Unit =
    println("~" * 30)
    println("     KONANE - Versao TUI")
    println("~" * 30)
    mainMenu()

  // Menu principal com opcões de novo jogo, carregar e sair
  @tailrec
  private def mainMenu(): Unit =
    println("\n--- Menu Principal ---")
    println("1. Novo Jogo")
    println("2. Carregar Jogo")
    println("3. Sair")
    print("> ")
    StdIn.readLine() match
      case "1" =>
        val state = setupNewGame()
        gameLoop(state)
        mainMenu()
      case "2" =>
        GameState.load(SAVE_FILE) match
          case Some(state) =>
            println("Jogo carregado com sucesso!")
            gameLoop(state)
            mainMenu()
          case None =>
            println("Nenhum jogo guardado encontrado.")
            mainMenu()
      case "3" =>
        println("A Hui Hou!")
      case _ =>
        println("Opcao invalida.")
        mainMenu()

  // Configura um novo jogo pedindo dimensões, tempo e dificuldade ao utilizador
  private def setupNewGame(): GameState =
    println("\n--- Configuracao do Jogo ---")
    val (rows, cols) = readBoardDimensions()
    val maxTime = readMaxTime()
    val difficulty = readDifficulty()
    val state = GameState.newGame(rows, cols, maxTime, difficulty)
    println(s"\nJogo criado: ${rows}x${cols}, Tempo: ${if maxTime == 0 then "sem limite" else s"${maxTime}s"}, " +
      s"Dificuldade: ${difficultyName(difficulty)}")
    state

  // Le e valida as dimensões do tabuleiro — devem ser pares entre 4 e 12
  private def readBoardDimensions(): (Int, Int) =
    print("Dimensoes do tabuleiro (linhas colunas, ex: 6 6): ")
    val input = StdIn.readLine().trim.split("\\s+")
    if input.length == 2 then
      try
        val r = input(0).toInt
        val c = input(1).toInt
        if r >= 4 && r <= 12 && c >= 4 && c <= 12 && r % 2 == 0 && c % 2 == 0 then (r, c)
        else
          println("Dimensoes devem ser pares entre 4 e 12.")
          readBoardDimensions()
      catch
        case _: NumberFormatException =>
          println("Entrada invalida.")
          readBoardDimensions()
    else
      println("Formato: <linhas> <colunas> (ex: 6 6)")
      readBoardDimensions()

  // Le o tempo maximo por jogada em segundos (0 significa sem limite)
  private def readMaxTime(): Long =
    print("Tempo maximo por jogada em segundos (0 = sem limite): ")
    try
      val t = StdIn.readLine().trim.toLong
      if t >= 0 then t else { println("Valor invalido."); readMaxTime() }
    catch
      case _: NumberFormatException =>
        println("Entrada invalida.")
        readMaxTime()

  // Le o nível de dificuldade: 1=Facil, 2=Medio, 3=Difícil
  private def readDifficulty(): Int =
    print("Nivel de dificuldade (1=Facil, 2=Medio, 3=Dificil): ")
    try
      val d = StdIn.readLine().trim.toInt
      if d >= 1 && d <= 3 then d else { println("Escolha 1, 2 ou 3."); readDifficulty() }
    catch
      case _: NumberFormatException =>
        println("Entrada invalida.")
        readDifficulty()

  // Converte número de dificuldade para nome legível
  private def difficultyName(d: Int): String = d match
    case 1 => "Facil"
    case 2 => "Medio"
    case _ => "Dificil"

  // Converte índice de coluna para letra (0 -> 'A', 1 -> 'B', ...)
  private def colToLetter(c: Int): Char = ('A' + c).toChar

  // Converte letra de coluna para índice ('A' -> 0, 'B' -> 1, ...)
  private def letterToCol(ch: Char): Int = ch.toUpper - 'A'

  // Formata uma coordenada para apresentacao ao utilizador (ex: (2,0) -> "2A")
  private def formatCoord(c: Coord2D): String = s"${c._1}${colToLetter(c._2)}"

  // Interpreta texto introduzido pelo utilizador como coordenada
  // Aceita formatos: "2A", "2 A", "A2", "2,0", "2 0"
  private def parseCoord(input: String, maxRow: Int, maxCol: Int): Option[Coord2D] =
    val s = input.trim.toUpperCase.replaceAll("[,\\s]+", "")
    if s.isEmpty then None
    else
      // Tenta formato número+letra (ex: "2A") ou letra+número (ex: "A2")
      val numLetterPattern = "^(\\d+)([A-Z])$".r
      val letterNumPattern = "^([A-Z])(\\d+)$".r
      val numNumPattern = "^(\\d+)[,\\s]*(\\d+)$".r

      s match
        case numLetterPattern(row, col) =>
          val r = row.toInt
          val c = letterToCol(col.head)
          if r >= 0 && r < maxRow && c >= 0 && c < maxCol then Some((r, c)) else None
        case letterNumPattern(col, row) =>
          val r = row.toInt
          val c = letterToCol(col.head)
          if r >= 0 && r < maxRow && c >= 0 && c < maxCol then Some((r, c)) else None
        case _ =>
          // Tenta dois números separados por espaco ou vírgula (ex: "2 0")
          val parts = input.trim.split("[,\\s]+")
          if parts.length == 2 then
            try
              val r = parts(0).toInt
              val c = parts(1).toInt
              if r >= 0 && r < maxRow && c >= 0 && c < maxCol then Some((r, c)) else None
            catch
              case _: NumberFormatException => None
          else None

  // T7 + T6: Ciclo principal do jogo — alterna entre turno do humano e do computador
  // Usa @tailrec para evitar stack overflow em jogos longos
  @tailrec
  private def gameLoop(state: GameState): Unit =
    println()
    println(Game.boardToString(state.board, state.rows, state.cols))

    // T5: verifica se ha vencedor antes de continuar
    Game.checkWinner(state.board, state.currentPlayer, state.rows, state.cols) match
      case Some(winner) =>
        println(s"\n*** ${winner.display} GANHOU! ${state.currentPlayer.display} nao tem jogadas validas. ***")
      case None =>
        if state.currentPlayer == Stone.Black then
          // Turno do humano
          humanTurn(state) match
            case Some(newState) => gameLoop(newState)
            case None => () // utilizador saiu ou guardou
        else
          // Turno do computador — delega em Game.computerPlay respeitando a dificuldade
          println(s"\nComputador (${state.currentPlayer.display}) a pensar...")
          val (resultOpt, newRand, newOpen, moveOpt) =
            Game.computerPlay(state.board, state.currentPlayer, state.rows, state.cols,
              state.lstOpenCoords, state.rand, state.difficulty)
          resultOpt match
            case Some(newBoard) =>
              moveOpt match
                case Some((from, to)) =>
                  println(s"Computador jogou: ${formatCoord(from)} -> ${formatCoord(to)}")
                case None => ()
              val newState = state.copy(
                board = newBoard,
                currentPlayer = state.currentPlayer.opponent,
                lstOpenCoords = newOpen,
                rand = newRand
              )
              gameLoop(newState)
            case None =>
              println(s"\n*** ${state.currentPlayer.opponent.display} GANHOU! Computador nao tem jogadas. ***")

  // Turno interativo do humano com suporte a temporizador e capturas em cadeia
  // Devolve Some(novoEstado) para continuar ou None para terminar
  private def humanTurn(state: GameState): Option[GameState] =
    val movablePieces = Game.getMovablePieces(state.board, state.currentPlayer, state.rows, state.cols)
    if movablePieces.isEmpty then
      println(s"Sem jogadas validas! ${state.currentPlayer.opponent.display} ganhou!")
      None
    else
      println(s"\nJogador: ${state.currentPlayer.display}")
      if state.maxMoveTime > 0 then
        println(s"Tempo limite: ${state.maxMoveTime}s")
      println(s"Pecas que podem jogar: ${movablePieces.map(formatCoord).mkString(", ")}")
      println()
      println("[J]ogar  [A]uto  [U]ndo  [G]uardar  [R]einiciar  [D]imensoes  [T]empo  [N]ivel  [Q]uit")
      print("> ")
      val startTime = System.currentTimeMillis()

      StdIn.readLine() match
        case null => None
        case input =>
          input.trim.toUpperCase match
            case "Q" =>
              println("A sair do jogo...")
              None
            case "G" =>
              if GameState.save(state, SAVE_FILE) then
                println(s"Jogo guardado em '$SAVE_FILE'.")
              else
                println("Erro ao guardar o jogo.")
              Some(state) // continua com o mesmo estado
            case "U" =>
              GameState.undo(state) match
                case Some(prevState) =>
                  println("Undo realizado!")
                  Some(prevState)
                case None =>
                  println("Nada para desfazer.")
                  Some(state)
            case "R" =>
              println("A reiniciar...")
              Some(GameState.newGame(state.rows, state.cols, state.maxMoveTime, state.difficulty))
            case "D" =>
              val (rows, cols) = readBoardDimensions()
              println(s"Novo jogo ${rows}x${cols}...")
              Some(GameState.newGame(rows, cols, state.maxMoveTime, state.difficulty))
            case "T" =>
              val maxTime = readMaxTime()
              println(s"Tempo atualizado para ${if maxTime == 0 then "sem limite" else s"${maxTime}s"}.")
              Some(state.copy(maxMoveTime = maxTime))
            case "N" =>
              val diff = readDifficulty()
              println(s"Dificuldade atualizada para ${difficultyName(diff)}.")
              Some(state.copy(difficulty = diff))
            case "J" =>
              executePlayerTurn(state, movablePieces, startTime)
            case "A" =>
              // O computador escolhe e executa a melhor jogada em nome do humano
              playAuto(state)
            case _ =>
              // Tenta interpretar a entrada diretamente como coordenada de peca (ex: "2A")
              parseCoord(input, state.rows, state.cols) match
                case Some(coord) if movablePieces.contains(coord) =>
                  executeChainCapture(state, coord, startTime)
                case _ =>
                  println("Opcao invalida. Escolha uma opcao do menu ou insira coordenada de peca (ex: 2A).")
                  Some(state)

  // [A]uto — usa Game.computerPlay para jogar automaticamente pelo humano (Preto)
  // Guarda o estado no histórico para permitir undo após a jogada automatica
  private def playAuto(state: GameState): Option[GameState] =
    val (resultOpt, newRand, newOpen, moveOpt) =
      Game.computerPlay(
        state.board, state.currentPlayer,
        state.rows, state.cols,
        state.lstOpenCoords, state.rand, state.difficulty
      )
    resultOpt match
      case Some(newBoard) =>
        moveOpt.foreach { case (from, to) =>
          println(s"Joguei por ti: ${formatCoord(from)} -> ${formatCoord(to)}")
        }
        // Guarda estado anterior no histórico antes de aplicar a jogada automatica
        Some(GameState.pushHistory(state).copy(
          board = newBoard,
          currentPlayer = state.currentPlayer.opponent,
          lstOpenCoords = newOpen,
          rand = newRand
        ))
      case None =>
        println("Sem jogadas possiveis.")
        Some(state)

  // Pede ao utilizador que selecione uma peca e inicia a cadeia de capturas
  private def executePlayerTurn(state: GameState, movablePieces: List[Coord2D], startTime: Long): Option[GameState] =
    print(s"Selecione a peca (ex: ${formatCoord(movablePieces.head)}): ")
    val pieceInput = StdIn.readLine()
    if checkTimeout(state, startTime) then
      println("Tempo esgotado! Perdeu a jogada.")
      Some(state.copy(currentPlayer = state.currentPlayer.opponent))
    else
      parseCoord(pieceInput, state.rows, state.cols) match
        case Some(from) if movablePieces.contains(from) =>
          executeChainCapture(state, from, startTime)
        case _ =>
          println(s"Peca invalida. Escolha entre: ${movablePieces.map(formatCoord).mkString(", ")}")
          Some(state)

  // Executa capturas em cadeia a partir de uma posicao inicial
  // Permite ao jogador continuar a capturar ou parar voluntariamente com [P]
  private def executeChainCapture(state: GameState, from: Coord2D, startTime: Long): Option[GameState] =
    val stateWithHistory = GameState.pushHistory(state)

    // Termina o turno e passa a vez ao adversario
    def finishTurn(board: Board, openCoords: List[Coord2D], rand: MyRandom, captures: Int): Option[GameState] =
      println(s"Turno completo. Capturas: $captures")
      Some(stateWithHistory.copy(
        board = board, currentPlayer = state.currentPlayer.opponent,
        lstOpenCoords = openCoords, rand = rand
      ))

    // Ciclo de capturas — verifica saltos disponíveis e pergunta ao jogador
    def loop(board: Board, pos: Coord2D, open: List[Coord2D],
             rand: MyRandom, captures: Int): Option[GameState] =
      val jumps = Game.getSingleJumps(board, state.currentPlayer, pos, state.rows, state.cols)
      if jumps.isEmpty && captures == 0 then
        println("Sem capturas possiveis desta posicao.")
        Some(state)
      else if jumps.isEmpty then
        finishTurn(board, open, rand, captures)
      else if checkTimeout(state, startTime) then
        println("Tempo esgotado!")
        if captures > 0 then finishTurn(board, open, rand, captures)
        else Some(state.copy(currentPlayer = state.currentPlayer.opponent))
      else
        if captures > 0 then
          // Ja fez pelo menos uma captura — mostra tabuleiro atual e pergunta se quer continuar
          println()
          println(Game.boardToString(board, state.rows, state.cols))
          println(s"\nPosicao atual: ${formatCoord(pos)}")
          println(s"Destinos possiveis: ${jumps.map(formatCoord).mkString(", ")}")
          println("[C]ontinuar captura  [P]arar")
          print("> ")
          StdIn.readLine().trim.toUpperCase match
            case "P" => finishTurn(board, open, rand, captures)
            case _ => readJump(board, pos, open, rand, captures, jumps)
        else
          println(s"Destinos possiveis: ${jumps.map(formatCoord).mkString(", ")}")
          readJump(board, pos, open, rand, captures, jumps)

    // Le e valida o destino escolhido pelo jogador para o próximo salto
    def readJump(board: Board, pos: Coord2D, open: List[Coord2D],
                 rand: MyRandom, captures: Int, jumps: List[Coord2D]): Option[GameState] =
      print(s"Destino (ex: ${formatCoord(jumps.head)}): ")
      if checkTimeout(state, startTime) then
        println("Tempo esgotado!")
        if captures > 0 then finishTurn(board, open, rand, captures)
        else Some(state.copy(currentPlayer = state.currentPlayer.opponent))
      else
        parseCoord(StdIn.readLine(), state.rows, state.cols) match
          case Some(to) if jumps.contains(to) =>
            val (newBoard, newOpen) = Game.executeCapture(board, pos, to, open)
            println(s"Captura: ${formatCoord(pos)} -> ${formatCoord(to)}")
            loop(newBoard, to, newOpen, rand, captures + 1)
          case _ =>
            println(s"Destino invalido. Escolha entre: ${jumps.map(formatCoord).mkString(", ")}")
            readJump(board, pos, open, rand, captures, jumps)

    loop(stateWithHistory.board, from, stateWithHistory.lstOpenCoords, stateWithHistory.rand, 0)

  // T6: verifica se o tempo limite da jogada foi ultrapassado
  private def checkTimeout(state: GameState, startTime: Long): Boolean =
    if state.maxMoveTime <= 0 then false
    else
      val elapsed = (System.currentTimeMillis() - startTime) / 1000
      elapsed >= state.maxMoveTime
