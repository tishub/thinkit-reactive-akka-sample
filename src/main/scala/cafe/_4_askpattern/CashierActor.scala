package cafe._4_askpattern

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

import scala.util.{Failure, Success}

class CashierActor extends Actor with ActorLogging {
  import CashierActor._

  val barista: ActorRef = context.actorOf(BaristaActor.props, "baristaActor")

  def receive: Receive = {
  	case Initialize =>
	    log.info("starting akka cafe")
    case Order =>
      import scala.concurrent.duration._
      import akka.util.Timeout
      import akka.pattern.ask
      import context.dispatcher
      import scala.language.postfixOps

      implicit val timeout = Timeout(5 seconds)                // タイムアウトの設定
      val response = barista ? BaristaActor.Order("coffee", 2)  // 「?」でメッセージを送信
      // 応答があった後の未来の処理
      response.mapTo[OrderCompleted] onComplete {
        case Success(result) =>
          log.info(s"result: ${result.message}")
        case Failure(t) =>
          log.info(t.getMessage)
      }
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
  case class OrderCompleted(message: String)
}
