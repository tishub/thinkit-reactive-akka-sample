package http._3_tellactor

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import cafe._3_tellpattern.BaristaActor.Order
import cafe._3_tellpattern.CashierActor
import com.typesafe.config.ConfigFactory
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

import scala.io.StdIn

object CafeServer extends App with SprayJsonSupport {
  lazy val log = Logging(system, this.getClass)

  // JSON形式とOrder型のマーシャリング・アンマーシャリング
  implicit val orderFormat: RootJsonFormat[Order] = jsonFormat2(Order)

  implicit val system = ActorSystem("AkkaHttpCafeServer", ConfigFactory.load("cafe"))
  // レジ係アクターを生成
  val cashierActor = system.actorOf(CashierActor.props, "cashierActor")

  val route: Route =
      put {
        path("order") {
          log.info("receive PUT request /order")
          entity(as[Order]) { order =>
            // レジ係アクターへOrderメッセージを送信
            cashierActor ! order
            complete(s"Received order: ${order.product} * ${order.count}")
          }
        }
      }

  // RunnableGraphの実行(run)にActorMaterializerが必要
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  // HTTPサーバーの起動
  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
  implicit val executionContext = system.dispatcher
  bindingFuture
    .map { serverBinding =>
      log.info(s"Server online at ${serverBinding.localAddress}")

      // 初期化メッセージを送信しカフェをスタートさせる
      cashierActor ! CashierActor.Initialize

      log.info("Press RETURN to stop...")
      StdIn.readLine() // RETURN が押されるまでサーバーを起動したままにする

      serverBinding.unbind()
      serverBinding
    }
    .onComplete { _ ⇒
      system.terminate()
      log.info("System terminated.")
    }
}
