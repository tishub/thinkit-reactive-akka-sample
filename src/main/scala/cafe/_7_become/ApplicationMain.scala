package cafe._7_become

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.util.Random

object ApplicationMain extends App {
  val system = ActorSystem("CafeActorSystem", ConfigFactory.load("cafe"))
  val cashierActor = system.actorOf(CashierActor.props, "cashierActor")

  cashierActor ! CashierActor.Initialize

  import CashierActor._
  import system.dispatcher
  import scala.concurrent.duration._
  import scala.language.postfixOps

  // コーヒー豆は「モカ」でオープン
  cashierActor ! Open(BaristaActor.Bean("Mocha"))
  // 1秒毎にランダム杯のコーヒーを注文
  system.scheduler.schedule(0 seconds, 1 seconds)(cashierActor ! CashierActor.Order("Coffee", Random.nextInt(9) + 1))
  // 5秒後にクローズ
  system.scheduler.scheduleOnce( 5 seconds, cashierActor, CashierActor.Close)
  // 10秒後にコーヒー豆を「キリマンジャロ」でオープン
  system.scheduler.scheduleOnce(10 seconds, cashierActor, Open(BaristaActor.Bean("Kilimanjaro")))
  // 15秒後にシャットダウン
  system.scheduler.scheduleOnce(15 seconds, cashierActor, CashierActor.Shutdown)

  Await.result(system.whenTerminated, Duration.Inf)
}
