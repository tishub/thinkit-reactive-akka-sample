package cafe._6_supervisor

import akka.actor.{Actor, ActorLogging, Props}

// バリスタアクター
class BaristaActor(offset: Int) extends Actor with ActorLogging {
  import BaristaActor._

  private var orderCount = offset // 注文数
  override def receive: Receive = {
    case DripCoffee(count) =>
      orderCount += count // 受信した注文数を加算
      log.info(s"Receive your order: Drip $count cups of coffee. The number of orders: $orderCount ")
      sender() ! CashierActor.OrderCompleted("I'm a Barista. Received your orders!")
  }
}

// バリスタアクターのコンパニオンオブジェクト
object BaristaActor {
  def props(offset: Int) = Props(classOf[BaristaActor], offset)

  // ケースクラスの定義
  case class DripCoffee(count: Int)
}
