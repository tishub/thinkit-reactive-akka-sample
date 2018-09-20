package cafe._3_tellpattern

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import cafe._3_tellpattern.BaristaActor.Order

class CashierActor extends Actor with ActorLogging {
  import CashierActor._

  val barista: ActorRef = context.actorOf(BaristaActor.props, "baristaActor")

  def receive: Receive = {
  	case Initialize =>
	    log.info("starting akka cafe")
    case order: Order =>
      barista ! order // 「!」でメッセージを送信
    case Shutdown =>
      log.info("terminating akka cafe")
      context.system.terminate()
    case result: OrderCompleted =>
      log.info(s"result: ${result.message}")
  }
}

object CashierActor {
  val props: Props = Props[CashierActor]

  // メッセージプロトコルの定義
  case object Initialize
  case object Shutdown
  case class OrderCompleted(message: String)
}
