package service.impl

import com.google.inject.{Inject, Singleton}
import com.typesafe.scalalogging.LazyLogging
import config.DiggerConfig
import entities.TorrentTrackerMeta
import monix.eval.Task
import service.FetchTrackerMeta
import trackers.TrackerCrawler

import scala.util.control.NonFatal

@Singleton
class DefaultFetchTrackerMeta @Inject()(conf: DiggerConfig) extends FetchTrackerMeta with LazyLogging {

  private def crawlSafe(hash: String, crawler: TrackerCrawler): Task[Option[TorrentTrackerMeta]] = {
    crawler.crawl(hash).onErrorRecover {
      case NonFatal(e) =>
        logger.error(s"Error crawl hash: $hash , tracker: ${crawler.id}", e)
        None
    }
  }

  override def crawlSafe(hash: String): Task[Option[TorrentTrackerMeta]] = {
    Task.sequence(conf.availableTrackers.map(crawlSafe(hash, _))).map(_.flatten)
      .map(_.headOption)
      .onErrorRecover {
        case NonFatal(e) =>
          logger.error(s"Error fetch trackers'${e.getLocalizedMessage}'", e)
          None
      }
  }
}

