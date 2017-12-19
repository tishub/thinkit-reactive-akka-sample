package streams._3_error

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
import akka.stream.scaladsl._

/**
  * エラー処理
  * マテリアライザーにスーパーバイザー戦略を適用
  */
object Main5Supervisor extends App {

  // アクターシステムの生成
  implicit val system = ActorSystem("simple-stream")
  implicit val executionContext = system.dispatcher

  // Supervision.Deciderの定義
  val decider: Supervision.Decider = {
    case _: IllegalArgumentException ⇒ Supervision.Resume
    case _                           ⇒ Supervision.Stop
  }
  // マテリアライザーにスーパーバイザー戦略を適用
  implicit val materializer = ActorMaterializer(
    ActorMaterializerSettings(system).withSupervisionStrategy(decider))

  // ソースを作成
  val source = Source(1 to 5)
  // シンクを作成
  val sink = Sink.foreach[Int](e => println(s"element=$e"))

  // フローを作成
  val flow = Flow[Int].map{e =>
    // 要素の値が3の場合に例外を発生させる
    if (e == 3) throw new IllegalArgumentException("oops!") else e
  }

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
