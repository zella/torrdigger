package service.impl

import java.time.Instant

import akka.Done
import com.google.inject.{Inject, Singleton}
import dao.MqDao
import entities.TorrentMessage
import monix.eval.Task
import service.QueueService

@Singleton
class SqliteQueueService @Inject()(mq: MqDao) extends QueueService[TorrentMessage] {

  override def take(count: Int): Task[Seq[TorrentMessage]] = mq.exec(mq.take(count))

  override def push(m: TorrentMessage): Task[Done] = mq.exec(mq.push(m)).map(_ => Done)

  override def failMessages(m: Seq[TorrentMessage]): Task[Done] = Task.sequence(
    m.map(m_ => mq.exec(mq.setFailed(m_.hash, Instant.now())))
  ).map(_ => Done)

  override def deleteMessages(m: Seq[TorrentMessage]): Task[Done] = mq.exec(mq.delete(m.map(_.hash))).map(_ => Done)
}
