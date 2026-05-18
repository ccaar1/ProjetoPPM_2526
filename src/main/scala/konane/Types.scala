package konane

import scala.collection.parallel.immutable.ParMap //mapa para otimizado para collections , requerido para o ParMap do board

// data types requeridos no enunciado
type Coord2D = (Int, Int) // (row, column)
type Board = ParMap[Coord2D, Stone] //mapas guardam valores/objetos em posicoes. ex. uma peca preta na posicao 1,2

// Enum em scala, modern adts
enum Stone: //algebraic data type, definimos fixed set values
  case Black, White

  def opponent: Stone = this match
    case Stone.Black => Stone.White
    case Stone.White => Stone.Black

  def display: String = this match //tui, como as stones aparecem
    case Stone.Black => "B"
    case Stone.White => "W"
