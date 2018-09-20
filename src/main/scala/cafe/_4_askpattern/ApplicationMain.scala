package cafe._4_askpattern

import akka.actor.ActorSystem
import cafe._4_askpattern.CashierActor.OrderCompleted
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.util.{Failure, Success}

object ApplicationMain extends App {
  val system = ActorSystem("CafeActorSystem", ConfigFactory.load("cafe"))
  val cashierActor = system.actorOf(CashierActor.props, "cashierActor")

  cashierActor ! CashierActor.Initialize

  import scala.concurrent.duration._
  import akka.util.Timeout
  import akka.pattern.ask
  import system.dispatcher

  import scala.language.postfixOps

  implicit val timeout = Timeout(5 seconds)                // タイムアウトの設定
  val response = cashierActor ? BaristaActor.Order("coffee", 2)  // 「?」でメッセージを送信
  // 応答があった後の未来の処理
  response.mapTo[OrderCompleted] onComplete {
    case Success(result) =>
      println(s"result: ${result.message}")
    case Failure(t) =>
      println(t.getMessage)
  }

  // 2秒後にシャットダウン
  system.scheduler.scheduleOnce(2 seconds, cashierActor, CashierActor.Shutdown)

  Await.result(system.whenTerminated, Duration.Inf)
}
