package entities

import java.time.Instant

import play.api.libs.json.{Format, Json}

case class Torrent(hash: String,
                   name: String,
                   files: Seq[TorrentFileMeta],
                   size: Long,
                   contentType: Option[String],
                   info: Option[String],
                   seeders: Option[Int],
                   leechers: Option[Int],
                   added: Option[Instant],
                   completed: Option[Boolean],
                   good: Option[Int],
                   bad: Option[Int],
                   tracker: Option[String]
                  )

object Torrent {
  implicit val jsonFormat: Format[Torrent] = Json.format[Torrent]

  def from(meta:TorrentMeta, trackerMeta: Option[TorrentTrackerMeta]): Torrent = ???
}

