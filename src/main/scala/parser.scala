
import collection.mutable._
import scala.jdk.CollectionConverters._
import scala.util.chaining.*


enum Command:
    case draw(color: String) 
    case fill(color: String)


sealed trait ParseSingleResult
sealed trait PointsResult
sealed trait PointResult
sealed trait ErrorResult

case class SingleResult(args: List[String], rest: String) extends ParseSingleResult
case class Point(x: Float, y: Float) extends PointResult
case class Points(p1: Point, p2: Point) extends PointsResult
case class Error(msg: String) extends PointsResult with PointResult with ErrorResult with ParseSingleResult
case class NoError() extends ErrorResult


trait Canvas:
    def boundingBox(p1: Point, p2: Point): Unit
    def line(command: Command, p1: Point, p2: Point): Unit
    def rectangle(command: Command, p1: Point, p2: Point): Unit
    def circle(command: Command, p1: Point, r: Float): Unit
    def text(command: Command, p1: Point, text: String): Unit


def simplifyLisp(str: String): String = 
    str.replace("\n", " ")
       .replace("\r", " ")
       .replace("\t", " ")
       .pipe(Iterator.iterate(_))(_.replace("  ", " "))
       .dropWhile(_.contains("  "))
       .next
       .replace("( ", "(")
       .replace(" )", ")")
       .trim()


def parse(canvas: Canvas, str: String, defaultCommand: Command): ErrorResult =
    parseSingle(simplifyLisp(str)) match
        case Error(msg) => Error(msg)
        case SingleResult(args, rest) =>
            parsePoint(args(1), args(2)) match
                case Error(msg) => Error(msg)
                case Points(p1, p2) =>
                    canvas.boundingBox(p1, p2)
                    if args.head != "BOUNDING-BOX" || args.length != 3 then
                        Error(s"parse() error: First commando is not a BOUNDING-BOX with 2 args: '$args'")
                    else
                        parseRest(canvas, defaultCommand, rest)


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


def parseRest(canvas: Canvas, command: Command, rest: String): ErrorResult =
    var mutRest = rest
    while mutRest.length > 0 do
        parseSingle(mutRest) match
            case Error(msg) => return Error(msg)
            case SingleResult(args, rest_) =>
                mutRest = rest_
                parseMatch(canvas, command, args) match
                    case Error(msg) => return Error(msg)
                    case _ =>
    NoError()


def parseMatch(canvas: Canvas, command: Command, args: List[String]): ErrorResult =
    args match
        case List("LINE", p1, p2) => parseLine(canvas, command, p1, p2)
        case List("RECTANGLE", p1, p2) => parseRectangle(canvas, command, p1, p2)
        case List("CIRCLE", p1, r) => parseCircle(canvas, command, p1, r)
        case List("TEXT-AT", p1, text) => parseText(canvas, command, p1, text)
        case List("FILL", c, g) => parseFill(canvas, Command.fill(c), g)
        case "DRAW" :: c :: args => parseDraw(canvas, Command.draw(c), args)
        case _ => Error("parseMatch() error: " + args.toString)
    

def parseLine(canvas: Canvas, command: Command, p1: String, p2: String): ErrorResult =
    parsePoint(p1, p2) match
        case Error(msg) => Error(msg)
        case Points(p1, p2) => canvas.line(command, p1, p2); NoError()


def parseRectangle(canvas: Canvas, command: Command, p1: String, p2: String): ErrorResult =
    parsePoint(p1, p2) match
        case Error(msg) => Error(msg)
        case Points(p1, p2) => canvas.rectangle(command, p1, p2); NoError()
    

def parseCircle(canvas: Canvas, command: Command, p1: String, r: String): ErrorResult =
    (parsePoint(p1), r.toFloatOption) match
        case (Error(msg), _) => Error(msg)
        case (_, None) => Error("parseCircle() error")
        case (Point(x, y), Some(r)) => 
            canvas.circle(command, Point(x, y), r)
            NoError()

    
def parseText(canvas: Canvas, command: Command, p1: String, text: String): ErrorResult =
    parsePoint(p1) match
        case Point(x, y) => canvas.text(command, Point(x, y), text); NoError()
        case Error(msg) => Error(msg)
    

def parseFill(canvas: Canvas, command: Command, g: String): ErrorResult =
    parseSingle(g) match
        case SingleResult(args, rest) => parseMatch(canvas, command, args)
        case Error(msg) => Error(msg)


def parseDraw(canvas: Canvas, command: Command, args: List[String]): ErrorResult =
    args.map(parseRest(canvas, command, _))
        .collectFirst{case Error(msg) => Error(msg)}
        .getOrElse(NoError())


def parsePoint(p1: String): PointResult =
    parseSingle(p1) match
        case Error(msg) => Error(msg)
        case SingleResult(args, rest) =>
            if rest.nonEmpty || args.length != 2 then
                Error(s"parsePoint() error 1: $p1")
            else
                (args.head.toFloatOption, args.last.toFloatOption) match
                    case (Some(x), Some(y)) => Point(x, y)
                    case _ => Error(s"parsePoint() error 2: $p1")
    

def parsePoint(arg1: String, arg2: String): PointsResult =
    (parsePoint(arg1), parsePoint(arg2)) match
        case (Error(msg), _) => Error(msg)
        case (_, Error(msg)) => Error(msg)
        case (p1 @ Point(x1, y1), p2 @ Point(x2, y2)) => 
            Points(p1, p2)
