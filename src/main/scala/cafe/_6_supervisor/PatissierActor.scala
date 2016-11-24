package cafe._6_supervisor

import akka.actor.{Actor, ActorLogging, Props}

// パティシエアクター
class PatissierActor(offset: Int) extends Actor with ActorLogging {
  import PatissierActor._

  private var orderCount = offset // 注文数
  override def receive: Receive = {
    case BakeCake(count) =>
      orderCount += count // 受信した注文数を加算
      log.info(s"Receive your order: Bake $count pieces of cake. The number of orders: $orderCount ")
      if(count > 3) throw new KitchenActor.ExceededLimitException(s"The number of your orders: $count")
      sender() ! CashierActor.OrderCompleted("I'm a Patissier. Received your orders!")
  }
}

// パティシエアクターのコンパニオンオブジェクト
object PatissierActor {
  def props(offset: Int) = Props(classOf[PatissierActor], offset)

  // ケースクラスの定義
  case class BakeCake(count: Int)
}
