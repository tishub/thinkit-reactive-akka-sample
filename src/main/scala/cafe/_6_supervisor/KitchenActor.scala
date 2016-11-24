package cafe._6_supervisor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

// キッチンアクター
class KitchenActor(offset: Int) extends Actor with ActorLogging {
  import KitchenActor._

  val barista: ActorRef = context.actorOf(BaristaActor.props(offset), "baristaActor")
  val patissier: ActorRef = context.actorOf(PatissierActor.props(offset), "patissierActor")

  override def receive: Receive = {
    case DripCoffee(count) =>
      barista forward  BaristaActor.DripCoffee(count)
    case BakeCake(count) =>
      patissier forward  PatissierActor.BakeCake(count)
  }
  import akka.actor.OneForOneStrategy
  import akka.actor.SupervisorStrategy._
  import scala.concurrent.duration._
  import scala.language.postfixOps

  override val supervisorStrategy: OneForOneStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: ArithmeticException      => Resume
      case _: ExceededLimitException     => Restart
      case _: IllegalArgumentException => Stop
      case _: Exception                => Escalate
    }
}


// キッチンアクターのコンパニオンオブジェクト
object KitchenActor {
  def props(offset: Int) = Props(classOf[KitchenActor], offset)

  // メッセージプロトコルの定義
  sealed trait Request
  case class DripCoffee(count: Int) extends Request
  case class BakeCake(count: Int) extends Request

  // 例外クラスの定義
  class ExceededLimitException(message :String = null) extends RuntimeException(message)
}
