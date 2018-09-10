package http._1_usinghttp

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

object CafeServer extends App {
  lazy val log = Logging(system, this.getClass)

  val route: Route =
    get {
      path("ping") {
        // ここに振る舞いを定義する
        log.info("receive GET request /ping")
        // ...

        // HTTP OKでレスポンスを返す
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>pong</h1>"))
      } ~
        path("ping-reject") {
          // ここにふるまいを定義する
          log.info("receive GET request /ping-reject")
          // ...

          reject(AuthorizationFailedRejection)
        } ~
        path("ping-fail") {
          // ここにふるまいを定義する
          log.info("receive GET request /ping-fail")
          // ...

          failWith(new IllegalArgumentException("oops!"))
        } ~
        path("ping-redirect") {
          // ここにふるまいを定義する
          log.info("receive GET request /ping-redirect")
          // ...

          redirect("/ping", StatusCodes.PermanentRedirect)
        }
    }

  // RunnableGraphの実行(run)にActorMaterializerが必要
  implicit val system = ActorSystem("CafeServer", ConfigFactory.load("cafe"))
  implicit val materializer = ActorMaterializer()

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
