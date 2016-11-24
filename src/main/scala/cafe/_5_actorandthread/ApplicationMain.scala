package cafe._5_actorandthread

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.util.Random

object ApplicationMain extends App {
  val system = ActorSystem("CafeActorSystem", ConfigFactory.load("cafe"))
  val cashierActor = system.actorOf(CashierActor.props, "cashierActor")
  import system.dispatcher
  import scala.concurrent.duration._
  import scala.language.postfixOps

  cashierActor ! CashierActor.Initialize
  system.scheduler.schedule(0 seconds, 1 seconds)(cashierActor ! CashierActor.Order("Coffee", Random.nextInt(9) + 1))

  // 2秒後にシャットダウン
  system.scheduler.scheduleOnce(2 seconds, cashierActor, CashierActor.Shutdown)

  Await.result(system.whenTerminated, Duration.Inf)
}
