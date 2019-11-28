import com.google.inject.{AbstractModule, Provides, Singleton}
import common.{MessageHandler, MessageReceiver}
import config.DiggerConfig
import entities.TorrentMessage
import monix.execution.Scheduler
import service.QueueService

class Module extends AbstractModule {

  @Provides
  @Singleton
  def scheduler(): Scheduler = monix.execution.Scheduler.global

  @Provides
  @Singleton
  def messageReceiver(
                       handler: MessageHandler[TorrentMessage],
                       queueService: QueueService[TorrentMessage],
                       conf: DiggerConfig
                     ): MessageReceiver[TorrentMessage] =
    new MessageReceiver[TorrentMessage](handler, queueService, conf.messagePerTick)

}
