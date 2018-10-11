package annex.compiler

import annex.compiler.Arguments.LogLevel
import java.io.{PrintStream, PrintWriter, StringWriter}
import java.nio.file.Paths
import java.util.function.Supplier
import xsbti.Logger

final class AnxLogger(level: String, out: PrintStream = System.err) extends Logger {
  private[this] val root = s"${Paths.get("").toAbsolutePath}/"

  private[this] def format(value: String) = value.replace(root, "")

  def debug(msg: Supplier[String]) = level match {
    case LogLevel.Debug => out.println(format(msg.get))
    case _              =>
  }

  def error(msg: Supplier[String]) = level match {
    case LogLevel.Debug | LogLevel.Error | LogLevel.Info | LogLevel.Warn => out.println(format(msg.get))
    case _                                                               =>
  }

  def info(msg: Supplier[String]) = level match {
    case LogLevel.Debug | LogLevel.Info => out.println(format(msg.get))
    case _                              =>
  }

  def trace(err: Supplier[Throwable]) = level match {
    case LogLevel.Debug | LogLevel.Error | LogLevel.Info | LogLevel.Warn =>
      val trace = new StringWriter();
      err.get.printStackTrace(new PrintWriter(trace));
      out.println(format(trace.toString))
    case _ =>
  }

  def warn(msg: Supplier[String]) = level match {
    case LogLevel.Debug | LogLevel.Info | LogLevel.Warn => out.println(format(msg.get))
    case _                                              =>
  }

}
