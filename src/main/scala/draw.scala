object draw:
    type Pixels = List[List[Int]]
    def emptyPixels = List(List(0))


    def boundingBox(p1: Point, p2: Point): Pixels =
        val x0 = p1.x.round
        val y0 = p1.y.round
        val x1 = p2.x.round
        val y1 = p2.y.round

        val minX = Math.min(x0, x1)
        val maxX = Math.max(x0, x1)
        val minY = Math.min(y0, y1)
        val maxY = Math.max(y0, y1)

        val width = maxX + 1
        val height = maxY + 1
        val pixels = Array.fill(width)(Array.fill(height)(0))

        for
            x <- minX to maxX
            y <- minY to maxY
        do
            pixels(x)(y) = 1

        pixels.map(_.toList).toList



    def line(command: Command, p1: Point, p2: Point): Pixels =
        val x0 = p1.x.round
        val y0 = p1.y.round
        val x1 = p2.x.round
        val y1 = p2.y.round

        val maxX = Math.max(x0, x1)
        val maxY = Math.max(y0, y1)
        val pixels = Array.fill(maxX + 1)(Array.fill(maxY + 1)(0))

        var dx = Math.abs (x1-x0)
        var dy = Math.abs (y1-y0)

        var x = x0
        var y = y0

        pixels(x)(y) = 1

        if dx>=dy then {
            var p = ((2*dy) - dx)
            while (x!=x1){
                if (p<0){
                    x+=1
                    p = p + 2*dy
                }
                else{
                    x+=1    
                    y+=1    
                    p = p + 2*dy - 2*dx
                }
                if x >= 0 && x < pixels.length && y >= 0 && y < pixels(x).length then
                pixels(x)(y) = 1
            }
        }
        else{
            var p = ((2*dx) - dy)

            while (y!=y1){
                if (p<0){
                    y+=1
                    p= p + 2*dx
                }
                else{
                    x+=1
                    y+=1
                    p= p + 2*dx - 2*dy
                }
                pixels(x)(y) = 1
            }
        }
        pixels.map(_.toList).toList


    def rectangle(command: Command, p1: Point, p2: Point): Pixels =
        val x0 = p1.x.round
        val y0 = p1.y.round
        val x1 = p2.x.round
        val y1 = p2.y.round

        line(command, Point(x0,y0),Point(x0,y1))
        line(command, Point(x0,y1), Point(x1,y1))
        line(command, Point(x1,y1), Point(x1,y0))
        line(command, Point(x1,y0), Point(x0,y0))

    def circle(command: Command, p1: Point, r: Float): Pixels =
        emptyPixels

    def text(command: Command, p1: Point, text: String): Pixels =
        emptyPixels
