package cafe._6_supervisor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

class CashierActor(kitchen: ActorRef) extends Actor with ActorLogging {
  import CashierActor._

  def receive: Receive = {
    case OrderCoffee(count) =>
      kitchen ! KitchenActor.DripCoffee(count)
    case OrderCake(count) =>
      kitchen ! KitchenActor.BakeCake(count)
    case result: OrderCompleted =>
      log.info(s"result: ${result.message}")
  }
}

object CashierActor {
  def props(kitchen: ActorRef) = Props(classOf[CashierActor], kitchen)

  // メッセージプロトコルの定義
  case class OrderCompleted(message: String)

  sealed trait Order
  case class OrderCoffee(count: Int) extends Order
  case class OrderCake(count: Int) extends Order
}
