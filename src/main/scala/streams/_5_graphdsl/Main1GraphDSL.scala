package streams._5_graphdsl

import akka.actor.ActorSystem
import akka.stream._
import akka.NotUsed
import akka.stream.scaladsl._

/**
  * 複雑なグラフ
  */
object Main1GraphDSL extends App {

  // アクターシステムの生成
  implicit val system = ActorSystem("simple-stream")
  implicit val materializer = ActorMaterializer()

  // RunnableGraphの生成
  val runnableGraph: RunnableGraph[NotUsed] =
    RunnableGraph.fromGraph(GraphDSL.create() {
      implicit builder: GraphDSL.Builder[NotUsed] =>
        import GraphDSL.Implicits._

        // ソース／シンク
        val source = Source(1 to 5)
        val sink   = Sink.foreach(println)

        // ファンアウト／ファンイン
        val balance = builder.add(Balance[String](3))
        val merge   = builder.add(Merge[String](3))

        // フロー
        val flow1   = Flow[Int].map(e => s"$e ~> flow1")
        val flow2_1 = Flow[String].map(e => s"$e ~> flow2-1")
        val flow2_2 = Flow[String].map(e => s"$e ~> flow2-2")
        val flow2_3 = Flow[String].map(e => s"$e ~> flow2-3")
        val flow3   = Flow[String].map(e => s"$e ~> flow3")

        // グラフ
        source ~> flow1 ~> balance ~> flow2_1 ~> merge ~> flow3 ~> sink
        balance ~> flow2_2 ~> merge
        balance ~> flow2_3 ~> merge
        ClosedShape
    })

  // グラフの実行
  runnableGraph.run()

  Thread.sleep(1000)
  system.terminate()
}

