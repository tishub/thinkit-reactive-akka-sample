package cafe._5_actorandthread

import akka.actor.{Actor, ActorLogging, Props}

// バリスタアクター
class BaristaActor(offset: Int) extends Actor with ActorLogging {
  import BaristaActor._

  private var orderCount = offset // 注文数
  override def receive: Receive = {
    case Order(product, count) =>
      orderCount += count // 受信した注文数を加算
      log.info(s"Receive your order: $product, $count. The number of orders: $orderCount ")
      sender() ! CashierActor.OrderCompleted(s"Received your order.")
  }
}

// バリスタアクターのコンパニオンオブジェクト
object BaristaActor {
  def props(offset: Int) = Props(classOf[BaristaActor], offset)

  // メッセージプロトコルの定義
  case class Order(product: String, count: Int)
}
