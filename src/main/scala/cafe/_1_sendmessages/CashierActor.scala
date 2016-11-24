package cafe._1_sendmessages

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

class CashierActor extends Actor with ActorLogging {
  import CashierActor._

  def createBarista() : ActorRef = {
    context.actorOf(BaristaActor.props, "baristaActor")
  }

  def receive: Receive = {
    case Initialize =>
      log.info("starting akka cafe")
    case Order =>
      // CashierからBaristaにメッセージを送信
      val barista = createBarista()
      barista ! "coffee"            // String型
      barista ! 2                   // Int型
      barista ! BaristaActor.Order("coffee", 2)  // Order型
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
