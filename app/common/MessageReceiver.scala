package common

import akka.Done
import com.typesafe.scalalogging.LazyLogging
import monix.eval.Task
import service.QueueService

class MessageReceiver[T](handler: MessageHandler[T], queue: QueueService[T], messageCount: Int) extends LazyLogging {

  def processMessages(): Task[Done] = {
    queue.take(messageCount)
      .flatMap(messages => handler.handle(messages)
        .flatMap(success =>
          if (success) {
            queue.deleteMessages(messages)
          } else {
            logger.error(s"Fail to handle Message ${messages.toString}")
            Task.pure(Done)
          }
        )
        .onErrorRecover {
          case e: Exception =>
            logger.error(s"Fail to handle Message ${messages.toString}", e)
            Done
        })
  }
}
