val scala3Version = "3.8.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "testproject",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,
    fork := true,
    javaOptions ++= Seq(
      "-Xms1G",
      "-Xmx4G"
    )
  )
  
scalacOptions ++= Seq(
  "-feature",
  "-deprecation"
)

