package streams._1_source_and_sink

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._

/**
  * Akka Streamsの基本要素
  * ショートカットメソッドによるグラフの実行
  */
object Main2ShortcutMethod extends App {

  // アクターシステムの生成
  implicit val system = ActorSystem("simple-stream")
  implicit val materializer = ActorMaterializer()

  // アクターシステムの終了

  // ショートカットメソッドによるグラフの実行
  implicit val executionContext = system.dispatcher
  val result = Source(1 to 10).runForeach(println)
  result.onComplete(_ => system.terminate())
}
