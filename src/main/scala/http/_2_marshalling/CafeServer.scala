package http._2_marshalling

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import spray.json.DefaultJsonProtocol._

import scala.io.StdIn

object CafeServer extends App with SprayJsonSupport {
  lazy val log = Logging(system, this.getClass)

  // ケースクラスの定義
  case class Order(product: String, count: Int)

  // JSON形式とOrder型のマーシャリング・アンマーシャリング
  implicit val orderFormat = jsonFormat2(Order)

  val route: Route =
    path("order") {
      get {
        // ここにふるまいを定義する
        log.info("receive GET request /order")
        // ...

        // Order型を返す
        complete(Order("Coffee", 10))
      } ~
        put {
          // Order型で受け取る
          entity(as[Order]) { order =>
            log.info("receive PUT request /order")
            // ここにふるまいを定義する
            // ...

            complete(s"Received order: ${order.product} * ${order.count}")
          }
        }
    }

  // RunnableGraphの実行(run)にActorMaterializerが必要
  implicit val system = ActorSystem("AkkaHttpCafeServer", ConfigFactory.load("cafe"))
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  // HTTPサーバーの起動
  val bindingFuture = Http().bindAndHandle(route, "localhost", 8082)
  implicit val executionContext = system.dispatcher
  bindingFuture
    .map { serverBinding =>
      log.info(s"Server online at ${serverBinding.localAddress}")
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
