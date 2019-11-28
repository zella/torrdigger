package service

import entities.TorrentHash
import monix.eval.Task

trait WebSearch {

  def searchSafe(name: String): Task[Seq[TorrentHash]]

}
