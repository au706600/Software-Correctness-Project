//import java.awt.Point
import scala.collection.mutable.ListBuffer

object draw
{
    type Pixels = List[List[Int]]
    def enptyPixels = List(List(0))

    def boundingBox(p1: Point, p2: Point): Pixels =
        enptyPixels

    // Bresenham's line algorithm
    def line(command: Command, p1: Point, p2: Point): Pixels = {
        // case class Point(x: Float, y: Float) extends PointResult
        val dx = p2.x.toInt - p1.x.toInt;
        val dy = p2.y.toInt - p1.y.toInt;
        var p_error = (2*dy) - dx;

        // Constant time appending and prepending
        //var points : scala.collection.mutable.ListBuffer[Point]();
        var points = ListBuffer[Point]();

        var startPoint = p1.x.toInt;
        var endPoint = p1.y.toInt;

        points += Point(startPoint.toFloat, endPoint.toFloat);

        while(startPoint < p2.x.toInt || endPoint < p2.y.toInt)
        {
            if(p_error >= 0)
            {
                p_error = p_error + (2 * dy - 2 * dx);
                startPoint += 1;
                endPoint += 1;
            }

            else
            {
                p_error = p_error + (2*dy);
                startPoint += 1;
            }
            
            points += Point(startPoint.toFloat, endPoint.toFloat);

        }

        // Returns List[Point], so should return List[List[Int]]
        points.toList.map(p => List(p.x.toInt, p.y.toInt)) 
        }

    def rectangle(command: Command, p1: Point, p2: Point): Pixels =
        enptyPixels

    def circle(command: Command, p1: Point, r: Float): Pixels =
        enptyPixels

    def text(command: Command, p1: Point, text: String): Pixels =
        enptyPixels 
}

