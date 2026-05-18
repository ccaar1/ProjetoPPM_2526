package konane

import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene

import javafx.fxml.FXMLLoader

object KonaneGUI extends JFXApp3:

  override def start(): Unit =

    val loader = new FXMLLoader(
      getClass.getResource("/konane.fxml")
    )

    val root = loader.load[javafx.scene.layout.BorderPane]()

    stage = new JFXApp3.PrimaryStage:

      title = "Konane"

      resizable = false

      scene = new Scene(root)