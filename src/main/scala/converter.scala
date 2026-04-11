import scala.collection.mutable.Buffer
import java.awt.Color


val highlightColor = Color(220, 220, 220)
val scaleFactor = 2
val lineWidth = 2
val maxWidth = 1000
val maxHeight = 1000
val lineScaleFactor = scaleFactor 
val extraHIghlightPixels = scaleFactor



case class PixelsResult(pixels: Map[(Int, Int), Color], msg: String)

case class ResultHandle(warningMessages: Buffer[String]=Buffer()) {
    def addWarning(msg: String): Unit =
        warningMessages += s"warning: ${msg}"

    def error(msg: String): PixelsResult =
        PixelsResult(Map[(Int, Int), Color](), (warningMessages ++ Buffer(s"error: ${msg}")).mkString("\n"))

    def result(pixels: Map[(Int, Int), Color]): PixelsResult =
        PixelsResult(pixels, warningMessages.mkString("\n"))
}


case class Bounding(p1: IntPoint, p2: IntPoint) {
    def isInBound(x: Int, y: Int) =
        x >= p1.x && x <= p2.x &&
        y >= p1.y && y <= p2.y
}


case class CmdIdAndPixels(cmdId: Int, pixels: Map[(Int, Int), Color])


def mergePixelsCanvas(pixelsCanvas: Buffer[CmdIdAndPixels]): Map[(Int, Int), Color] =
    pixelsCanvas.map(d => d.pixels).fold(Map())((p1, p2) => p1 ++ p2)


def makeSquare(x1: Int, y1: Int, x2: Int, y2: Int, color: Color) =
    (for(x <- x1 to x2; y <- y1 to y2) yield {(x, y) -> color}).toMap


def countPixels(data: Buffer[CmdIdAndPixels]) =
    data.map(d => d.pixels.keys.size).fold(0)((s1, s2) => s1 + s2)


def filterPixels(pixels: Map[(Int, Int), Color], bounding: Bounding) = 
    pixels.filter((k, v) => bounding.isInBound(k._1, k._2))

def filterPixelsCanvas(data: Buffer[CmdIdAndPixels], bounding: Bounding) = 
    data.map(d => CmdIdAndPixels(d.cmdId, filterPixels(d.pixels, bounding)))


case class SplitByIdResult(cmdIdMatch: Buffer[CmdIdAndPixels], rest: Buffer[CmdIdAndPixels])
def splitById(data: Buffer[CmdIdAndPixels], cmdId: Int): SplitByIdResult =
    SplitByIdResult(
        data.filter((d) => d.cmdId == cmdId),
        data.filter((d) => d.cmdId != cmdId))

def getMatCmdId(data: Buffer[CmdIdAndPixels]): Int =
    data.map(d => d.cmdId).fold(data(0).cmdId)(math.max)

def getHighlight(data: Buffer[CmdIdAndPixels], resultHandle: ResultHandle):  Map[(Int, Int), Color] =
    var maxId = getMatCmdId(data)
    
    val maxIdCanvas = data.filter(d => d.cmdId == maxId)

    if countPixels(maxIdCanvas) == 0 then
        resultHandle.addWarning("No pixels in maxId")
        Map[(Int, Int), Color]()
    else
        val maxIdPixels = mergePixelsCanvas(maxIdCanvas)
        val maxIdPixelsX =  maxIdPixels.map((k,v) => k._1)
        val maxIdPixelsY =  maxIdPixels.map((k,v) => k._2)
        
        def foldPixel(data: Iterable[Int], fun: (Int, Int) => Int) =
            data.fold(data.head)(fun)

        makeSquare(
            foldPixel(maxIdPixelsX, math.min) - extraHIghlightPixels,
            foldPixel(maxIdPixelsY, math.min) - extraHIghlightPixels,
            foldPixel(maxIdPixelsX, math.max) + extraHIghlightPixels,
            foldPixel(maxIdPixelsY, math.max) + extraHIghlightPixels,
            highlightColor)


class PixelsCanvas extends Canvas:
    var pixelsCanvasArray = Buffer[CmdIdAndPixels]()
    var drawBoundingBox: Option[Bounding] = None


    def clearPixels() =
        pixelsCanvasArray.clear();
        var drawBoundingBox = None


    def mergePixels(): PixelsResult =
        var resultHandle = ResultHandle()
        
        val boundingBox = drawBoundingBox match
            case None => return resultHandle.error("BoundingBox not set")
            case Some(value) => value
        

        if pixelsCanvasArray.length == 0 then
            return resultHandle.error("No data exist")
        

        if countPixels(pixelsCanvasArray) == 0 then
            return resultHandle.error("No pixels exist")
       
       
        val inBoundPixelsCanvasArray = filterPixelsCanvas(pixelsCanvasArray, boundingBox)
        if countPixels(inBoundPixelsCanvasArray) == 0 then
            return resultHandle.error("No pixels in bound exist")
        

        val maxCanvasBounding = Bounding(IntPoint(0,0), IntPoint(maxWidth, maxHeight))
        val pixelsCanvas = filterPixelsCanvas(inBoundPixelsCanvasArray, maxCanvasBounding)

        if countPixels(pixelsCanvas) == 0 then
            return resultHandle.error("No pixels in max canvas bounding exist")

        var maxId = getMatCmdId(pixelsCanvas)
        val pixelsCanvases = splitById(pixelsCanvas, maxId)
        

        //show the highlight box first then others (highlight z=-1)
        //prevent hightlight hide other drawing
        val selected = mergePixelsCanvas(pixelsCanvases.cmdIdMatch)
        val highlightOnly = filterPixels(getHighlight(pixelsCanvases.cmdIdMatch, resultHandle), boundingBox)

        val finalPixels = highlightOnly ++ mergePixelsCanvas(pixelsCanvases.rest) ++ selected
        resultHandle.result(finalPixels)
        

    def boundingBox(p1: Point, p2: Point, cmdId: Int): Unit =
        drawBoundingBox = Some(Bounding(p1.scale(scaleFactor), p2.scale(scaleFactor)))


    def line(command: Command, p1: Point, p2: Point, cmdId: Int): Unit =
        pixelsCanvasArray += CmdIdAndPixels(cmdId, drawLine(command, p1.scale(scaleFactor), p2.scale(scaleFactor)).toMap)


    def rectangle(command: Command, p1: Point, p2: Point, cmdId: Int): Unit =
        pixelsCanvasArray += CmdIdAndPixels(cmdId, drawRectangle(command, p1.scale(scaleFactor), p2.scale(scaleFactor)).toMap)


    def circle(command: Command, p1: Point, r: Float, cmdId: Int): Unit =
        pixelsCanvasArray += CmdIdAndPixels(cmdId, drawCircle(command, p1.scale(scaleFactor), (r*scaleFactor).round).toMap)


    def text(command: Command, p1: Point, text: String, cmdId: Int): Unit =
        pixelsCanvasArray += CmdIdAndPixels(cmdId, drawText(command, p1.scale(scaleFactor), text).toMap)


object converter:
    var pixelsCanvas = PixelsCanvas()

    def convert(commandos: String): ResultFromScala =
        pixelsCanvas.clearPixels()
        val color = colorToValueNoCheck("black")
        parse(pixelsCanvas, commandos, Command.draw(color)) match
            case Error(msg) => ResultFromScala(toJavaArrayList(Map()), s"Error: ${msg}")
            case NoError()  =>
                val PixelsResult(pixels, msg) = pixelsCanvas.mergePixels()
                ResultFromScala(toJavaArrayList(pixels), msg)
        

def toJavaArrayList(pixels: Map[(Int, Int), Color]) =
    var out = java.util.ArrayList[JavaPixel]
    for(((x, y), color) <- pixels)
        out.add(JavaPixel(x, y, color))
    out