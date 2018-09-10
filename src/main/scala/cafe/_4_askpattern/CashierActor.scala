package cafe._4_askpattern

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import cafe._4_askpattern.BaristaActor.Order

class CashierActor extends Actor with ActorLogging {
  import CashierActor._

  val barista: ActorRef = context.actorOf(BaristaActor.props, "baristaActor")

  def receive: Receive = {
  	case Initialize =>
	    log.info("starting akka cafe")
    case order: Order =>
      import scala.concurrent.duration._
      import akka.util.Timeout
      import akka.pattern.ask
      import context.dispatcher
      import scala.language.postfixOps

      implicit val timeout = Timeout(5 seconds)                // タイムアウトの設定
      barista forward  order // 「?」でメッセージを送信

    case Shutdown =>
      log.info("terminating akka cafe")
      context.system.terminate()
  }
}

object CashierActor {
  val props: Props = Props[CashierActor]

  // メッセージプロトコルの定義
  case object Initialize
  case object Shutdown
  case class OrderCompleted(message: String)
}
