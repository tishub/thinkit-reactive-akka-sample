package cafe._1_sendmessages

import akka.actor.{Actor, ActorLogging, Props}

class BaristaActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case product: String => // String型のメッセージを受信した場合
      log.info(s"Received your order: $product")
    case count: Int =>      // Int型のメッセージを受信した場合
      log.info(s"Received your order: $count")
    case _ =>               // String型、Int型以外のメッセージを受信した場合
      log.info("Received your order.")
  }
}

// バリスタアクターのコンパニオンオブジェクト
object BaristaActor {
  val props: Props = Props[BaristaActor]

  // メッセージプロトコルの定義
  case class Order(product: String, count: Int)
}
