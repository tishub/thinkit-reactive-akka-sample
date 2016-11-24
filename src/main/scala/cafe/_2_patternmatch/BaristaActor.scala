package cafe._2_patternmatch

import akka.actor.{Actor, ActorLogging, Props}

// バリスタアクター
class BaristaActor extends Actor with ActorLogging {
  import BaristaActor._
  val two = 2

  override def receive: Receive = {
    // 1の場合
    case 1 =>
      log.info("(1) received: 1")
    // 変数「two」の値と一致する場合
    case `two` =>
      log.info(s"(2) received: $two")
    // 10より小さい数値（Int型）の場合
    case count: Int if count < 10 =>
      log.info(s"(3) received: $count")
    // Order型で第一引数productが「Coffee」の場合
    case order @ Order("Coffee", _) =>
      log.info(s"(4) product: ${order.product}")
      log.info(s"    count  : ${order.count}")
    // Order型の場合
    case Order(product, count) =>
      log.info(s"(5) Receive your order.") // ケースクラスを受信した時の振る舞い
      log.info(s"    product: $product")
      log.info(s"    count  : $count")
  }
}

// バリスタアクターのコンパニオンオブジェクト
object BaristaActor {
  val props: Props = Props[BaristaActor]

  // メッセージプロトコルの定義
  case class Order(product: String, count: Int)
}
