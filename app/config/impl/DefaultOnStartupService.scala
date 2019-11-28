package config.impl

import com.google.inject.{Inject, Singleton}
import common.{MessageReceiver, ScheduledTask}
import config.OnStartupService
import entities.TorrentMessage
import monix.execution.Scheduler
import play.api.inject.ApplicationLifecycle
import service.impl.TorrentMessageHandler

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

@Singleton
class DefaultOnStartupService @Inject()(lifecycle: ApplicationLifecycle, receiver: MessageReceiver[TorrentMessage])(implicit ec: Scheduler)
  extends OnStartupService {

  schedule(
    4.seconds,
    1.seconds, //TODO conf
    new ScheduledTask("Polling torrent queue") {
      override def work(): Unit =
        Await.result(receiver.processMessages().runToFuture, 1.minute) //TODO conf
    }
  )

  private def schedule(delay: FiniteDuration, interval: FiniteDuration, task: ScheduledTask): Unit = {
    task.scheduleWithFixedRate(delay, interval)
    lifecycle.addStopHook(
      () =>
        Future {
          task.cancel()
        }
    )
  }

}
