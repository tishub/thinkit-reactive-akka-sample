package cafe._7_become

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import cafe._7_become.BaristaActor.Bean

class CashierActor extends Actor with ActorLogging {
  import CashierActor._

  val barista: ActorRef = context.actorOf(BaristaActor.props, "baristaActor")

  def receive: Receive = {
  	case Initialize =>
	    log.info("starting akka cafe")
    case Open(bean :Bean) =>
      barista ! BaristaActor.Open(bean)
    case Close =>
      barista ! BaristaActor.Close
    case Order(product, count) =>
      barista ! BaristaActor.Order(product, count)
    case result: OrderCompleted =>
      log.info(s"result: ${result.message}")
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
  case class Open(bean: Bean)
  case object Close
  case class Order(product: String, count: Int)
  case class OrderCompleted(message: String)
}
