package service

import akka.Done
import monix.eval.Task

trait TorrentProcessor {
  def process(): Task[Done]
}
