import scala.collection.mutable.Buffer
import scala.collection.mutable.Map
import java.awt.Color








def drawRectangle_prof(command: Command, p1: IntPoint, p2: IntPoint): Map[(Int, Int), Color] =
    // Empty map with a tuple with x,y ordinates as the key and the color as it value.
    var pixels = Map[(Int, Int), Color]()
    
    // The type checker can verify that all variant are handle
    val color = command match
        case Command.draw(color) => color
        case Command.fill(color) => color
    
    // The type checker can verify that all variant are handle
    val fill = command match
        case Command.draw(_) => false
        case Command.fill(_) => true
    
    // The -1 and 1 en the following lines is pixel adjust in order adjust the line pixel width and displacement.
    val offset = lineWidthInPixels - 1
    // Ensures xMin and yMin to be the min values and
    // Ensures xMax and yMax to be the max values
    // And adjust with the half width on both sides in order to have the width of the lines equally on both sides of the specified boundary coordinates.
    val xMin = math.min(p1.x, p2.x) - lineWidthInPixels / 2 + 1
    val xMax = math.max(p1.x, p2.x) + lineWidthInPixels / 2 - 1
    val yMin = math.min(p1.y, p2.y) - lineWidthInPixels / 2 + 1
    val yMax = math.max(p1.y, p2.y) + lineWidthInPixels / 2 - 1 

    // As for loops is used instead of a while loop the termination is guaranteed.
    for(x <- xMin to xMax)
        for(y <- yMin to yMax)
            val isActive =
                fill ||
                x <= xMin + offset || x >= xMax - offset ||
                y <= yMin + offset || y >= yMax - offset

            if(isActive)
                pixels((x, y)) = color
                
    pixels

