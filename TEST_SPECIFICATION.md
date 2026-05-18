# Especificacao de Testes - Konane GUI

## GUI: Funcionalidade Principal

### G1 - Iniciar jogo via GUI
- **Passos:** Abrir a aplicacao e selecionar a interface GUI.
- **Esperado:** A janela do jogo abre, o tabuleiro inicial e os controlos aparecem.
- **Verifica:** Titulos, botoes e tabuleiro visiveis.

### G2 - Criar novo jogo com dimensoes validas
- **Passos:** Selecionar "Novo Jogo" no GUI, escolher dimensoes validas (4-12 pares), tempo e dificuldade.
- **Esperado:** Novo tabuleiro e configuracoes aplicadas.
- **Verifica:** O tabuleiro corresponde às dimensoes escolhidas e o estado reflete o tempo/dificuldade.

### G3 - Selecionar peca e destino no tabuleiro
- **Passos:** Clicar numa peca valida e depois num destino de captura.
- **Esperado:** Captura executada, tabuleiro atualizado, turno do computador inicia.
- **Verifica:** A peca moveu-se, a peca capturada desapareceu e o estado mudou.

### G4 - Captura em cadeia via GUI
- **Passos:** Iniciar um movimento que permita continuar capturando.
- **Esperado:** O GUI permite continuar ou parar a cadeia, mostrando destinos validos.
- **Verifica:** Capturas adicionais sao aplicadas com sucesso enquanto o jogador escolher continuar.

### G5 - Undo na GUI
- **Passos:** Fazer uma jogada e clicar "Undo".
- **Esperado:** O tabuleiro regressa ao estado anterior ao turno completo.
- **Verifica:** Tanto a jogada do humano quanto a resposta do computador sao desfeitas.

### G6 - Guardar e carregar jogo no GUI
- **Passos:** Guardar o jogo atual e recarregar o mesmo ficheiro.
- **Esperado:** O estado recarregado corresponde ao momento do save.
- **Verifica:** Posicoes, jogador atual, tempo restante e dificultade sao restaurados.

### G7 - Alterar dificuldade via GUI
- **Passos:** Mudar a dificuldade no painel de configuracao e iniciar novo jogo.
- **Esperado:** O comportamento do computador altera-se conforme o nivel escolhido.
- **Verifica:** As escolhas de dificuldade ficam refletidas no estado do jogo.

### G8 - Alterar tempo por jogada via GUI
- **Passos:** Ajustar o tempo por jogada no GUI.
- **Esperado:** O timer usa o novo valor em turnos subsequentes.
- **Verifica:** A interface de tempo mostra o valor alterado.

### G9 - Timer expira no GUI
- **Passos:** Configurar tempo limitado e nao jogar ate o tempo acabar.
- **Esperado:** O turno do jogador termina e o computador joga.
- **Verifica:** Mensagem de tempo esgotado e passagem de turno automática.

### G10 - Fim de jogo na GUI
- **Passos:** Jogar ate nao haver jogadas validas para um jogador.
- **Esperado:** Mensagem de vencedor mostra-se e o jogo termina.
- **Verifica:** O tabuleiro fica inativo e o estado de vitoria é apresentado.

### G11 - Verificar elementos do GUI apos reiniciar
- **Passos:** Reiniciar o jogo no GUI durante uma partida.
- **Esperado:** Novo tabuleiro e configuracoes de jogo redefinidos.
- **Verifica:** O estado anterior e resultados de jogadas anteriores desaparecem.

### G12 - Comportamento de botões e menus
- **Passos:** Testar os botões principais: Novo Jogo, Guardar, Carregar, Undo, Reiniciar.
- **Esperado:** Cada ação executa o comportamento esperado e atualiza a interface.
- **Verifica:** Nao ha widgets inativos ou erros visuais apos cada ação.

### G13 - Suporte a diferentes tamanhos de tabuleiro no GUI
- **Passos:** Criar jogos com 4x4, 6x6, 8x8, 10x10 e 12x12.
- **Esperado:** O GUI renderiza corretamente cada tamanho.
- **Verifica:** Nenhuma linha ou coluna é truncada e os headers mantêm-se alinhados.

### G14 - Mensagens de erro e validação no GUI
- **Passos:** Inserir valores invalidos nas configurações do jogo.
- **Esperado:** Mensagens de erro visíveis sem crash.
- **Verifica:** O usuário recebe feedback claro e pode corrigir a entrada.

### G15 - Persistencia de estado e reload
- **Passos:** Guardar um jogo, fechar a aplicação e abrir de novo, então carregar.
- **Esperado:** O jogo retoma do ponto salvo.
- **Verifica:** O estado não é perdido entre sessões.

### G16 - Comportamento do AI nas dificuldades do GUI
- **Passos:** Defender-se contra o computador em todos os niveis de dificuldade.
- **Esperado:** O computador faz jogadas plausíveis para Façil, Médio e Difícil.
- **Verifica:** As escolhas de movimento mudam conforme o nivel.

### G17 - Botões desativados ou ativados corretamente
- **Passos:** Verificar quando os botões Undo, Guardar e Reiniciar estão disponíveis.
- **Esperado:** Nao existem ações disparadas quando os botões estão desativados.
- **Verifica:** Os botões apresentam o estado correto durante o jogo.

### G18 - Estabilidade de interface
- **Passos:** Navegar entre opcoes e iniciar/terminar jogos repetidamente.
- **Esperado:** O GUI não crasha nem congela.
- **Verifica:** A aplicação permanece responsiva.

### G19 - Confirmação de termino de jogo
- **Passos:** Completar uma partida até declaração de vencedor.
- **Esperado:** A interface apresenta claramente o vencedor e impede novos movimentos.
- **Verifica:** O fluxo de fim de jogo é suportado sem erros.

### G20 - Responsividade e atualizações de labels
- **Passos:** Observar labels de turno, tempo e dificuldade durante o jogo.
- **Esperado:** Essas labels atualizam sempre que o estado muda.
- **Verifica:** Informações da interface estão sincronizadas com o estado interno.

- **Passos:** Jogar algumas jogadas, digitar "R".
- **Esperado:** Novo tabuleiro, mesmas configuracoes.

### T7.9 - Opcao [D]imensoes
- **Passos:** Digitar "D", inserir "8 8".
- **Esperado:** Novo jogo 8x8.

### T7.10 - Opcao [N]ivel
- **Passos:** Digitar "N", inserir "3".
- **Esperado:** Dificuldade atualizada para Dificil.

### T7.11 - Opcao [G]uardar
- **Passos:** Digitar "G".
- **Esperado:** "Jogo guardado em 'konane_save.txt'."

### T7.12 - Opcao [Q]uit
- **Passos:** Digitar "Q".
- **Esperado:** "A sair do jogo..." e retorno ao menu principal.

### T7.13 - Captura em cadeia: Continuar
- **Passos:** Apos primeiro salto, digitar "C", escolher destino.
- **Esperado:** Segundo salto executado.

### T7.14 - Captura em cadeia: Parar
- **Passos:** Apos primeiro salto, digitar "P".
- **Esperado:** Turno completo, computador joga.

### T7.15 - Input invalido no menu de jogo
- **Passos:** Digitar "X" ou texto aleatorio.
- **Esperado:** "Opcao invalida." e nova oportunidade.

---

## T8: GUI

### T8.1 - Tabuleiro 6x6 renderizado
- **Passos:** Iniciar GUI.
- **Esperado:** Tabuleiro 6x6 com pecas pretas e brancas, 2 posicoes vazias.

### T8.2 - Pecas moveis destacadas
- **Passos:** Observar tabuleiro no turno do humano.
- **Esperado:** Pecas que podem jogar destacadas a verde claro.

### T8.3 - Selecao de peca
- **Passos:** Clicar numa peca verde.
- **Esperado:** Peca fica amarela, destinos validos ficam verdes com indicador.

### T8.4 - Captura por clique
- **Passos:** Clicar num destino valido apos selecionar peca.
- **Esperado:** Captura executada, peca adversaria removida.

### T8.5 - Clique em peca invalida
- **Passos:** Clicar numa peca branca ou peca preta sem jogadas.
- **Esperado:** Selecao limpa, mensagem "Selecione uma peca (destacada em verde)."

### T8.6 - Computador joga automaticamente
- **Passos:** Apos jogada do humano.
- **Esperado:** Apos breve pausa (~500ms), computador faz jogada, tabuleiro atualiza.

### T8.7 - Botao Novo Jogo
- **Passos:** Clicar "Novo Jogo".
- **Esperado:** Tabuleiro reiniciado, status "Novo jogo iniciado!"

### T8.8 - Botao Guardar e Carregar
- **Passos:** Jogar, clicar "Guardar", clicar "Novo Jogo", clicar "Carregar".
- **Esperado:** Jogo restaurado ao estado guardado.

### T8.9 - ComboBox Dificuldade
- **Passos:** Mudar dificuldade para "Dificil".
- **Esperado:** Computador joga com minimax no proximo turno.

### T8.10 - ComboBox Tempo
- **Passos:** Mudar tempo para "15s".
- **Esperado:** Timer exibe contagem a partir de 15s.

### T8.11 - Botao Parar Captura
- **Passos:** Iniciar captura em cadeia, clicar "Parar Captura".
- **Esperado:** Turno termina, capturas feitas sao mantidas.

### T8.12 - Parar Captura sem cadeia ativa
- **Passos:** Clicar "Parar Captura" sem captura em cadeia.
- **Esperado:** Nada acontece (botao ignorado).

### T8.13 - Timer visual
- **Passos:** Observar sidebar durante turno do humano com tempo definido.
- **Esperado:** Contagem decrescente visivel a vermelho.

### T8.14 - Timer sem limite
- **Passos:** Selecionar "Sem limite" no ComboBox de tempo.
- **Esperado:** Timer desaparece ou mostra vazio.

---

## Persistencia

### P.1 - Save e Load completo
- **Passos:**
  1. Jogar 3 jogadas
  2. Guardar (TUI: "G", GUI: botao)
  3. Fechar programa
  4. Reabrir, carregar
- **Esperado:** Tabuleiro, jogador, tempo, dificuldade, historico undo restaurados.

### P.2 - Load ficheiro inexistente
- **Passos:** Tentar carregar sem ficheiro save.
- **Esperado:** Mensagem de erro, jogo nao afetado.

### P.3 - Load ficheiro corrompido
- **Passos:** Alterar manualmente o ficheiro konane_save.txt com dados invalidos.
- **Esperado:** `None` retornado, mensagem de erro.

### P.4 - Save sobrescreve ficheiro anterior
- **Passos:** Guardar, jogar mais, guardar novamente.
- **Esperado:** Segundo save substitui o primeiro.

---

## Niveis de Dificuldade

### D.1 - Facil (aleatorio)
- **Passos:** Jogar com dificuldade 1.
- **Esperado:** Computador faz jogadas aleatorias, facil de vencer.

### D.2 - Medio (greedy)
- **Passos:** Jogar com dificuldade 2.
- **Esperado:** Computador prefere capturas mais longas (mais pecas capturadas).

### D.3 - Dificil (minimax)
- **Passos:** Jogar com dificuldade 3.
- **Esperado:** Computador joga de forma estrategica, mais dificil de vencer.

### D.4 - Mudar dificuldade durante jogo (TUI)
- **Passos:** Digitar "N", escolher nova dificuldade.
- **Esperado:** Proximo turno do computador usa nova dificuldade.

### D.5 - Mudar dificuldade durante jogo (GUI)
- **Passos:** Alterar ComboBox de dificuldade.
- **Esperado:** Proximo turno do computador usa nova dificuldade.

---

## Edge Cases e Robustez

### E.1 - Tabuleiro minimo 4x4
- **Passos:** Criar jogo 4x4 via TUI.
- **Esperado:** Tabuleiro funcional, jogadas possiveis limitadas, fim de jogo rapido.

### E.2 - Tabuleiro maximo 12x12
- **Passos:** Criar jogo 12x12 via TUI.
- **Esperado:** Tabuleiro renderizado corretamente, jogo funcional.

### E.3 - Dificuldade Hard em tabuleiro grande
- **Passos:** Criar jogo 10x10 com dificuldade 3.
- **Esperado:** Computador joga (pode ser lento, mas nao deve crashar).

### E.4 - Undo imediatamente apos load
- **Passos:** Carregar jogo guardado, fazer undo.
- **Esperado:** Se havia historico no save, undo funciona. Se nao, "Nada para desfazer."

### E.5 - Multiplos saves sem jogar
- **Passos:** Guardar, guardar novamente sem jogar.
- **Esperado:** Funciona sem erro, ficheiro sobrescrito.

### E.6 - Jogar apos fim de jogo (GUI)
- **Passos:** Apos "JOGO TERMINADO", clicar no tabuleiro.
- **Esperado:** Cliques ignorados ou mensagem adequada.

### E.7 - Tempo 0 (sem limite)
- **Passos:** Configurar tempo = 0 na TUI ou "Sem limite" na GUI.
- **Esperado:** Sem contagem decrescente, jogador tem tempo ilimitado.

---

## Testes de Programacao Funcional

### FP.1 - Imutabilidade do Board
- **Verifica:** Apos `play(board, ...)`, o `board` original nao e alterado.
- **Como testar:** Guardar referencia ao board antes de play, comparar apos.

### FP.2 - Pureza de randomMove
- **Verifica:** Mesma seed produz mesmo resultado.
- **Como testar:** `randomMove(list, MyRandom(42))` chamado duas vezes deve retornar o mesmo.

### FP.3 - Pureza de play
- **Verifica:** Sem side effects. Mesmos inputs = mesmos outputs.
- **Como testar:** Chamar play com mesmos argumentos varias vezes.

### FP.4 - @tailrec correto
- **Verifica:** Funcoes com `@tailrec` compilam (anotacao causa erro de compilacao se nao for tail recursive).
- **Como testar:** O projeto compila sem erros.

### FP.5 - GameState imutavel
- **Verifica:** `GameState.copy(...)` cria nova instancia sem alterar a original.
- **Como testar:** Guardar referencia, fazer copy, comparar.

---

## Checklist do Professor

Testes que os professores provavelmente farao durante a avaliacao:

- [ ] Importar ZIP no IntelliJ sem erros
- [ ] `sbt run` compila e executa
- [ ] TUI: Novo jogo 6x6 funciona
- [ ] TUI: Jogada valida com captura
- [ ] TUI: Jogada invalida rejeitada
- [ ] TUI: Captura em cadeia (continuar + parar)
- [ ] TUI: Undo funciona
- [ ] TUI: Save e Load funciona
- [ ] TUI: Timer funciona
- [ ] TUI: Reiniciar, alterar dimensoes, nivel
- [ ] GUI: Tabuleiro 6x6 renderiza corretamente
- [ ] GUI: Selecao por clique com feedback visual
- [ ] GUI: Captura funciona
- [ ] GUI: Computador joga automaticamente
- [ ] GUI: Undo funciona
- [ ] GUI: Save/Load funciona
- [ ] GUI: Timer visual funciona
- [ ] Chamar randomMove diretamente
- [ ] Chamar play diretamente
- [ ] Chamar playRandomly diretamente
- [ ] Verificar imutabilidade
- [ ] Verificar assinaturas conforme enunciado
- [ ] Verificar uso de pattern matching
- [ ] Verificar uso de recursividade
- [ ] Verificar uso de higher-order functions
- [ ] Perguntas sobre decisoes de implementacao
