package cafe._3_tellpattern

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await

object ApplicationMain extends App {
  val system = ActorSystem("CafeActorSystem", ConfigFactory.load("cafe"))
  val cashierActor = system.actorOf(CashierActor.props, "cashierActor")

  cashierActor ! CashierActor.Initialize
  cashierActor ! CashierActor.Order

  import scala.concurrent.duration._
  import system.dispatcher
  import scala.language.postfixOps

  // 2秒後にシャットダウン
  system.scheduler.scheduleOnce(2 seconds, cashierActor, CashierActor.Shutdown)

  Await.result(system.whenTerminated, Duration.Inf)
}
