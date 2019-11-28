package config.impl

import java.net.URL

import com.google.inject.{Inject, Singleton}
import com.typesafe.scalalogging.LazyLogging
import config.DiggerConfig
import play.api.Configuration
import trackers.TrackerCrawler

import scala.concurrent.duration.FiniteDuration
import scala.util.Try

@Singleton
class DefaultDiggerConfig @Inject()(conf: Configuration) extends DiggerConfig with LazyLogging {

  logger.info(s"Output Http Hook: $outputHttpHook")

  override val outputHttpHook: Option[URL] = Try(new URL(conf.get[String]("output.httpHook"))).toOption

  override def availableTrackers: Seq[TrackerCrawler] = ??? //TODO
  override def fetchTorrentExecutable: Seq[String] = ???

  override def fetchTorrentTimeout: FiniteDuration = ???

  override def spiderExecutable: Seq[String] = ???

  override def spiderTimeout: FiniteDuration = ???
}
