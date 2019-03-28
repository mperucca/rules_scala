package higherkindness.rules_scala
package common.sbt_testing

import java.io.{PrintWriter, StringWriter}
import sbt.testing.{Event, Status, TestSelector}
import Status.{Canceled, Error, Failure, Ignored, Pending, Skipped}
import scala.collection.mutable.ListBuffer
import scala.xml.{Elem, XML}

class JUnitXmlReporter(tasksAndEvents: ListBuffer[(String, ListBuffer[Event])]) {
  def result: Elem =
    <testsuites>
      {
        for ((name, events) <- tasksAndEvents) yield <testsuite hostname="" name={
            name
          } tests={
            events.size.toString
          } errors={
            events.count(_.status == Error).toString
          } failures={
            events.count(_.status == Failure).toString
          } skipped={
            events.count(e => e.status == Ignored || e.status == Skipped || e.status == Pending || e.status == Canceled).toString
          } time={
            (events.map(_.duration).sum / 1000d).toString
          }>
          {
            for (e <- events) yield <testcase classname={ name } name={
                e.selector match {
                  case selector: TestSelector => selector.testName.split('.').last
                  case _ => "Error occurred outside of a test case."
                }
              } time={ (e.duration / 1000d).toString }>
              {
                val stringWriter = new StringWriter()
                if (e.throwable.isDefined) {
                  val writer = new PrintWriter(stringWriter)
                    e.throwable.get.printStackTrace(writer)
                    writer.flush()
                }
                val trace: String = stringWriter.toString
                e.status match {
                  case Status.Error if e.throwable.isDefined =>
                    val t = e.throwable.get
                    <error message={ t.getMessage } type={ t.getClass.getName }>{ trace }</error>
                  case Status.Error =>
                    <error message={ "No Exception or message provided" }/>
                  case Status.Failure if e.throwable.isDefined =>
                    val t = e.throwable.get
                    <failure message={ t.getMessage } type={ t.getClass.getName }>{ trace }</failure>
                  case Status.Failure =>
                    <failure message={ "No Exception or message provided" }/>
                  case Status.Ignored | Status.Skipped | Status.Pending | Status.Canceled =>
                    <skipped/>
                  case _ =>
                }
              }
            </testcase>
          }
          <system-out><![CDATA[]]></system-out>
          <system-err><![CDATA[]]></system-err>
        </testsuite>
      }
    </testsuites>

  def write = {
    Option(System.getenv.get("XML_OUTPUT_FILE"))
      .foreach { filespec =>
        XML.save(filespec, result, "UTF-8", true, null)
      }
  }
}
