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
import net.sourceforge.argparse4j.ArgumentParsers
import net.sourceforge.argparse4j.impl.Arguments
import net.sourceforge.argparse4j.inf.Namespace

import java.io.{File, FileWriter, IOException}
import scala.io.Source

trait WorkerMainFmt[S] {

  private[this] final case class ExitTrapped(code: Int) extends Throwable

  protected[this] def init(args: Option[Array[String]]): S

  protected[this] def work(ctx: S, args: Array[String], out: PrintStream): Unit

  def writeStringToFile(file: File, data: String, appending: Boolean = false) =
    using(new FileWriter(file, appending))(_.write(data))

  def using[A <: {def close() : Unit}, B](resource: A)(f: A => B): B =
    try f(resource) finally resource.close()

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

        val file = new java.io.File("/home/borkaehw/Desktop/angry.txt")

        // var fruits = new scala.collection.mutable.ListBuffer[WorkerProtocol.WorkRequest]()

        try {
          @tailrec
          def process(ctx: S): S = {
            val outStream = new ByteArrayOutputStream
            val out = new PrintStream(outStream)

            val request = WorkerProtocol.WorkRequest.parseDelimitedFrom(stdin)
            // fruits = fruits:+request
            val args = request.getArgumentsList.toArray(Array.empty[String])
            // out.println("KEVIN")
            // fruits.foreach(x => out.println(x.getInputsList()))
            val parser = ArgumentParsers.newFor("scalafmt").addHelp(true).defaultFormatWidth(80).fromFilePrefix("@").build
            parser.addArgument("--config").required(true).`type`(Arguments.fileType)
            parser.addArgument("input").`type`(Arguments.fileType)
            parser.addArgument("output").`type`(Arguments.fileType)
            val namespace = parser.parseArgsOrFail(args)

            // writeStringToFile(file, fruits + "\n", appending = true)
            writeStringToFile(file, namespace.get[File]("input") + " GET IN ANNEX\n", appending = true)

            // File.createTempFile("fmt", ".txt", new File("/home/borkaehw/Desktop/"))

            val requestId = request.getRequestId()


            val f: Future[Int] = Future {
              try {
                // out.println("TEST IN FUTURE")
                writeStringToFile(file, namespace.get[File]("input") + " TEST IN FUTURE\n", appending = true)
                work(ctx, args, out)
                0
              } catch {
                case ExitTrapped(code) => {
                  // out.println("TEST IN EXIT")
                  writeStringToFile(file, namespace.get[File]("input") + " TEST IN EXIT\n", appending = true)
                  code
                }
                case NonFatal(e) => {
                  // out.println("TSET NONFATAL")
                  writeStringToFile(file, namespace.get[File]("input") + " TEST IN NONFATAL\n", appending = true)
                  e.printStackTrace(out)
                  1
                }
              }
            }

            f.onComplete {
              case Success(code) => synchronized {

                writeStringToFile(file, namespace.get[File]("input") + " COMPLETE SUCCESS\n", appending = true)

                out.println(namespace.get[File]("input") + " Back to Bazel")

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
                writeStringToFile(file, namespace.get[File]("input") + " COMPLETE FAILURE\n", appending = true)
                e.printStackTrace(out)

                WorkerProtocol.WorkResponse.newBuilder
                  .setRequestId(requestId)
                  .setOutput(outStream.toString)
                  .setExitCode(-1)
                  .build
                  .writeDelimitedTo(stdout)

                out.flush()
                outStream.reset()
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
