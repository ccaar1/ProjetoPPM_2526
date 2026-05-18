package konane

import scala.collection.parallel.immutable.ParMap
import java.io.{PrintWriter, File}
import scala.io.Source

// T6 historia do undo
case class GameState(
  board: Board,
  currentPlayer: Stone,
  lstOpenCoords: List[Coord2D],
  rows: Int,
  cols: Int,
  rand: MyRandom,
  history: List[(Board, Stone, List[Coord2D], MyRandom)], // undo stack
  maxMoveTime: Long, // em segundos
  difficulty: Int // 1.Facil, 2. Medio, 3 Dificil
)

object GameState:

  def newGame(rows: Int, cols: Int, maxMoveTime: Long, difficulty: Int): GameState =
    val rand = MyRandom()
    val (board, open, r2) = Game.createGame(rows, cols, rand)
    GameState(board, Stone.Black, open, rows, cols, r2, Nil, maxMoveTime, difficulty)

  // T6 estado atual para o historico
  def pushHistory(state: GameState): GameState =
    val entry = (state.board, state.currentPlayer, state.lstOpenCoords, state.rand)
    state.copy(history = entry :: state.history)

  // T6 undo, faz pop do ultimo adicionado
  def undo(state: GameState): Option[GameState] =
    state.history match
      case (prevBoard, prevPlayer, prevOpen, prevRand) :: rest =>
        Some(state.copy(
          board = prevBoard,
          currentPlayer = prevPlayer,
          lstOpenCoords = prevOpen,
          rand = prevRand,
          history = rest
        ))
      case Nil => None

  // guardar ficheiro
  def save(state: GameState, filename: String): Boolean =
    try
      val pw = new PrintWriter(new File(filename))
      try
        pw.println("KONANE_SAVE_V1")
        pw.println(s"${state.rows},${state.cols}")
        pw.println(state.currentPlayer.toString)
        pw.println(state.maxMoveTime.toString)
        pw.println(state.difficulty.toString)
        pw.println(state.rand.seed.toString)
        // Open coords
        pw.println(state.lstOpenCoords.map(c => s"${c._1}:${c._2}").mkString("|"))
        // Board
        val boardStr = state.board.seq.toList.sortBy(e => (e._1._1, e._1._2))
          .map { case ((r, c), s) => s"$r:$c:${s.display}" }.mkString("|")
        pw.println(boardStr)
        // History
        pw.println(state.history.size.toString)
        state.history.foreach { case (b, p, open, r) =>
          val bStr = b.seq.toList.sortBy(e => (e._1._1, e._1._2))
            .map { case ((r, c), s) => s"$r:$c:${s.display}" }.mkString("|")
          pw.println(bStr)
          pw.println(p.toString)
          pw.println(open.map(c => s"${c._1}:${c._2}").mkString("|"))
          pw.println(r.seed.toString)
        }
        true
      finally pw.close()
    catch
      case _: Exception => false

  // carrega jogo de um ficheiro preexistente
  def load(filename: String): Option[GameState] =
    try
      val file = new File(filename)
      if !file.exists() then None
      else
        val source = Source.fromFile(file)
        val lines = try source.getLines().toList finally source.close()
        if lines.isEmpty || lines.head != "KONANE_SAVE_V1" then None
        else
          val dims = lines(1).split(",").map(_.trim.toInt)
          val rows = dims(0)
          val cols = dims(1)
          val player = if lines(2).trim == "Black" then Stone.Black else Stone.White
          val maxTime = lines(3).trim.toLong
          val diff = lines(4).trim.toInt
          val seed = lines(5).trim.toLong
          val rand = MyRandom(seed)

          val openCoords = parseCoords(lines(6))
          val board = parseBoard(lines(7))

          val histCount = lines(8).trim.toInt
          val history = (0 until histCount).toList.map { i =>
            val base = 9 + i * 4
            val hBoard = parseBoard(lines(base))
            val hPlayer = if lines(base + 1).trim == "Black" then Stone.Black else Stone.White
            val hOpen = parseCoords(lines(base + 2))
            val hRand = MyRandom(lines(base + 3).trim.toLong)
            (hBoard, hPlayer, hOpen, hRand)
          }

          Some(GameState(board, player, openCoords, rows, cols, rand, history, maxTime, diff))
    catch
      case _: Exception => None

  private def parseCoords(line: String): List[Coord2D] =
    if line.trim.isEmpty then Nil
    else line.trim.split("\\|").toList.map { s =>
      val parts = s.split(":")
      (parts(0).toInt, parts(1).toInt)
    }

  private def parseBoard(line: String): Board =
    if line.trim.isEmpty then ParMap.empty
    else
      val entries = line.trim.split("\\|").map { s =>
        val parts = s.split(":")
        val stone = if parts(2) == "B" then Stone.Black else Stone.White
        (parts(0).toInt, parts(1).toInt) -> stone
      }
      ParMap(entries.toSeq*)
