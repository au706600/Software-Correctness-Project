/*
@main def _main(): Unit = {
  println("----------------------------------------------------")
  parse(PixelsCanvas(),
    """
      (BOUNDING-BOX (1 2) (3 4))

      (DRAW green
            (RECTANGLE (1 2) (3 4))
            (DRAW yellow
                  (LINE (5 6) (7 8)))
            (FILL blue
                  (CIRCLE (1 2) 3))
            (TEXT-AT (9 10) Hallo))
      
      (LINE (10 11) (12 13))
    """
  , defaultCommand=Command.draw(colorToValueNoCheck("green")))
  println("----------------------------------------------------")
}
*/
// (BOUNDING-BOX (1 2) (3 4))
// (LINE (10 11) (12 13))