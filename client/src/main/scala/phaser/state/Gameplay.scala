package phaser.state

import com.definitelyscala.phaser.{PhysicsObj, State}
import phaser.PhaserGame
import phaser.card.CardImages
import settings.PlayerSettings

import scala.scalajs.js.annotation.ScalaJSDefined

@ScalaJSDefined
class Gameplay(g: PhaserGame, settings: PlayerSettings) extends State {
  override def preload() = {
    game.physics.startSystem(PhysicsObj.ARCADE)

    val images = new CardImages(g, settings)
  }

  override def create() = {
    utils.Logging.info("Running!")
  }
}
