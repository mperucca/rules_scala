package annex.bloop

import annex.worker.WorkerMain
import bloop.Bloop
import java.io.PrintStream

object BloopRunner extends WorkerMain[Unit] {
  override def init(args: Option[Array[String]]): Unit = ()
  override def work(ctx: Unit, args: Array[String], out: PrintStream): Unit = Bloop
}
