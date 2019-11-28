package config

import java.net.URL

import trackers.TrackerCrawler

import scala.concurrent.duration.FiniteDuration

trait DiggerConfig {
  def outputHttpHook: Option[URL]

  def messagePerTick: Int

  def fetchTorrentExecutable: Seq[String]

  def fetchTorrentTimeout: FiniteDuration

  def spiderExecutable: Seq[String]

  def spiderTimeout: FiniteDuration

  def availableTrackers: Seq[TrackerCrawler]
}
