package streams._6_buffer

import akka.Done
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * バッファー
  * バッファーあり、オーバーフロー戦略：backpressure
  */
object Main2BufferBackpressure extends App {

  // アクターシステムの生成
  implicit val system = ActorSystem("simple-stream")
  implicit val materializer = ActorMaterializer(
    ActorMaterializerSettings(system)
      .withInputBuffer(
        initialSize = 1,
        maxSize = 1))

  // RunnableGraphの生成
  val runnableGraph =
    RunnableGraph.fromGraph(GraphDSL.create() {
      implicit builder =>
        import GraphDSL.Implicits._
        // 1秒間に5件のデータを流すソース
        val source = Source(1 to 15).throttle(5, 1.second, 1, ThrottleMode.shaping)
        val sink: Sink[String, Future[Done]] = Sink.foreach[String](println)

        // 2つの出力にブロードキャスト
        val broadcast = builder.add(Broadcast[Int](2))

        // 非同期境界
        val flow = Flow[Int].async

        // 遅いフロー：1秒間に1件のデータを処理
        val slowFlow = Flow[Int].map { e =>
          Thread.sleep(1000)
          s"    slow $e"
        }.async

        // 速いフロー：1秒間に5件のデータを処理
        val fastFlow = Flow[Int].map { e =>
          Thread.sleep(200)
          s"fast $e"
        }.async

        // バッファーの設定：backpressure
        val buffer = Flow[Int].buffer(2, OverflowStrategy.backpressure)

        // グラフ
        source ~> flow ~> broadcast ~>  buffer ~> slowFlow ~> sink
                          broadcast ~> fastFlow ~> sink
        ClosedShape
    })

  // グラフの実行
  runnableGraph.run()

  Thread.sleep(16000)
  system.terminate()
}

