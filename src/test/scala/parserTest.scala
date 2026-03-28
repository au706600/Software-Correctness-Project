import scala.language.implicitConversions
import collection.mutable._
import scala.compiletime.ops.boolean
import java.awt.Color
import munit.Compare

class MySuite extends munit.FunSuite:

  test("simplifyLisp"):
    assertEquals(
      simplifyLisp(" \n (    Fun    x    y  ) \r "),
      "(Fun x y)")

      assertEquals(
      simplifyLisp(" \n (    Fun    ( 4  5 )    (2 1)  ) \r "),
      "(Fun (4 5) (2 1))")


  test("parseSingle"):
    assertEquals(
      parseSingle("(TEXT-AT (9 10) Hallo)"),
      SingleResult(List("TEXT-AT", "(9 10)", "Hallo"), ""))

    assertEquals(
      parseSingle("(Fun x y)"),
      SingleResult(List("Fun", "x", "y"), ""))

    assertEquals(
      parseSingle("(Fun x y) (123)"),
      SingleResult(List("Fun", "x", "y"), "(123)"))

    assertEquals(
      parseSingle("(BOUNDING-BOX (1 2) (3 4))"),
      SingleResult(List("BOUNDING-BOX", "(1 2)", "(3 4)"), ""))

    assertEquals(
      parseSingle("(LINE (1 2) (3 4))"),
      SingleResult(List("LINE", "(1 2)", "(3 4)"), ""))
      
    assertEquals(
      parseSingle("(FILL blue (RECTANGLE (1 2) (3 4)))"),
      SingleResult(List("FILL", "blue", "(RECTANGLE (1 2) (3 4))"), ""))


  test("parseRest"):
    var canvas = FakeCanvas()
    val result = parseRest(canvas, Command.draw(colorGreen), "(LINE (1 2) (3 4))", 1, true)
    assertEquals(result, NoError())
    assertEquals(canvas.asList, List("1 draw green line (1 2) (3 4)"))


  test("parse 1"):
    var canvas = FakeCanvas()
    val result = parse(canvas, "(BOUNDING-BOX (1 2) (3 4))", Command.draw(colorGreen))
    assertEquals(result, NoError())
    assertEquals(canvas.asList, List("0 boundingBox (1 2) (3 4)"))
 

  test("parse 2"):
    var canvas = FakeCanvas()
    val result = parse(canvas,
    """
      ; This is a comment
      (bOUNDING-BOX (1 2) (3 4))
      (COMMENt (CIRCLE (1 2) 3))
      ;;;; this is ;  a ; comment
      (DRaW green
            (RECTAnGLE (1 2) (3 4))
            (CIrCLE (1 2) 3))
    """, Command.draw(colorGreen))
    assertEquals(result, NoError())
    assertEquals(canvas.asList, List(
      "0 boundingBox (1 2) (3 4)",
      "1 draw green rectangle (1 2) (3 4)",
      "1 draw green circle (1 2) 3"
    ))


  test("parse 3"):
    var canvas = FakeCanvas()
    val result = parse(canvas,
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
    """,
    defaultCommand = Command.draw(colorBlack))

    assertEquals(result, NoError())
    assertEquals(canvas.asList, List(
      "0 boundingBox (1 2) (3 4)",
      "1 draw green rectangle (1 2) (3 4)",
      "1 draw yellow line (5 6) (7 8)",
      "1 fill blue circle (1 2) 3",
      "1 draw green text (9 10) Hallo",
      "2 draw black line (10 11) (12 13)",
    ))


  test("parse 4"):
    assertEquals(handleParse("(BOUNDING-BOX (1 2) (3 4))"), NoError())
    assertEquals(handleParse("(BOUNDING-BOX (1 2) (-3 4))"), NoError())
    assertEquals(handleParse("(BOUNDING-BOX (0 0) (3 4)) (LINE (10 11) (12 13))"), NoError())
    assertEquals(handleParse("(BOUNDING-BOX (1 2) (3 4) (5 6))"), TestError())
    assertEquals(handleParse("(BOUNDING-BOX (1 2) (3 4)) (CIRCLE (1 2) 3)"), NoError())
    assertEquals(handleParse("(BOUNDING-BOX (1 2) (3 4)) (CIRCLE (1 2) -3)"), TestError())
    assertEquals(handleParse("(LINE (10 11) (12 13))"), TestError())
    

  test("stringToColor"):
    val options = List(
      ("black", "k", "#000000"),
      ("white", "w", "#ffffff"),
      ("red", "r", "#d90000"),
      ("blue", "b", "#0000ff"),
      ("green", "g", "#0aff00"),
      ("yellow", "y", "#ffff00"))

    assertEquals(colorToValueHelper(options, ""), TestError())
    assertEquals(colorToValueHelper(options, "black"), Result(Color(0,0,0)))
    assertEquals(colorToValueHelper(options, "yellow"), Result(Color(255,255,0)))
    assertEquals(colorToValueHelper(options, "yElloW"), Result(Color(255,255,0)))
    assertEquals(colorToValueHelper(options, "#ffff00"), Result(Color(255,255,0)))


def handleParse(str: String): ErrorOption =
  var canvas = FakeCanvas()
  parse(canvas, str, Command.draw(colorGreen))


case class CanvasTestData(name: String, command: Option[Command], point1: Option[Point], point2: Option[Point], r: Option[Float], text: Option[String], cmdId: Option[Int]):
  def floatToString(value: Float): String =
      if value == value.round.toFloat then
        value.round.toString
      else
        value.toString

  def asString =
    var out = ""
    
    def handle(point: Option[Point | String | Float | Int]) =
      point match
        case Some(value) =>
          if out.nonEmpty then
            out += " "  
          out += (value match
            case Point(x, y) => "("+floatToString(x)+" "+floatToString(y)+")"
            case str: String => str
            case value: Float => floatToString(value)
            case value: Int => value.toString())
        case None =>
    
    def handleCommand(command: Option[Command]) =
      command match
          case Some(Command.draw(color)) => handle(Some(s"draw ${colorToString(color)}")) 
          case Some(Command.fill(color)) => handle(Some(s"fill ${colorToString(color)}")) 
          case None =>
    
    handle(cmdId)
    handleCommand(command)
    handle(Some(name))
    handle(point1)
    handle(point2)
    handle(r)
    handle(text)
    out
      

class FakeCanvas extends Canvas:
    var data = ArrayBuffer[CanvasTestData]()

    def asList =
      data.map(f => f.asString).toList
    
    def add(name: String, command: Option[Command]=None, p1: Option[Point]=None, p2: Option[Point]=None, r: Option[Float]=None, text: Option[String]=None, cmdId: Option[Int]=None) =
      data.addOne(CanvasTestData(name, command, p1, p2, r, text, cmdId))

    def boundingBox(p1: Point, p2: Point, cmdId: Int): Unit =
        add("boundingBox", p1=Some(p1), p2=Some(p2), cmdId=Some(cmdId))

    def line(command: Command, p1: Point, p2: Point, cmdId: Int): Unit =
        add("line", command=Some(command), p1=Some(p1), p2=Some(p2), cmdId=Some(cmdId))

    def rectangle(command: Command, p1: Point, p2: Point, cmdId: Int): Unit =
        add("rectangle", command=Some(command), p1=Some(p1), p2=Some(p2), cmdId=Some(cmdId))

    def circle(command: Command, p1: Point, r: Float, cmdId: Int): Unit =
        add("circle", command=Some(command), p1=Some(p1), r=Some(r), cmdId=Some(cmdId))

    def text(command: Command, p1: Point, text: String, cmdId: Int): Unit =
        add("text", command=Some(command), p1=Some(p1), text=Some(text), cmdId=Some(cmdId))


case class TestError()


given Compare[ErrorOption, TestError] with
  override def isEqual(obtained: ErrorOption, expected: TestError): Boolean = {
    obtained match
      case Error(msg) => true
      case NoError() => false
  }
  

given Compare[ErrorResult, TestError] with
  override def isEqual(obtained: ErrorResult, expected: TestError): Boolean = {
    obtained match
      case Error(msg) => true
      case Result(res) => false
}

  
val colorBlack = colorToValueNoCheck("black")
val colorGreen = colorToValueNoCheck("green")


def colorToString(color: Color): String =
  List("black", "green", "yellow", "blue")
    .find(c => colorToValueNoCheck(c) == color)
    .getOrElse("-----")
  