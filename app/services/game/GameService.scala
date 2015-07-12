package services.game

import java.util.UUID

import models._
import models.rules.GameRulesSet
import models.rules.moves.InitialMoves
import models.user.PlayerRecord
import org.joda.time.LocalDateTime
import utils.DateUtils

case class GameService(
    id: UUID, rules: String, seed: Int, started: LocalDateTime, protected val player: PlayerRecord, protected val testGame: Boolean
) extends GameServiceHelper {
  log.info(s"Started game [$rules] for user [${player.userId}: ${player.name}] with seed [$seed].")

  protected[this] val observerConnections = collection.mutable.ArrayBuffer.empty[(PlayerRecord, Option[UUID])]

  protected[this] val gameRules = GameRulesSet.allByIdWithAliases(rules)
  protected[this] val gameState = gameRules.newGame(id, seed, rules)

  gameState.addPlayer(player.userId, player.name, player.autoFlipOption)

  protected[this] val gameMessages = collection.mutable.ArrayBuffer.empty[(GameMessage, UUID, LocalDateTime)]
  protected[this] var moveCount = 0
  protected[this] var firstMoveMade: Option[LocalDateTime] = None
  protected[this] var lastMoveMade: Option[LocalDateTime] = None
  protected[this] var gameWon = false

  protected[this] var autoFlipOption = false

  private[this] var _status = "started"
  protected[this] def getStatus = _status
  protected[this] def setStatus(s: String) {
    _status = s
    this.update()
  }

  override def preStart() {
    this.create()
    InitialMoves.performInitialMoves(gameRules, gameState)

    player.connectionActor.foreach(_ ! GameStarted(id, self, started))
    player.connectionActor.foreach(_ ! GameJoined(id, gameState.view(player.userId), 0, possibleMoves(Some(player.userId))))
  }

  override def receiveRequest = {
    case gr: GameRequest => handleGameRequest(gr)
    case im: InternalMessage => handleInternalMessage(im)
    case di: DebugInfo => handleCheat(di.data)
    case sp: SetPreference => handleSetPreference(sp.name, sp.value)
    case x => log.warn(s"GameService received unknown message [${x.getClass.getSimpleName}].")
  }

  private[this] def handleGameRequest(gr: GameRequest) = {
    //log.debug("Handling [" + gr.message.getClass.getSimpleName.replace("$", "") + "] message from user [" + gr.userId + "] for game [" + id + "].")
    try {
      val time = DateUtils.now
      gameMessages += ((gr.message, gr.userId, time))
      moveCount += 1
      if(firstMoveMade.isEmpty) {
        firstMoveMade = Some(time)
      }
      lastMoveMade = Some(time)
      update()
      gr.message match {
        case GetPossibleMoves => timeReceive(GetPossibleMoves) { handleGetPossibleMoves(gr.userId) }

        case sc: SelectCard => timeReceive(sc) { handleSelectCard(gr.userId, sc.card, sc.pile) }
        case sp: SelectPile => timeReceive(sp) { handleSelectPile(gr.userId, sp.pile) }
        case mc: MoveCards => timeReceive(mc) { handleMoveCards(gr.userId, mc.cards, mc.src, mc.tgt) }

        case Undo => timeReceive(Undo) { handleUndo(gr.userId) }
        case Redo => timeReceive(Redo) { handleRedo(gr.userId) }

        case r => log.warn(s"GameService received unknown game message [${r.getClass.getSimpleName.replace("$", "")}].")
      }
    } catch {
      case x: Exception =>
        log.error(s"Exception processing game request [$gr].", x)
        sender() ! ServerError(x.getClass.getSimpleName, x.getMessage)
    }
  }

  private[this] def handleInternalMessage(im: InternalMessage) = {
    //log.debug("Handling [" + im.getClass.getSimpleName.replace("$", "") + "] internal message for game [" + id + "].")
    try {
      im match {
        case ap: AddPlayer => timeReceive(ap) { handleAddPlayer(ap.userId, ap.name, ap.connectionId, ap.connectionActor, ap.autoFlipOption) }
        case ao: AddObserver => timeReceive(ao) { handleAddObserver(ao.userId, ao.name, ao.connectionId, ao.connectionActor, ao.as) }
        case cs: ConnectionStopped => timeReceive(cs) { handleConnectionStopped(cs.connectionId) }
        case StopGame => timeReceive(StopGame) { handleStopGame() }
        case StopGameIfEmpty => timeReceive(StopGameIfEmpty) { handleStopGameIfEmpty() }
        case gt: GameTrace => timeReceive(gt) { handleGameTrace() }
        case _ => log.warn(s"GameService received unhandled internal message [${im.getClass.getSimpleName}].")
      }
    } catch {
      case x: Exception => log.error(s"Exception processing internal message [$im].", x)
    }
  }
}
