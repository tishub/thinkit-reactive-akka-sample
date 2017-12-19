package streams._4_graph_and_actor

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._

/**
  * グラフとアクター
  * asyncメソッドによる非同期境界の設定
  */
object Main1Async extends App {

  // アクターシステムの生成
  implicit val system = ActorSystem("simple-stream")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  // ソースを生成
  val source = Source(1 to 5)
  // シンクを生成
  val sink = Sink.foreach[Int](e => println(s"$e                 ~> sink"))
  // フローを生成
  val flow1 = Flow[Int].map { e =>
    println(s"$e ~> flow1")
    e
  }
  val flow2 = Flow[Int].map { e =>
    println(s"$e         ~> flow2")
    e
  }

  // RunnableGraphを生成
  val runnableGraph = source.via(flow1).async.via(flow2).toMat(sink)(Keep.right)

  // RannableGraphを実行
  val result = runnableGraph.run()

  result.onComplete { r =>
    println(s"result:$r")
    // アクターシステムを終了
    system.terminate()
  }
}
