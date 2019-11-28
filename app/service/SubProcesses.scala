package service

import entities.TorrentMeta
import monix.eval.Task

trait SubProcesses {

  def evalTorrent(hash: String): Task[TorrentMeta]

}
