// #Sireum #Logika
// @Logika: --background save
import org.sireum._
import org.sireum.justification._
import org.sireum.justification.natded.prop._
import org.sireum.justification.natded.pred._


@strictpure def min(a: Z, b: Z): Z = {
  if(a < b) {
    a
  } else {
    b
  }
}

@strictpure def max(a: Z, b: Z): Z = {
  if(a > b) {
    a
  } else {
    b
  }
}

@strictpure def drawRectangleRecHelper(x: Z, y: Z, x1: Z, y1: Z, x2: Z, y2: Z): MSZ[(Z, Z)] = {
  if(x == x2 & y == y2) {
    MSZ[(Z, Z)]((x, y))
  }else if(x < x2) {
    (x, y) +: drawRectangleRecHelper(x+1, y, x1, y1, x2, y2)
  } else {
    (x, y) +: drawRectangleRecHelper(x1, y+1, x1, y1, x2, y2)
  }
}

@strictpure def drawRectangleRec(x1: Z, y1: Z, x2: Z, y2: Z): MSZ[(Z, Z)] = {
  val xMin = min(x1, x2)
  val xMax = max(x1, x2)
  val yMin = min(y1, y2)
  val yMax = max(y1, y2)
  Deduce(|- (xMin <= xMax))
  Deduce(|- (yMin <= yMax))
  drawRectangleRecHelper(xMin, yMin, xMin, yMin, xMax, yMax)
}


@strictpure def drawRectangleRecHelperInv(xs: Z, ys: Z, x: Z, y: Z, x1: Z, y1: Z, x2: Z, y2: Z): MSZ[(Z, Z)] = {
  if(x == xs & y == ys) {
    MSZ[(Z, Z)]()
  }else if(x == x2 & y == y2) {
    MSZ[(Z, Z)]((x, y))
  }else if(x < x2) {
    (x, y) +: drawRectangleRecHelperInv(xs, ys, x+1, y, x1, y1, x2, y2)
  } else {
    (x, y) +: drawRectangleRecHelperInv(xs, ys, x1, y+1, x1, y1, x2, y2)
  }
}

@strictpure def drawRectangleRecInv(xs: Z, ys: Z, x1: Z, y1: Z, x2: Z, y2: Z): MSZ[(Z, Z)] = {
  val xMin = min(x1, x2)
  val xMax = max(x1, x2)
  val yMin = min(y1, y2)
  val yMax = max(y1, y2)
  Deduce(|- (xMin <= xMax))
  Deduce(|- (yMin <= yMax))
  drawRectangleRecHelperInv(xs, ys, xMin, yMin, xMin, yMin, xMax, yMax)
}


@pure def drawRectangle(x1: Z, y1: Z, x2: Z, y2: Z): MSZ[(Z, Z)] = {
  Contract(
    // Requires(),
    // Ensures(Res[MSZ[(Z, Z)]] == drawRectangleRecInv(x2-1, y2+1, x1, y1, x2, y2))
  )
  val xMin = min(x1, x2)
  val xMax = max(x1, x2)
  val yMin = min(y1, y2)
  val yMax = max(y1, y2)
  Deduce(|- (xMin <= xMax))
  Deduce(|- (yMin <= yMax))

  var pixels: MSZ[(Z, Z)] = MSZ[(Z, Z)]()
  var x: Z = x1
  var y: Z = y1

  while(y <= yMax) {
    Invariant(
      Modifies(x, y, pixels),
      x >= xMin, x <= xMax + 1,
      y >= yMin, y <= yMax + 1,
      // pixels == drawRectangleRecInv(x, y, x1, y1, x2, y2),
    )
    val measure_pre_x = x
    val measure_pre_y = y
    pixels = pixels :+ (x, y)
    if (x < xMax) {
      x = x + 1
    } else {
      x = xMin
      y = y + 1
    }
    val measure_post_x = x
    val measure_post_y = y
    Deduce(|- (measure_post_x <= xMax + 1 & measure_post_y <= yMax + 1))
    Deduce(|- ((measure_post_x > measure_pre_x & measure_post_y == measure_pre_y) | measure_post_y > measure_pre_y))

  }

  return pixels
}
