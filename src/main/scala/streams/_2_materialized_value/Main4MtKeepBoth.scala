package streams._2_materialized_value

import java.nio.file.Paths

import akka.Done
import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.stream.{ActorMaterializer, IOResult}
import akka.util.ByteString

import scala.concurrent.Future

/**
  * もうひとつの出力値（マテリアライズされた値）
  * Keep.bothを指定した場合
  */
object Main4MtKeepBoth extends App {

  // アクターシステムの生成
  implicit val system = ActorSystem("simple-stream")
  implicit val materializer = ActorMaterializer()

  // ソースを生成
  val source: Source[ByteString, Future[IOResult]] = FileIO.fromPath(Paths.get("README.md"))

  // シンクを生成
  val sink: Sink[ByteString, Future[Done]] = Sink.foreach(b => println(b.utf8String))

  // ソースをシンクに繋いでRunnableGraphを生成：Keep.bothを指定した場合
  val graph: RunnableGraph[(Future[IOResult], Future[Done])] = source.toMat(sink)(Keep.both)

  // RannableGraphを実行
  val result: (Future[IOResult], Future[Done]) = graph.run()

  implicit val executionContext = system.dispatcher
  result match {
    case (sourceMt, sinkMt) => for {
      sourceResult <- sourceMt
      sinkResult <- sinkMt
    } yield {
      println(s"source:$sourceResult, sink:$sinkResult")
      // アクターシステムを終了
      system.terminate()
    }
  }
}
