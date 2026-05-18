val scala3Version = "3.3.4"

lazy val root = project
  .in(file("."))
  .settings(
    name := "Konane",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "1.2.0",
    libraryDependencies += "org.scalafx" %% "scalafx" % "21.0.0-R32",
    // JavaFX dependencies (needed since JavaFX 11+)
    libraryDependencies ++= {
      val javaFXVersion = "21.0.4"
      val osName = System.getProperty("os.name") match {
        case n if n.startsWith("Linux")   => "linux"
        case n if n.startsWith("Mac")     => "mac"
        case _                            => "win"
      }
      Seq("base", "controls", "graphics").map(m =>
        "org.openjfx" % s"javafx-$m" % javaFXVersion classifier osName
      )
    },

    javaOptions += "--enable-native-access=ALL-UNNAMED",
    fork := true,
    run / connectInput := true,
    Compile / mainClass := Some("konane.Main")
  )
