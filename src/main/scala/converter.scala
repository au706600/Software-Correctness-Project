
import java.{util => pixelspixelsToJava}

class PixelsCanvas extends Canvas:
    var pixelsCanvas = java.util.ArrayList[java.util.ArrayList[Integer]]()


    def initPixels(witht: Int, height: Int, colorValue: Int=0) =
        pixelsCanvas = pixelsToJava((
            for{i <- 0 until witht}
                yield {for{j <- 0 until height}
                    yield colorValue}.toList).toList)

    def setAllPixelsToColor(colorValue: Int) =
        for(i1 <- 0 until pixelsCanvas.size)
            var row = pixelsCanvas.get(i1)
            for(i2 <- 0 until row.size)
                row.set(i2, colorValue)

    def boundingBox(p1: Point, p2: Point): Unit =
        println(s"BOUNDING-BOX $p1 $p2")

    def line(command: Command, p1: Point, p2: Point): Unit =
        printCommand(command, s"draw $p1 $p2")

    def rectangle(command: Command, p1: Point, p2: Point): Unit =
        printCommand(command, s"rectangle $p1 $p2")

    def circle(command: Command, p1: Point, r: Float): Unit =
        printCommand(command, s"circle $p1 $r")

    def text(command: Command, p1: Point, text: String): Unit =
        printCommand(command, s"text $p1 $text")

    def printCommand(command: Command, str: String) =
        println(command match
            case Command.draw(color: String) => s"draw color $color $str"
            case Command.fill(color: String) => s"fill color $color $str")


object converter:
    var pixels = PixelsCanvas()

    def setBounds(witht: Int, height: Int, colorValue: Int=0): Unit =
        pixels.initPixels(witht, height, colorValue)

    def convert(comandoes: String): ResultFromScala =
        // pixels.claarPixels()
        parse(pixels, comandoes, Command.draw("black")) match
            case Error(msg) => ResultFromScala(pixels.pixelsCanvas, msg)
            case NoError() => ResultFromScala(pixels.pixelsCanvas, "")
        
    //def foo(): Unit =
    //    println("scala foo called")
    //    
    //def descriptionToPixels(description: String): ResultFromScala =
    //    val len = description.length
    //    val pixels = (for{i <- 0 until len} yield {for{j <- 0 until len} yield 1}.toList).toList
    //    ResultFromScala(
    //        pixelsToJava(pixels),
    //        description)
    

def pixelsToJava(pixels: List[List[Int]]) =
    var out = java.util.ArrayList[java.util.ArrayList[Integer]]()
    for(row <- pixels)
        var javaRow = java.util.ArrayList[Integer]()
        for(pixel <- row)
            javaRow.add(pixel)
        out.add(javaRow)
    out
