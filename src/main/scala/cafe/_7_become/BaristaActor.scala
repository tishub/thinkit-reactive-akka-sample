package cafe._7_become

import akka.actor.{Actor, ActorLogging, Props, Stash}

// バリスタアクター
class BaristaActor extends Actor with ActorLogging with Stash {
  import BaristaActor._

  // 初期状態の設定
  override def receive: Receive = close
  // 注文数
  var orderCount = 0

  // オープ状態の振る舞い
  def open(bean: Bean): Receive = {
    case Order(product, count) =>
      orderCount += count         // 受信した注文数を加算
      log.info(s"Receive your order: $product, $count. The number of orders: $orderCount ")
      sender() ! CashierActor.OrderCompleted(s"Received your order. Today's coffee is ${bean.name}.")
    case Close =>
      context.become(close)       // クローズへ状態変更
  }

  // クローズ状態の振る舞い
  def close: Receive = {
    case Order =>
      stash()                     // オーダーを退避しておく
      log.info("I'm closed.")
    case Open(bean) =>
      unstashAll()                // 退避したオーダーを引き戻す
      context.become(open(bean))  // オープンへ状態変更
  }
}

// バリスタアクターのコンパニオンオブジェクト
object BaristaActor {
  val props: Props = Props[BaristaActor]

  // メッセージプロトコルの定義
  case class Order(product: String, count: Int)
  case class Open(bean: Bean)
  case object Close

  // コーヒー豆の種類
  case class Bean(name: String)
}
