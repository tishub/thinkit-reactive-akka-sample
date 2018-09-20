package http._4_askactor

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import cafe._4_askpattern.BaristaActor.Order
import cafe._4_askpattern.CashierActor
import cafe._4_askpattern.CashierActor.OrderCompleted
import com.typesafe.config.ConfigFactory
import spray.json.DefaultJsonProtocol._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.{Failure, Success}

object CafeServer extends App with SprayJsonSupport {
  lazy val log = Logging(system, this.getClass)

  // JSON形式とOrder型のマーシャリング・アンマーシャリング
  implicit val orderFormat = jsonFormat2(Order)

  implicit val system = ActorSystem("AkkaHttpCafeServer", ConfigFactory.load("cafe"))
  // レジ係アクターを定義
  val cashierActor = system.actorOf(CashierActor.props, "cashierActor")

  val route: Route =
    path("order") {
      entity(as[Order]) { order =>
        log.info("receive PUT request /order")

        implicit val timeout: Timeout = 5.seconds
        val response: Future[Any] = cashierActor ? order

        // レジ係アクターからの応答を受信したときの処理
        onComplete(response.mapTo[OrderCompleted]) {
          case Success(orderCompleted) =>
            complete(s"${orderCompleted.message}")
          case Failure(t) =>
            complete(s"${t.getMessage}")
        }
      }
    }

  // RunnableGraphの実行(run)にActorMaterializerが必要
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  // HTTPサーバーの起動
  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
  implicit val executionContext = system.dispatcher
  bindingFuture
    .map {
      serverBinding =>
        log.info(s"Server online at ${
          serverBinding.localAddress
        }")

        // 初期化メッセージを送信しカフェをスタートさせる
        cashierActor ! CashierActor.Initialize

        log.info("Press RETURN to stop...")
        StdIn.readLine() // RETURN が押されるまでサーバーを起動したままにする

        serverBinding.unbind()
        serverBinding
    }
    .onComplete {
      _ ⇒
        system.terminate()
        log.info("System terminated.")
    }
}
