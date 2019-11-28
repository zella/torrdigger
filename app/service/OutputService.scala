package service

import akka.Done
import entities.Torrent
import monix.eval.Task

trait OutputService {

  def outputTorrent(torrent:Torrent): Task[Done]

}
