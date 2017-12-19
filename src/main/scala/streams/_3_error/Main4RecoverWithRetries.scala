package streams._3_error

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._

/**
  * エラー処理
  * 代替ソースによる回復
  */
object Main4RecoverWithRetries extends App {

  // アクターシステムの生成
  implicit val system = ActorSystem("simple-stream")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  // ソースを作成
  val source = Source(1 to 5)
  // シンクを作成
  val sink = Sink.foreach[Int](e => println(s"element=$e"))

  // フローを作成：代替ソースによる回復
  val flow = Flow[Int].map(e =>
    // 要素の値が3の場合に例外を発生させる
    if (e == 3) throw new IllegalArgumentException("oops!") else e
  ).recoverWithRetries(1, {
    case _: IllegalArgumentException ⇒ Source(List(30,40))
  })

  // 要素の値が3の場合に例外を発生させるグラフ
  val runnableGraph = source.via(flow).toMat(sink)(Keep.right)

  // RannableGraphを実行
  val result = runnableGraph.run()

  result.onComplete { r =>
    println(s"result:$r")
    // アクターシステムを終了
    system.terminate()
  }
}
