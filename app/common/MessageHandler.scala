package common

import monix.eval.Task

trait MessageHandler[T] {
  def handle(messages: Seq[T]): Task[Boolean]
}
