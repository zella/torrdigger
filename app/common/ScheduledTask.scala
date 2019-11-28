package common

import com.typesafe.scalalogging.LazyLogging
import javax.annotation.concurrent.ThreadSafe
import monix.execution.{Cancelable, Scheduler}

import scala.concurrent.duration._
import scala.util.control.NonFatal


@ThreadSafe
abstract class ScheduledTask(taskName: String)(implicit private val scheduler: Scheduler) extends LazyLogging {

  private var job: Option[Cancelable] = None

  def work(): Unit

  final def scheduleWithFixedRate(initialDelay: FiniteDuration, delay: FiniteDuration): Unit = {
    synchronized {
      if (job.nonEmpty) {
        logger.debug(s"task '$taskName' has been already scheduled")
      } else {
        job = Some(scheduler.scheduleAtFixedRate(initialDelay, delay) {
          // if task throws exception, scheduler doesn't reschedule it
          try {
            work()
          } catch {
            case NonFatal(e) => logger.error(s"error during execution of scheduled task '$taskName'", e)
          }
        })
        logger.info(s"task '$taskName' was scheduled")
      }
    }
  }

  final def cancel(): Unit = {
    synchronized {
      if (job.isEmpty) {
        logger.debug(s"task '$taskName' has not been scheduled yet or has been already cancelled")
      } else {
        job.foreach(_.cancel())
        job = None
        logger.info(s"task '$taskName' was cancelled")
      }
    }
  }

}
