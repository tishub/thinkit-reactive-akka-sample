package cafe._2_patternmatch

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

class CashierActor extends Actor with ActorLogging {
  import CashierActor._
  val barista: ActorRef = context.actorOf(BaristaActor.props, "baristaActor")

  def receive: Receive = {
  	case Initialize =>
	    log.info("starting akka cafe")
    case Order =>
      barista ! 1                   // Int型
      barista ! 2                   // Int型
      barista ! 3                   // Int型

      barista ! BaristaActor.Order("Coffee", 2)  // メッセージをケースクラスで送信
      barista ! BaristaActor.Order("Cake", 1)    // メッセージをケースクラスで送信
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
  case object Order
}
