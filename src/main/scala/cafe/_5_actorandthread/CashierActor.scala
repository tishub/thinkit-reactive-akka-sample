package cafe._5_actorandthread

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

class CashierActor extends Actor with ActorLogging {
  import CashierActor._

  val barista: ActorRef = context.actorOf(BaristaActor.props(0), "baristaActor")

  def receive: Receive = {
  	case Initialize =>
	    log.info("starting akka cafe")
    case Order(product, count) =>
      barista ! BaristaActor.Order(product, count)  // 「!」でメッセージを送信
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
  case class Order(product: String, count: Int)
  case class OrderCompleted(message: String)
}
