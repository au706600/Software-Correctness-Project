// #Sireum #Logika
// @Logika: --background save
import org.sireum._


def runtimeAssert1(label: String, expected: MSZ[String], actual: MSZ[String]): Unit = {
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


def runtimeAssert2(label: String, expected: String, actual: String): Unit = {
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


@strictpure def stringToMSZHelper(text: String, index: Z): MSZ[String] = {
  if(index >= text.size) {
    MSZ[String]()
  } else {
    ops.StringOps(text).substring(index, index + 1) +: stringToMSZHelper(text, index + 1)
  }
}


@strictpure def stringToMSZ(text: String): MSZ[String] = {
  stringToMSZHelper(text, 0)
}


@strictpure def MSZToString(text: MSZ[String]): String  = {
  val first = head(text)
  if (first == "") {
    ""
  } else {
    val rest = MSZToString(tail(text))

    // Logika gives a warning: String interpolation is currently over-approximated to produce an unconstrained string
    s"${first}${rest}"
  }
}


@strictpure def sub(inData: MSZ[String], inFromIndex: Z, inToIndex: Z): MSZ[String] = {
  if (inData.isInBound(inFromIndex) & inFromIndex < inToIndex) {
    // When Using the "Logika check (All in file)" in the IVE (intellij idea) it gives the following error: Error encountered when deducing that the sequence indexing is in bound
    // When the in "Logika check (Line)" is used it gives no errors. It may be a time out error that can be corrected by changing some settings.
    inData(inFromIndex) +: sub(inData, inFromIndex + 1, inToIndex)
  } else {
    MSZ[String]()
  }
}


@strictpure def take(n: Z, inData: MSZ[String]): MSZ[String] = {
  // Contract(Ensures(Res.size == n))
  sub(inData, 0, n)
}


@strictpure def drop(n: Z, inData: MSZ[String]): MSZ[String] = {
  sub(inData, n, inData.size)
}


@strictpure def tail(inData: MSZ[String]): MSZ[String] = {
  drop(1, inData)
}


// assert(take(1, MSZ("1", "2")) == MSZ("1"))
// assert(sub(MSZ("1", "2", "3"), 0, 1) == MSZ("1"))
// assert(sub(MSZ("1", "2", "3"), 1, 2) == MSZ("2"))
// assert(sub(MSZ("1", "2", "3"), 2, 3) == MSZ("3"))
// assert(sub(MSZ("1", "2", "3"), 0, 2) == MSZ("1", "2"))
// assert(sub(MSZ("1", "2"), 0, 2) == MSZ("1", "2"))
// assert(tail(MSZ("1", "2")) == MSZ("2"))


@strictpure def spaceAlike(c: String): B = {
  (c == "\n" | c == "\r" | c == "\t")
}


@strictpure def head(text: MSZ[String]): String = {
  if (text.isInBound(0)) {
    // When Using the "Logika check (All in file)" in the IVE (intellij idea) it gives the following error: Error encountered when deducing that the sequence indexing is in bound
    // When the in "Logika check (Line)" is used it gives no errors. It may be a time out error that can be corrected by changing some settings.
    text(0)
  } else {
    ""
  }
}


@strictpure def removeCommentsHelper(first: String, rest: MSZ[String], commentActive: B): MSZ[String] = {
  if (first == "") {
    MSZ[String]()
  } else if (first == "\n") {
    removeCommentsHelper(head(rest), tail(rest), false)
  } else if (first == ";" | commentActive) {
    removeCommentsHelper(head(rest), tail(rest), true)
  } else {
    first +: removeCommentsHelper(head(rest), tail(rest), false)
  }
}


@strictpure def removeComments(inData: MSZ[String]): MSZ[String] = {
  removeCommentsHelper(head(inData), tail(inData), false)
}


@strictpure def simplifyLispHelper1(first: String, second: String, rest: MSZ[String], started: B): MSZ[String] = {
  if (first=="" | (first==" " & second=="")) {
    MSZ[String]()
  } else if (spaceAlike(first)) {
    simplifyLispHelper1(" ", second, rest, started)
  } else if (spaceAlike(second)) {
    simplifyLispHelper1(first, " ", rest, started)
  } else if ((!started & first==" ") | (first==" " & second==" ") | (first==" " & second==")")) {
    simplifyLispHelper1(second, head(rest), tail(rest), started)
  } else if (first=="(" & second==" ") {
    simplifyLispHelper1(first, head(rest), tail(rest), started)
  } else {
    first +: simplifyLispHelper1(second, head(rest), tail(rest), true)
  }
}


@strictpure def simplifyLisp(inData: String): String = {
  val data = removeComments(stringToMSZ(inData))
  MSZToString(simplifyLispHelper1(head(data), head(tail(data)), tail(tail(data)), false))
}


runtimeAssert2(
  "MSZToString()",
  "1234",
  MSZToString(MSZ[String]("1","2","34")))


runtimeAssert1(
  "removeComments()",
  stringToMSZ("1 2 3 4 5 6"),
  removeComments(stringToMSZ("1 2 3 4; comment 123 \n 5 6")))


runtimeAssert2(
  "simplifyLisp()",
  "(Fun1 (3 4 3) (Fun2 (4 5) (2 1)))",
  simplifyLisp(" \n ( Fun1 (   3  4 3) \r ; (comment 1 2 3) \n  (  Fun2  \t  ( 4  5 ) \n\r  (2 1)  ) \r )"))


// -----------------------------------------------------------------------------------------------------------------------
// Command line verification change the 'C:\Applications\Sireum\bin\sireum.jar' to your installation path.
// -----------------------------------------------------------------------------------------------------------------------

// java --enable-native-access=ALL-UNNAMED -jar C:\Applications\Sireum\bin\sireum.jar logika verifier .\src\project\simplifyLisp.sc
// ----- stdout ------------------------------------------------------------------------------
// - [63, 5] String interpolation is currently over-approximated to produce an unconstrained string
// - [63, 5] String interpolation is currently over-approximated to produce an unconstrained string
// - [63, 5] String interpolation is currently over-approximated to produce an unconstrained string
// - [63, 5] String interpolation is currently over-approximated to produce an unconstrained string
// ----- Note -------------------------------------------------------------------------------
// The bound errors describes in the 'sub' and 'head' function is not reported when checked from the command like.

// java -jar C:\Applications\Sireum\bin\sireum.jar slang run .\src\project\simplifyLisp.sc
// ----- stdout ------------------------------------------------------------------------------
// WARNING: Unknown module: javafx.graphics specified to --enable-native-access
// WARNING: Unknown module: javafx.media specified to --enable-native-access
// Test: 'MSZToString()' passed
// Test: 'removeComments()' passed
// Test: 'simplifyLisp()' passed