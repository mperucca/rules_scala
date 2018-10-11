package annex.worker

import com.google.devtools.build.lib.worker.WorkerProtocol
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.PrintStream
import java.lang.SecurityManager
import java.security.Permission
import java.util.concurrent.Executors
import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.util.Success
import scala.util.Failure

trait WorkerMain[S] {

  private[this] final case class ExitTrapped(code: Int) extends Throwable

  protected[this] def init(args: Option[Array[String]]): S

  protected[this] def work(ctx: S, args: Array[String], out: PrintStream): Unit

  final def main(args: Array[String]): Unit = {
    args.toList match {
      case "--persistent_worker" :: args =>
        val stdin = System.in
        val stdout = System.out
        val stderr = System.err

        System.setSecurityManager(new SecurityManager {
          val Exit = raw"exitVM\.(-?\d+)".r
          override def checkPermission(permission: Permission): Unit = {
            permission.getName match {
              case Exit(code) => throw new ExitTrapped(code.toInt)
              case _          =>
            }
          }
        })

        val garbageOut = new PrintStream(new ByteArrayOutputStream)

        System.setIn(new ByteArrayInputStream(Array.emptyByteArray))
        System.setOut(garbageOut)
        System.setErr(garbageOut)

        try {
          @tailrec
          def process(ctx: S): S = {
            val request = WorkerProtocol.WorkRequest.parseDelimitedFrom(stdin)
            val args = request.getArgumentsList.toArray(Array.empty[String])

            val f: Future[(Int, ByteArrayOutputStream, PrintStream, Int)] = Future {
              val outStream = new ByteArrayOutputStream
              val out = new PrintStream(outStream)
              val requestId = request.getRequestId()

              val code = try {
                work(ctx, args, out)
                0
              } catch {
                case ExitTrapped(code) => code
                case NonFatal(e) =>
                  e.printStackTrace()
                  1
              }
              (code, outStream, out, requestId)
            }

            f.onComplete {
              case Success(value) => synchronized {
                val code = value._1
                val outStream = value._2
                val out = value._3
                val requestId = value._4

                WorkerProtocol.WorkResponse.newBuilder
                  .setRequestId(requestId)
                  .setOutput(outStream.toString)
                  .setExitCode(code)
                  .build
                  .writeDelimitedTo(stdout)

                out.flush()
                outStream.reset()
              }
              case Failure(e) => {
                e.printStackTrace()
              }
            }
            process(ctx)
          }
          process(init(Some(args.toArray)))
        } finally {
          System.setIn(stdin)
          System.setOut(stdout)
          System.setErr(stderr)
        }

      case args => {
        val outStream = new ByteArrayOutputStream
        val out = new PrintStream(outStream)
        work(init(None), args.toArray, out)
      }
    }
  }

}
