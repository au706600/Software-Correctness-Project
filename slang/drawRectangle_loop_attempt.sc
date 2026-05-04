// #Sireum #Logika
// @Logika: --background save
import org.sireum._


def runtimeAssert(label: String, expected: MSZ[(Z, Z)], actual: MSZ[(Z, Z)]): Unit = {
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


@pure def point(i1: Z, x1: Z, x2: Z, y1: Z): (Z, Z) = {
  Contract(
    Requires(
      x1 <= x2,
    ),
    Ensures(
      Res[(Z, Z)] == (x1 + i1 % (x2 - x1 + 1), y1 + i1 / (x2 - x1 + 1)),
    )
  )
  return (x1 + i1 % (x2 - x1 + 1), y1 + i1 / (x2 - x1 + 1))
}


@pure def drawRectangleRec(i1: Z, i2: Z, x1: Z, x2: Z, y1: Z, a: MSZ[(Z, Z)]): MSZ[(Z, Z)] = {
  Contract(
    Requires(
      0 <= i1,
      0 <= i2,
      i1 <= i2,
      x1 <= x2,
      a.size == i1,
      All(0 until i1)(j => point(j, x1, x2, y1) == a(j))
    ),
    Ensures(
      Res[MSZ[(Z, Z)]].size == i2,
      All(0 until i2)(j => point(j, x1, x2, y1) == Res[MSZ[(Z, Z)]](j))
    )
  )
  if(i1 == i2) {
    return a
  } else {
    return drawRectangleRec(i1 + 1, i2, x1, x2, y1, a ++ MSZ[(Z, Z)](point(i1, x1, x2, y1)))
  }
}


@pure def pixelsSize(x: Z, y: Z, x1: Z, x2: Z, y1: Z): Z = {
  Contract(
    Requires(
      x >= x1,
      y >= y1,
      x2 >= x1,
      0 <= (x - x1 + (y - y1) * (x2 - x1 + 1))
    ),
    Ensures(
      Res[Z] == (x - x1 + (y - y1) * (x2 - x1 + 1))
    )
  )
  return (x - x1 + (y - y1) * (x2 - x1 + 1))
}


@pure def drawRectangle(x1: Z, y1: Z, x2: Z, y2: Z): MSZ[(Z, Z)] = {
  Contract(
    Requires(
      x1 <= x2, y1 <=y2
    ),
    Ensures(
      Res[MSZ[(Z, Z)]].size == (x2 - x1 + 1) * (y2 - y1 + 1),
      Res[MSZ[(Z, Z)]] == drawRectangleRec(0, (x2 - x1 + 1) * (y2 - y1 + 1), x1, x2, y1, MSZ[(Z, Z)]())
    )
  )

  var pixels: MSZ[(Z, Z)] = MSZ[(Z, Z)]()
  var x: Z = x1
  var y: Z = y1
  
  while(y <= y2) {
    Invariant(
      Modifies(x, y, pixels),
      x >= x1,
      y >= y1,
      pixels.size == pixelsSize(x, y, x1, x2, y1),
      pixels == drawRectangleRec(
        0,
        pixelsSize(x, y, x1, x2, y1),
        x1,
        x2,
        y1,
        MSZ[(Z, Z)]()),
    )

    pixels = pixels :+ (x, y)
    if (x < x2) {
      x = x + 1
    } else {
      x = x1
      y = y + 1
    }
  }
  
  return pixels
}

// println(drawRectangle(-5,-3,7,4))


runtimeAssert(
  "point()",
  MSZ[(Z, Z)]((2,10), (3,10), (2,11)),
  MSZ[(Z, Z)](point(0, 2, 3, 10), point(1, 2, 3, 10), point(2, 2, 3, 10)))


runtimeAssert(
  "drawRectangleRec()",
  MSZ[(Z, Z)]((1,1), (2,1), (1,2), (2,2)),
  drawRectangleRec(0,4,1,2,1, MSZ[(Z, Z)]()))


runtimeAssert(
  "drawRectangle()",
  MSZ[(Z, Z)]((1,1), (2,1), (1,2), (2,2)),
  drawRectangle(1,1,2,2))


// -----------------------------------------------------------------------------------------------------------------------
// Command line verification change the 'C:\Applications\Sireum\bin\sireum.jar' to your installation path.
// -----------------------------------------------------------------------------------------------------------------------

// java --enable-native-access=ALL-UNNAMED -jar C:\Applications\Sireum\bin\sireum.jar logika verifier .\src\project\drawRectangle_loop_attempt.sc
// ----- stdout ------------------------------------------------------------------------------
// * file:///C:/temp/untitled3/src/project/drawRectangle_loop_attempt.sc
//   - [98, 29] Could not deduce that the postcondition holds
//   - [112, 19] Could not deduce that the loop invariant holds at the end of while-loop
// ----- Note -------------------------------------------------------------------------------
// The while loop invariant does not hold or Logika can not verify it holds.


// java  -jar C:\Applications\Sireum\bin\sireum.jar slang run .\src\project\drawRectangle_loop_attempt.sc
// ----- stdout ------------------------------------------------------------------------------
// WARNING: Unknown module: javafx.graphics specified to --enable-native-access
// WARNING: Unknown module: javafx.media specified to --enable-native-access
// Test: 'point()' passed
// Test: 'drawRectangleRec()' passed
// Test: 'drawRectangle()' passed