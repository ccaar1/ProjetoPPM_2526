package konane

import scala.io.StdIn

object Main:
  def main(args: Array[String]): Unit =
    println("~" * 30)
    println("           KONANE")
    println("~" * 30)
    println()
    println("Qual interface deseja jogar?")
    println("1. TUI (textual)")
    println("2. GUI (gráfica)")
    print("> ")


    val choice = StdIn.readLine()
    choice match
      case "1" => TUI.start()
      case "2" => KonaneGUI.main(Array.empty)
      case _ =>
        println("Opcao invalida. A iniciar TUI...")
        TUI.start()
