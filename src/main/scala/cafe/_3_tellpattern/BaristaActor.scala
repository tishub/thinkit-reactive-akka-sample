package cafe._3_tellpattern

import akka.actor.{Actor, ActorLogging, Props}

// バリスタアクター
class BaristaActor extends Actor with ActorLogging {
  import BaristaActor._

  override def receive: Receive = {
    case Order(product, count) =>
      // Baristaからの応答を受け取ったときの振る舞い
      log.info(s"Your order has been completed. (product: $product, count: $count)")
      // 送信元に注文に対する調理が完了したことを返す
      sender() ! CashierActor.OrderCompleted("ok")
  }
}

// バリスタアクターのコンパニオンオブジェクト
object BaristaActor {
  val props: Props = Props[BaristaActor]

  // メッセージプロトコルの定義
  case class Order(product: String, count: Int)
}
