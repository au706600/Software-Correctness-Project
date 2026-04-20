
import collection.mutable._
import scala.jdk.CollectionConverters._
import scala.util.chaining.*
import java.util.ArrayList
import java.awt.Color


def stringToColor(color: String): ErrorResult =
    def parseSingleChar(c: Char): Int =
        Integer.parseInt(c.toString, 16)

    def parseCharPair(c1: Char, c2: Char): Int =
        parseSingleChar(c1)*16+parseSingleChar(c2)

    color.toArray match
        case Array(_, r1, r2, g1, g2, b1, b2) =>
            Result(Color(
                parseCharPair(r1, r2),
                parseCharPair(g1, g2),
                parseCharPair(b1, b2)))

        case _: Array[Char] =>
            Error(s"stringToColor() can not '${color}' convert to color")


def colorToValueHelper(options: List[(String,String,String)], color: String): ErrorResult =
    val colorLow = color.toLowerCase()
    
    val selected = options.filter((long, short, _) => List(long.toLowerCase(), short.toLowerCase()).contains(colorLow))

    if selected.length > 0 then
        stringToColor(selected.head.last)
    else
        stringToColor(colorLow)
        

def colorToValue(color: String): ErrorResult =
    colorToValueHelper(
        List(("black", "k", "#000000"),
             ("white", "w", "#ffffff"),
             ("gray", "w", "#999999"),
             ("red", "r", "#d90000"),
             ("blue", "b", "#0000ff"),
             ("green", "g", "#0aff00"),
             ("yellow", "y", "#ffff00")),
        color)


def colorToValueNoCheck(color: String): Color =
    colorToValue(color).asInstanceOf[Result].res


enum Command:
    case draw(color: Color) 
    case fill(color: Color)


sealed trait ParseSingleResult
sealed trait PointsResult
sealed trait PointResult
sealed trait ErrorResult
sealed trait ErrorOption

case class SingleResult(args: List[String], rest: String) extends ParseSingleResult

case class IntPoint(x: Int, y: Int)

case class Point(x: Float, y: Float) extends PointResult {
    def scale(factor: Int) =
        IntPoint((x*factor).round, (y*factor).round)
}
case class Points(p1: Point, p2: Point) extends PointsResult
case class Error(msg: String) extends PointsResult with PointResult with ErrorResult with ErrorOption with ParseSingleResult
case class Result(res: Color) extends ErrorResult
case class NoError() extends ErrorOption


trait Canvas:
    def boundingBox(p1: Point, p2: Point, cmdId: Int): Unit
    def line(command: Command, p1: Point, p2: Point, cmdId: Int): Unit
    def rectangle(command: Command, p1: Point, p2: Point, cmdId: Int): Unit
    def circle(command: Command, p1: Point, r: Float, cmdId: Int): Unit
    def text(command: Command, p1: Point, text: String, cmdId: Int): Unit


def simplifyLisp(str: String): String =     
    str
       .replace("\r", "")
       .split("\n").map(s => s.split(";", 2).head).mkString(" ") // remove single line Comments
       .replace("\n", " ")
       .replace("\t", " ")
       .pipe(Iterator.iterate(_))(_.replace("  ", " "))
       .dropWhile(_.contains("  "))
       .next
       .replace("( ", "(")
       .replace(" )", ")")
       .trim()


def parse(canvas: Canvas, str: String, defaultCommand: Command): ErrorOption =
    parseSingle(simplifyLisp(str)) match
        case Error(msg) => Error(msg)
        case SingleResult(args, rest) =>
            if args.length < 3 then
                Error("Error missing arguments")
            else
                parsePoint(args(1), args(2)) match
                    case Error(msg) => Error(msg)
                    case Points(p1, p2) =>
                        canvas.boundingBox(p1, p2, 0)
                        if args.head.toUpperCase != "BOUNDING-BOX" || args.length != 3 then
                            Error(s"parse() error: First commando is not a BOUNDING-BOX with 2 args: '$args'")
                        else
                            parseRest(canvas, defaultCommand, rest, 1, true)


def parseSingle(str: String): ParseSingleResult =
    var args: ArrayBuffer[String] = ArrayBuffer()
    var arg: String = ""
    var count = 0
    val res = str.takeWhile(c =>
        c match
            case '(' =>
                count += 1
                if count > 1 then
                    arg += c
            case ')' =>
                if count == 1 then
                    args.addOne(arg)
                    arg = ""
                count -= 1
                arg += c
            case ' ' if count == 1 =>
                args.addOne(arg)
                arg = ""
            case _ =>
                arg += c
        count > 0)
    
    if count != 0  then
        Error(s"parseSingle() error: str: '$str', res: '$res', args: '$args', count: '$count'")
    else
        SingleResult(args.toList, str.drop(res.length+1).trim())


enum LevelCmd:
    case newLevel()
    case continueFrom(value: Int)


def parseRest(canvas: Canvas, command: Command, rest: String, cmdId: Int, firstLevel: Boolean): ErrorOption =
    var mutRest = rest
    var restCount = cmdId
    while mutRest.length > 0 do 
        parseSingle(mutRest) match
            case Error(msg) => return Error(msg)
            case SingleResult(args, rest_) =>
                val nexCndId = if firstLevel then restCount else cmdId
                mutRest = rest_
                if args.isEmpty then
                    return Error("No args 1")
                else
                    upperFirstElement(args) match
                        case "COMMENT"::_ =>
                        case _ =>
                            restCount += 1
                            parseMatch(canvas, command, args, nexCndId) match
                                case Error(msg) => return Error(msg)
                                case _ =>
    NoError()


def upperFirstElement(args: List[String]) =
    List(args.head.toUpperCase()) ++ args.tail

def parseMatch(canvas: Canvas, command: Command, args: List[String], cmdId: Int): ErrorOption =
    if args.isEmpty then
        Error("No args 2")
    else
        upperFirstElement(args) match
            case List("LINE", p1, p2) => parseLine(canvas, command, p1, p2, cmdId)
            case List("RECTANGLE", p1, p2) => parseRectangle(canvas, command, p1, p2, cmdId)
            case List("CIRCLE", p1, r) => parseCircle(canvas, command, p1, r, cmdId)
            case "TEXT-AT" :: p1 :: text => parseText(canvas, command, p1, text.mkString(" "), cmdId)
            case List("FILL", c, g) =>
                colorToValue(c) match
                    case Error(msg) => Error(msg)
                    case Result(res) => parseFill(canvas, Command.fill(res), g, cmdId)
            case "DRAW" :: c :: args =>
                colorToValue(c) match
                    case Error(msg) => Error(msg)
                    case Result(res) => parseDraw(canvas, Command.draw(res), args, cmdId)
            case _ => Error("parseMatch() error: " + args.toString)
    

def parseLine(canvas: Canvas, command: Command, p1: String, p2: String, cmdId: Int): ErrorOption =
    parsePoint(p1, p2) match
        case Error(msg) => Error(msg)
        case Points(p1, p2) => canvas.line(command, p1, p2, cmdId); NoError()


def parseRectangle(canvas: Canvas, command: Command, p1: String, p2: String, cmdId: Int): ErrorOption =
    parsePoint(p1, p2) match
        case Error(msg) => Error(msg)
        case Points(p1, p2) => canvas.rectangle(command, p1, p2, cmdId); NoError()
    

def parseCircle(canvas: Canvas, command: Command, p1: String, r: String, cmdId: Int): ErrorOption =
    (parsePoint(p1), r.toFloatOption) match
        case (Error(msg), _) => Error(msg)
        case (_, None) => Error("parseCircle() error")
        case (Point(x, y), Some(r)) =>
            if r < 0 then
                Error("parseCircle() error because the radius argument is a negative number")
            else
                canvas.circle(command, Point(x, y), r, cmdId)
                NoError()

    
def parseText(canvas: Canvas, command: Command, p1: String, text: String, cmdId: Int): ErrorOption =
    parsePoint(p1) match
        case Point(x, y) => canvas.text(command, Point(x, y), text, cmdId); NoError()
        case Error(msg) => Error(msg)
    

def parseFill(canvas: Canvas, command: Command, g: String, cmdId: Int): ErrorOption =
    parseSingle(g) match
        case SingleResult(args, rest) => parseMatch(canvas, command, args, cmdId)
        case Error(msg) => Error(msg)


def parseDraw(canvas: Canvas, command: Command, args: List[String], cmdId: Int): ErrorOption =
    args.map(parseRest(canvas, command, _, cmdId, false))
        .collectFirst{case Error(msg) => Error(msg)}
        .getOrElse(NoError())


def parsePoint(p1: String): PointResult =
    parseSingle(p1) match
        case Error(msg) => Error(msg)
        case SingleResult(args, rest) =>
            if rest.nonEmpty then
                Error(s"parsePoint() called with arg $p1: error because of rest.nonEmpty")
            else if args.length != 2 then
                Error(s"parsePoint() called with arg $p1: error because of args.length != 2")
            else
                (args.head.toFloatOption, args.last.toFloatOption) match
                    case (Some(x), Some(y)) => Point(x, y)
                    case _ => Error(s"parsePoint() called with arg $p1: error because not able to parse to float")


def parsePoint(arg1: String, arg2: String): PointsResult =
    (parsePoint(arg1), parsePoint(arg2)) match
        case (Error(msg), _) => Error(msg)
        case (_, Error(msg)) => Error(msg)
        case (p1 @ Point(x1, y1), p2 @ Point(x2, y2)) => 
            Points(p1, p2)