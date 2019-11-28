package trackers.impl

import entities.TorrentTrackerMeta
import monix.eval.Task
import play.api.libs.ws.WSClient
import trackers.TrackerCrawler

class RutorTrackerCrawler(ws: WSClient) extends TrackerCrawler {
  override def crawl(hash: String): Task[Option[TorrentTrackerMeta]] = Task.raiseError(new UnsupportedOperationException)

  override def id: String = "rutor"
}
