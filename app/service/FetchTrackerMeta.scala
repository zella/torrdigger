package service

import entities.{TorrentMeta, TorrentTrackerMeta}
import monix.eval.Task

trait FetchTrackerMeta {

  def crawlSafe(hash: String): Task[Option[TorrentTrackerMeta]]

}
