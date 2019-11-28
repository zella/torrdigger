package trackers

import entities.TorrentTrackerMeta
import monix.eval.Task

trait TrackerCrawler {

  def id:String

  def crawl(hash: String): Task[Option[TorrentTrackerMeta]]
}
