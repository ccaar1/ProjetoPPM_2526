Projeto Konane - Programacao Multiparadigma 2025/2026
=====================================================

Jogo de tabuleiro Konane (Hawaiian Checkers) implementado em Scala 3
com interfaces TUI e GUI (JavaFX/ScalaFX).

Como executar:
  1. Na raiz do projeto: sbt run
  2. Escolher 1 para TUI ou 2 para GUI

Estrutura:
  src/main/scala/konane/
    Types.scala       - Tipos de dados (Board, Coord2D, Stone)
    MyRandom.scala    - Gerador aleatorio puro
    Game.scala        - Logica do jogo (funcoes puras)
    GameState.scala   - Estado do jogo, undo, persistencia
    TUI.scala         - Interface textual
    KonaneGUI.scala   - Interface grafica (JavaFX)
    Main.scala        - Ponto de entrada

Notas:
  - O jogador humano joga com pecas pretas (B), o computador com brancas (W)
  - Na GUI o tabuleiro e fixo 6x6 conforme especificacao
  - Na TUI o tabuleiro pode variar entre 4x4 e 12x12 (dimensoes pares)
  - O jogo pode ser guardado e retomado entre execucoes
