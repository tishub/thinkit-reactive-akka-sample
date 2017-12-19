package streams._2_materialized_value

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._

import scala.concurrent.Future

/**
  * もうひとつの出力値（マテリアライズされた値）
  */
object Main1Mt extends App {

  // アクターシステムの生成
  implicit val system = ActorSystem("simple-stream")
  implicit val materializer = ActorMaterializer()

  // ソースを生成
  val source: Source[Int, NotUsed] = Source(1 to 10)

  // シンクを生成
  val sink: Sink[Int, Future[Int]] = Sink.fold[Int, Int](0)(_ + _)

  // ソースをシンクに繋いでRunnableGraphを生成
  val runnableGraph: RunnableGraph[Future[Int]] = source.toMat(sink)(Keep.right)

  // RannableGraphを実行
  val result: Future[Int] = runnableGraph.run()

  implicit val executionContext = system.dispatcher
  result.foreach { r =>
    println(s"result:$r")
    // アクターシステムを終了
    system.terminate()
  }
}
