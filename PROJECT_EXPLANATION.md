# Konane - Projeto de Programacao Multiparadigma 2025/2026

## Visao Geral

Este projeto implementa o jogo de tabuleiro Konane (Hawaiian Checkers) em Scala 3, seguindo os principios de programacao multiparadigma: programacao funcional pura na camada de negocio e programacao orientada a eventos (JavaFX) na camada de apresentacao.

O jogo e disputado entre um jogador humano (pecas pretas) e o computador (pecas brancas) num tabuleiro retangular com padrao alternado de pecas. O objetivo e capturar pecas adversarias saltando sobre elas; o jogador que ficar sem jogadas validas perde.

## Arquitetura

O projeto segue uma arquitetura de 3 camadas simplificada:

```
Camada de Apresentacao (TUI.scala, KonaneGUI.scala, Main.scala)
         |
Camada de Negocio (Game.scala, GameState.scala)
         |
Camada de Dados (Types.scala, MyRandom.scala)
```

**Separacao clara:** toda a logica do jogo esta em `Game.scala` como funcoes puras. O estado e gerido imutavelmente em `GameState.scala`. A mutabilidade existe apenas nas interfaces (TUI para I/O, GUI para estado grafico), conforme permitido pelo enunciado.

## Estrutura de Pastas

```
src/main/scala/konane/
  Types.scala       - Tipos de dados obrigatorios (Board, Coord2D, Stone)
  MyRandom.scala    - Gerador de numeros aleatorios puro
  Game.scala        - Logica do jogo (funcoes puras)
  GameState.scala   - Estado do jogo, undo, persistencia
  TUI.scala         - Interface textual
  KonaneGUI.scala   - Interface grafica (JavaFX/ScalaFX)
  Main.scala        - Ponto de entrada (escolha TUI/GUI)
build.sbt           - Configuracao do projeto e dependencias
```

## Explicacao das Tarefas

### T1 - randomMove

Gera uma coordenada aleatoria a partir de uma lista de posicoes, usando `MyRandom` para manter pureza funcional. O `MyRandom` usa um Linear Congruential Generator com seed baseada em `System.nanoTime()`, garantindo resultados diferentes entre execucoes.

**Localizacao:** `Game.scala`, metodo `randomMove`

### T2 - play

Valida e executa uma jogada de captura. O metodo recebe coordenadas de origem e destino, verifica se a jogada e valida (salto sobre peca adversaria para posicao vazia) e devolve o novo tabuleiro. Suporta capturas multiplas atraves de `findCapturePath` (BFS).

Inclui tambem `initBoard` para inicializar o tabuleiro e `removeInitialStones` para remover o par inicial de pecas (preta + branca adjacentes) do centro ou canto.

**Localizacao:** `Game.scala`, metodos `play`, `initBoard`, `removeInitialStones`, `findCapturePath`, `executeCapture`

### T3 - playRandomly

Funcao de ordem superior que joga automaticamente usando uma funcao `f` (tipicamente `randomMove`) para selecionar aleatoriamente uma peca e um destino valido.

**Localizacao:** `Game.scala`, metodo `playRandomly`

### T4 - Representacao do Tabuleiro

Converte o tabuleiro numa string formatada com cabecalhos de colunas (A, B, C...) e linhas numeradas, onde B = peca preta, W = peca branca, e `.` = posicao vazia.

**Localizacao:** `Game.scala`, metodo `boardToString`

### T5 - Detecao de Vitoria/Derrota

Verifica se um jogador perdeu (sem jogadas validas disponiveis). `hasLost` verifica se o jogador tem jogadas; `checkWinner` devolve o vencedor se o jogo terminou.

**Localizacao:** `Game.scala`, metodos `hasLost`, `checkWinner`

### T6 - Temporizador e Undo

**Temporizador:** configuravel no inicio do jogo (em segundos, 0 = sem limite). Na TUI, verificado a cada input do jogador via `System.currentTimeMillis()`. Na GUI, atualizado a cada segundo via `Timeline`.

**Undo:** o estado e guardado numa pilha (`history` no `GameState`) antes de cada jogada do humano. O undo restaura o estado anterior, anulando tanto a jogada do humano como a resposta do computador.

**Localizacao:** `GameState.scala` (pushHistory, undo), `TUI.scala` (checkTimeout), `KonaneGUI.scala` (timer, performUndo)

### T7 - TUI (Text User Interface)

Interface textual completa com menu principal (novo jogo, carregar, sair) e opcoes durante o jogo:
- **[J]ogar** - selecionar peca e destino
- **[U]ndo** - desfazer ultima jogada
- **[G]uardar** - guardar jogo em ficheiro
- **[R]einiciar** - novo jogo com mesmas configuracoes
- **[D]imensoes** - alterar tamanho do tabuleiro
- **[T]empo** - alterar tempo maximo por jogada
- **[N]ivel** - alterar dificuldade
- **[Q]uit** - sair

Capturas em cadeia sao interativas: apos cada salto, o jogador pode continuar ou parar.

**Localizacao:** `TUI.scala`

### T8 - GUI (Interface Grafica)

Interface grafica em JavaFX (via ScalaFX) com tabuleiro fixo 6x6:
- Pecas representadas como circulos (preto/branco)
- Selecao por clique: pecas moveis destacadas a verde, destinos validos indicados
- Capturas em cadeia com botao "Parar Captura"
- Barra lateral com: dificuldade, novo jogo, undo, guardar, carregar
- Temporizador visual com contagem decrescente
- Computador joga automaticamente apos jogada do humano

**Localizacao:** `KonaneGUI.scala`

## Sistema de Undo

O undo funciona como uma pilha (stack) de estados:
1. Antes de cada jogada do humano, o estado atual e guardado na pilha (`pushHistory`)
2. Ao fazer undo, o ultimo estado e retirado da pilha, restaurando o tabuleiro, jogador atual, coordenadas livres e seed do random
3. Isto anula efetivamente a jogada do humano E a resposta do computador
4. A pilha e persistida no ficheiro de save

## Temporizador

- **TUI:** verificado reativamente quando o jogador insere input (via `System.currentTimeMillis()`)
- **GUI:** `Timeline` do JavaFX atualiza a cada segundo, mostrando tempo restante e forcando fim de turno ao expirar
- Se o tempo expira durante uma captura em cadeia, as capturas ja feitas sao mantidas

## Persistencia

O estado do jogo e guardado num ficheiro de texto (`konane_save.txt`) com formato proprio:
- Versao do formato (KONANE_SAVE_V1)
- Dimensoes, jogador atual, tempo, dificuldade, seed
- Coordenadas livres e tabuleiro
- Historico completo de undo

## Logica Funcional

### Principios aplicados:
- **Imutabilidade:** `Board` (ParMap), `GameState` (case class), `Stone` (enum) sao imutaveis
- **Pure functions:** toda a logica em `Game.scala` e pura (sem side effects)
- **Recursividade:** `@tailrec` em `bfs`, `explore`, `gameLoop`, `mainMenu`
- **Pattern matching:** usado extensivamente (Stone, Option, List)
- **Higher-order functions:** `playRandomly` recebe funcao como parametro, uso de `foldLeft`/`foldRight`, `map`, `flatMap`, `filter`, `collect`
- **MyRandom:** encapsula estado do gerador aleatorio para manter pureza

### Excecoes a pureza (justificadas):
- I/O na TUI (StdIn, println)
- Estado mutavel na GUI (vars para estado grafico)
- Persistencia em ficheiro (save/load)

## Como Correr o Projeto

### Pre-requisitos
- JDK 17+ (recomendado JDK 21)
- sbt 1.10.x

### Via sbt (terminal)
```
sbt run
```
Escolher 1 para TUI ou 2 para GUI.

### Via IntelliJ IDEA
1. File > Open > selecionar a pasta do projeto
2. Importar como projeto sbt
3. Aguardar indexacao e download de dependencias
4. Run > Edit Configurations > Add New > sbt Task > `run`
5. Ou executar `Main.scala` diretamente

### Importacao a partir do ZIP
1. Extrair o ficheiro ZIP
2. IntelliJ > File > Open > selecionar pasta extraida
3. Selecionar "Import project from external model" > sbt
4. Clicar "OK" e aguardar resolucao de dependencias

## Como Testar

### Teste basico
1. Iniciar o jogo via TUI (opcao 1)
2. Criar novo jogo 6x6
3. Verificar tabuleiro inicial (padrao alternado com 2 posicoes vazias)
4. Jogar selecionando pecas e destinos
5. Verificar que capturas funcionam corretamente
6. Testar undo, guardar, carregar

### Teste da GUI
1. Iniciar via GUI (opcao 2)
2. Verificar que pecas moveis ficam verdes
3. Clicar numa peca, verificar destinos validos
4. Executar captura, verificar captura em cadeia
5. Testar botoes da barra lateral

## Fluxos Principais

### Fluxo de jogo (TUI)
```
Menu Principal -> Novo Jogo -> Configuracao -> Game Loop
                                                  |
                                     Jogada Humano -> Jogada Computador -> Verificar Vencedor -> Repetir
```

### Fluxo de captura em cadeia
```
Selecionar peca -> Escolher destino -> Captura executada
                                            |
                                  Mais saltos? -> Sim: Continuar/Parar
                                                  Nao: Fim do turno
```

## Decisoes de Implementacao

1. **MyRandom com LCG:** garante reprodutibilidade e pureza funcional
2. **BFS para caminhos de captura:** encontra caminhos validos entre duas posicoes, suportando capturas multiplas com mudanca de direcao
3. **Capturas interativas (step-by-step):** tanto na TUI como na GUI, o jogador decide salto a salto se quer continuar
4. **Niveis de dificuldade:** Easy (aleatorio), Medium (greedy - maximiza capturas), Hard (minimax com alpha-beta)
5. **Formato de save proprio:** simples, legivel, auto-contido

## Limitacoes

- O temporizador na TUI so e verificado quando o jogador insere input (nao interrompe ativamente)
- A GUI tem tabuleiro fixo 6x6 (conforme especificacao)
- O minimax (dificuldade Hard) usa profundidade 3, o que pode ser lento em tabuleiros grandes
- O ficheiro de save nao e encriptado

## Possiveis Melhorias Futuras

- Adicionar configuracao de tempo na GUI via campo de input
- Animacao de capturas na GUI
- Suporte para dois jogadores humanos
- Historico de jogadas visivel

## Guia Rapido para Discussao Oral

### Pontos-chave a dominar:
1. **Arquitetura:** camada de negocio (funcional pura) vs. camada de apresentacao (event-driven)
2. **Tipos obrigatorios:** Board = ParMap[Coord2D, Stone], Coord2D = (Int, Int)
3. **Pureza funcional:** Game.scala nao tem side effects; MyRandom garante pureza no random
4. **T1-T4:** assinaturas, como funcionam, onde estao
5. **T5:** hasLost verifica se jogador tem jogadas validas
6. **T6:** undo via pilha de estados em GameState; timer via System.currentTimeMillis
7. **T7:** TUI com tail recursion no gameLoop
8. **T8:** GUI com ScalaFX, selecao por clique, feedback visual
9. **Pattern matching:** usado em Stone, opcoes de menu, resultados Option
10. **Higher-order functions:** playRandomly, fold, map, flatMap, filter, collect

### Perguntas provaveis:
- "Onde esta a imutabilidade?" -> Board e ParMap (imutavel), GameState e case class
- "Como funciona o undo?" -> Pilha de estados, pushHistory antes da jogada, pop no undo
- "Porque usar MyRandom?" -> Para manter pureza funcional (seed explicita, sem estado global)
- "Como funciona a captura multipla?" -> BFS em findCapturePath ou step-by-step interativo
- "Onde estao os side effects?" -> Apenas na TUI (I/O) e GUI (vars mutaveis + JavaFX)
