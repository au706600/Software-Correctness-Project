import scala.collection.mutable.Buffer
import scala.collection.mutable.Map
import java.awt.Color


// Bresenham's line algorithm
def drawLine(command: Command, p1: IntPoint, p2: IntPoint): Map[(Int, Int), Color] = {
    val color = command match
        case Command.draw(color) => color
        case Command.fill(color) => color


    // case class IntPoint(x: Float, y: Float) extends IntPointResult
    val dx = p2.x.toInt - p1.x.toInt;
    val dy = p2.y.toInt - p1.y.toInt;
    var p_error = (2*dy) - dx;

    // Constant time appending and prepending
    //var IntPoints : scala.collection.mutable.Buffer[IntPoint]();
    var points = Map[(Int, Int), Color]();

    var startIntPoint = p1.x.toInt;
    var endIntPoint = p1.y.toInt;

    points((startIntPoint, endIntPoint)) = color;

    while(startIntPoint < p2.x.toInt || endIntPoint < p2.y.toInt)
    {
        if(p_error >= 0)
        {
            p_error = p_error + (2 * dy - 2 * dx);
            startIntPoint += 1;
            endIntPoint += 1;
        }

        else
        {
            p_error = p_error + (2*dy);
            startIntPoint += 1;
        }
        
        points((startIntPoint, endIntPoint)) = color;

    }

    points
}


def drawRectangle(command: Command, p1: IntPoint, p2: IntPoint): Map[(Int, Int), Color] =
    var pixels = Map[(Int, Int), Color]()

    val color = command match
        case Command.draw(color) => color
        case Command.fill(color) => color

    val fill = command match
        case Command.draw(_) => false
        case Command.fill(_) => true
    
    val offset = lineScaleFactor - 1
    val xMin = math.min(p1.x, p2.x)
    val xMax = math.max(p1.x, p2.x)
    val yMin = math.min(p1.y, p2.y)
    val yMax = math.max(p1.y, p2.y)
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

    def drawCircleEdge(rStart: Int) =
        for(r1 <- rStart*2 to r*2)
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
            drawCircleEdge(r - lineScaleFactor)
        case Command.fill(_) =>
            for(i <- 0 to r)
                drawCircleEdge(0)

    pixels



def drawText(command: Command, p1: IntPoint, text: String): Map[(Int, Int), Color] =
    Map() 


