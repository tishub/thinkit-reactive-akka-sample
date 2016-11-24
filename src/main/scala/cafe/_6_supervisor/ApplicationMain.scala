package cafe._6_supervisor

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await

object ApplicationMain extends App {
  val system = ActorSystem("CafeActorSystem", ConfigFactory.load("cafe"))
  val cafeActor = system.actorOf(CafeActor.props, "cafeActor")

  cafeActor ! CafeActor.Initialize

  // 顧客からの注文
  cafeActor ! CafeActor.Order(CafeActor.Coffee, 2)
  cafeActor ! CafeActor.Order(CafeActor.Cake, 4)
  cafeActor ! CafeActor.Order(CafeActor.Coffee, 1)
  cafeActor ! CafeActor.Order(CafeActor.Cake, 2)

  import scala.concurrent.duration._
  import system.dispatcher
  import scala.language.postfixOps

  // 2秒後にシャットダウン
  system.scheduler.scheduleOnce(2 seconds, cafeActor, CafeActor.Shutdown)

  Await.result(system.whenTerminated, Duration.Inf)
}
