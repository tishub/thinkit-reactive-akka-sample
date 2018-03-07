package streams._1_source_and_sink

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._

/**
  * Akka Streamsの基本要素
  * ソースとシンクを繋いで実行（run）
  */
object Main1SourceAndSink extends App {

  // ----- フローの定義 -----

  // ソースの作成
  val source = Source(1 to 10)
  // シンクの作成
  val sink = Sink.foreach[Int](e => println(s"element=$e"))
  // ソースをシンクに繋いでRunnableGraphを作成
  val runnableGraph = source.toMat(sink)(Keep.right)


  // ----- フローの実行 -----

  // アクターシステムの生成
  implicit val system = ActorSystem("simple-stream")
  implicit val materializer = ActorMaterializer()

  // RannableGraphの実行
  val result = runnableGraph.run()

  // アクターシステムの終了
  implicit val executionContext = system.dispatcher
  result.onComplete(_ => system.terminate())

}
