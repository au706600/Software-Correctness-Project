// #Sireum #Logika
// @Logika: --background save
import org.sireum._


def runtimeAssert3(label: String, expected: MSZ[(Z, Z)], actual: MSZ[(Z, Z)]): Unit = {
  print("Test: '")
  print(label)
  if (expected == actual) {
    println("' passed")
  } else {
    println("' Failed")
    print("expected: ")
    println(expected)
    print("got:      ")
    println(actual)
    println()
  }
}


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
  Contract(
    Requires(x1 <= x2, y1 <= y2),
  )
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


@pure def drawRectangle(x1: Z, y1: Z, x2: Z, y2: Z): MSZ[(Z, Z)] = {
  Contract(
    // The insures contract is here being accepted even if it is changed to something that is clearly not correct.
    // It might be because the strictpure functions have no specified contract.
    Ensures(Res[MSZ[(Z, Z)]] == drawRectangleRec(x1, y1, x2, y2))
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

//println(s"${drawRectangle(2,2,3,4)}")
//println(s"${drawRectangleRec(2,2,3,3)}")

runtimeAssert3(
  "drawRectangleRec()",
  MSZ[(Z, Z)]((2,2), (3,2), (2,3), (3,3), (2,4), (3,4)),
  drawRectangleRec(2,2,3,4))

runtimeAssert3(
  "drawRectangle()",
  MSZ[(Z, Z)]((2,2), (3,2), (2,3), (3,3), (2,4), (3,4)),
  drawRectangle(2,2,3,4))


// -----------------------------------------------------------------------------------------------------------------------
// Command line verification change the 'C:\Applications\Sireum\bin\sireum.jar' to your installation path.
// -----------------------------------------------------------------------------------------------------------------------

// java --enable-native-access=ALL-UNNAMED -jar C:\Applications\Sireum\bin\sireum.jar logika verifier .\src\project\drawRectangle.sc
// ----- stdout ------------------------------------------------------------------------------
// Logika verified! Verification time: 11.292s, Elapsed time: 11.312s


// java -jar C:\Applications\Sireum\bin\sireum.jar slang run .\src\project\drawRectangle.sc
// ----- stdout ------------------------------------------------------------------------------
// WARNING: Unknown module: javafx.graphics specified to --enable-native-access
// WARNING: Unknown module: javafx.media specified to --enable-native-access
// Test: 'drawRectangleRec()' passed
// Test: 'drawRectangle()' passed