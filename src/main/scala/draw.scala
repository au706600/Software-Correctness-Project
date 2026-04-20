import scala.collection.mutable.Buffer
import scala.collection.mutable.Map
import java.awt.Color



// Bresenham's line algorithm
def drawLine(command: Command, p1: IntPoint, p2: IntPoint): Map[(Int, Int), Color] = {
    var pixels = Map[(Int, Int), Color]()
    var count = 0
    val color = command match
        case Command.draw(color) => color
        case Command.fill(color) => color

    val x0 = p1.x.toInt
    val y0 = p1.y.toInt
    val x1 = p2.x.toInt
    val y1 = p2.y.toInt

    var dx = Math.abs(x1 - x0)
    var dy = Math.abs(y1 - y0)

    var x = x0
    var y = y0
    
    val xStep = if (x0 < x1) 1 else -1
    val yStep = if (y0 < y1) 1 else -1

    def thickPixels(px: Int, py: Int) = {
        val offset = lineWidthInPixels / 2
        for(dx <- -offset to offset; dy <- -offset to offset) {
            pixels((px + dx, py + dy)) = color
        }
    }

    if dx>=dy then {
        var p = (2*dy - dx)
        while (x != x1 && count < 1000){
            count += 1
            if (p < 0){
                x += xStep
                p = p + 2*dy
            }
            else {
                x += xStep    
                y += yStep
                p = p + 2*dy - 2*dx
            }
            thickPixels(x, y)            
        }
    
    } else{
        var p = (2*dx - dy)
        while (y != y1 && count < 1000){
            count += 1
            if (p < 0){
                y += yStep
                p = p + 2*dx
            }
            else {
                x += xStep
                y += yStep
                p = p + 2*dx - 2*dy
            }
            thickPixels(x, y)            
        }
    }
    pixels
}


def drawRectangle(command: Command, p1: IntPoint, p2: IntPoint): Map[(Int, Int), Color] =
    var pixels = Map[(Int, Int), Color]()

    val color = command match
        case Command.draw(color) => color
        case Command.fill(color) => color

    val fill = command match
        case Command.draw(_) => false
        case Command.fill(_) => true
    
    val offset = lineWidthInPixels - 1
    val xMin = math.min(p1.x, p2.x) - lineWidthInPixels / 2 + 1
    val xMax = math.max(p1.x, p2.x) + lineWidthInPixels / 2 - 1
    val yMin = math.min(p1.y, p2.y) - lineWidthInPixels / 2 + 1
    val yMax = math.max(p1.y, p2.y) + lineWidthInPixels / 2 - 1 

    for(x <- xMin to xMax)
        for(y <- yMin to yMax)
            val isActive =
                fill ||
                x <= xMin + offset || x >= xMax - offset ||
                y <= yMin + offset || y >= yMax - offset

            if(isActive)
                pixels((x, y)) = color
                
    pixels


def drawCircle(command: Command, p1: IntPoint, r: Int): Map[(Int, Int), Color] =
    // See: https://en.wikipedia.org/wiki/Midpoint_circle_algorithm
    // x^2+y^2==r^2
 
    var pixels = Map[(Int, Int), Color]()

    val color = command match
        case Command.draw(color) => color
        case Command.fill(color) => color

    def addPoint(x: Int, y: Int) =
        pixels((p1.x + x, p1.y + y)) = color

    def addPoints(x: Int, y: Int) =
        addPoint( x,  y)
        addPoint( x, -y)
        addPoint(-x,  y)
        addPoint(-x, -y)
        addPoint( y,  x)
        addPoint( y, -x)
        addPoint(-y,  x)
        addPoint(-y, -x)

    val r0 = r + lineWidthInPixels / 2 - 1

    def drawCircleEdge(rStart: Int) =
        for(r1 <- rStart*2 to r0*2)
            val r2 = r1*r1/4
            var x = r1/2
            var x2 = x*x
            var y = 0
            var y2 = y*y
            while(x >= y)
                addPoints(x, y)
                if(x2 + y2 > r2)
                    x -= 1 
                    x2 = x*x
                y2 = y*y
                y += 1

    command match
        case Command.draw(_) =>
            drawCircleEdge(r0 - lineWidthInPixels + 1)
        case Command.fill(_) =>
            for(i <- 0 to r0)
                drawCircleEdge(0)

    pixels

