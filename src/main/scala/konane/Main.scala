package konane

import scala.io.StdIn
import scala.annotation.tailrec

object Main:
  def main(args: Array[String]): Unit =
    println("~" * 30)
    println("           KONANE")
    println("~" * 30)
    println()
    askInterface()

  // continua a perguntar até o utilizador escolher 1, 2 ou -1
  // @tailrec garante que chamadas recursivas não acumulam stack frames
  @tailrec
  private def askInterface(): Unit =
    println("Qual interface deseja jogar?")
    println("1. TUI (textual)")
    println("2. GUI (gráfica)")
    println("-1. Sair")
    print("> ")

    StdIn.readLine() match
      case "1"  => TUI.start()
      case "2"  => KonaneGUI.main(Array.empty)
      case "-1" => println("A Hui Hou!")
      case _    =>
        println("Opcao invalida. Escolha 1, 2 ou -1.")
        println()
        askInterface()
