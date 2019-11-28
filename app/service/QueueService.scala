package service

import akka.Done
import monix.eval.Task

trait QueueService[T] {

  def take(count: Int): Task[Seq[T]]

  def push(m: T): Task[Done]

  def failMessages(m: Seq[T]): Task[Done]

  def deleteMessages(m: Seq[T]): Task[Done]

}
