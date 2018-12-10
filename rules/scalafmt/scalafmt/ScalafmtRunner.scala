package annex.scalafmt

import annex.worker.WorkerMainFmt
import java.io.{File, PrintStream}
import java.nio.charset.Charset
import java.nio.file.Files
import net.sourceforge.argparse4j.ArgumentParsers
import net.sourceforge.argparse4j.impl.Arguments
import net.sourceforge.argparse4j.inf.Namespace
import org.scalafmt.Scalafmt
import org.scalafmt.config.Config
import org.scalafmt.util.FileOps
import scala.annotation.tailrec
import scala.io.Codec
import java.io.{File, FileWriter, IOException}
import scala.io.Source

object ScalafmtRunner extends WorkerMainFmt[Unit] {

  // def writeStringToFile(file: File, data: String, appending: Boolean = false) =
  //   using(new FileWriter(file, appending))(_.write(data))

  // def using[A <: {def close() : Unit}, B](resource: A)(f: A => B): B =
  //   try f(resource) finally resource.close()

  protected[this] def init(args: Option[Array[String]]): Unit = {}

  protected[this] def work(worker: Unit, args: Array[String], out: PrintStream): Unit = {

    val parser = ArgumentParsers.newFor("scalafmt").addHelp(true).defaultFormatWidth(80).fromFilePrefix("@").build
    parser.addArgument("--config").required(true).`type`(Arguments.fileType)
    parser.addArgument("input").`type`(Arguments.fileType)
    parser.addArgument("output").`type`(Arguments.fileType)

    val namespace = parser.parseArgsOrFail(args)

    val source = FileOps.readFile(namespace.get[File]("input"))(Codec.UTF8)
    // out.println("KEVINVIVNIVN")
    // out.println(namespace.get[File]("input"))
    val file = new java.io.File("/home/borkaehw/Desktop/fmt.txt")
    // val writer = new BufferedWriter(new FileWriter("/home/borkaehw/Desktop/fmt.txt"))
    val config = Config.fromHoconFile(namespace.get[File]("config")).get
    @tailrec
    def format(code: String): String = {
      writeStringToFile(file, namespace.get[File]("input") + " LOOP\n", appending = true)

      val formatted = Scalafmt.format(code, config).get
      if (code == formatted) code else format(formatted)
    }

    val output = try {
      format(source)
    } catch {
      // case e @ (_: org.scalafmt.Error | _: scala.meta.parsers.ParseException) => {
      case e: Throwable => {
        out.println(Console.YELLOW + "WARN: " + Console.WHITE + "Unable to format file due to bug in scalafmt")
        out.println(Console.YELLOW + "WARN: " + Console.WHITE + e)
        source
      }
    }

    writeStringToFile(file, namespace.get[File]("input") + " END\n", appending = true)

    Files.write(namespace.get[File]("output").toPath, output.getBytes)
  }

}
