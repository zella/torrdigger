package dao

import java.time.Instant

import com.google.inject.Inject
import entities.{TorrentHash, TorrentMessage}
import monix.eval.Task
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.db.NamedDatabase
import slick.jdbc.JdbcProfile
import slick.jdbc.SQLiteProfile.api._
import slick.lifted.{TableQuery, Tag}

import scala.concurrent.ExecutionContext

class MqTable(tag: Tag) extends Table[TorrentMessage](tag, "queue") {
  def hash = column[TorrentHash]("hash", O.PrimaryKey)

  def time = column[Instant]("time")

  def failures = column[Int]("failures", O.Default(0))

  def failTime = column[Instant]("failTime", O.Default(Instant.EPOCH))

  override def * = (hash, time, failures, failTime) <> ((TorrentMessage.apply _).tupled, TorrentMessage.unapply)

  def idxFailures = index("idx_failures", failures, unique = false)

  def idxFailTime = index("idx_fail_time", failTime, unique = false)

  def idxTime = index("idx_time", (time), unique = false)
}


class MqDao @Inject()(@NamedDatabase("default") protected val dbConfigProvider: DatabaseConfigProvider)(
  implicit val sc: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  private val mq = TableQuery[MqTable]

  val MaxFailures = 8

  def exec[T](query: DBIO[T]): Task[T] = Task.deferFuture(db.run(query))

  def take(count: Int): DBIO[Seq[TorrentMessage]] = (for {
    _ <- mq.schema.createIfNotExists
    messages <- mq
      .filter(_.failures < 8) //8 times TODO conf
      .filter(_.failTime < Instant.now().minusSeconds(7200)) //2hours TODO conf
      .sortBy(_.time).take(count).result
  } yield messages).transactionally

  def push(m: TorrentMessage): DBIO[Int] = (for {
    _ <- mq.schema.createIfNotExists
    created <- mq.insertOrUpdate(m)
  } yield created).transactionally

  def setFailed(hash: TorrentHash, failTime: Instant): DBIO[Int] = {
    (for {
      failures <- mq.filter(_.hash === hash).map(_.failures).result.headOption
      updated <- mq.filter(_.hash === hash)
        .map(t => (t.failures, t.failTime))
        .update((failures.getOrElse(0) + 1, failTime))
    } yield updated).transactionally
  }

  def delete(hashes: Seq[TorrentHash]): DBIO[Int] = {
    for {
      deleted <- mq.filter(_.hash.inSet(hashes)).delete
      _ <- mq.filter(_.failures > 8).delete
    } yield deleted
  }

  def count(): DBIO[Int] = for {
    _ <- mq.schema.createIfNotExists
    count <- mq.filter(_.failures === 0).length.result
  } yield count

}