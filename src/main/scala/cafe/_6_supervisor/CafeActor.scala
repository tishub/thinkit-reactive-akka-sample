package cafe._6_supervisor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

// カフェアクター
class CafeActor extends Actor with ActorLogging {

  import CafeActor._

  val kitchen: ActorRef = context.actorOf(KitchenActor.props(0), "kitchenActor")
  val cashier: ActorRef = context.actorOf(CashierActor.props(kitchen), "cashierActor")

  override def receive: Receive = {
    case Initialize =>
      log.info("starting akka cafe")
    case Order(Coffee, count) =>
      cashier forward CashierActor.OrderCoffee(count)
    case Order(Cake, count) =>
      cashier forward CashierActor.OrderCake(count)
    case Shutdown =>
      log.info("terminating akka cafe")
      context.system.terminate()
  }
}

// カフェアクターのコンパニオンオブジェクト
object CafeActor {
  val props: Props = Props[CafeActor]

  // メッセージプロトコルの定義
  case object Initialize
  case object Shutdown
  case class Order(product: Product, count: Int)

  // 商品リスト
  sealed trait Products
  case object Coffee extends Products
  case object Cake extends Products
}
