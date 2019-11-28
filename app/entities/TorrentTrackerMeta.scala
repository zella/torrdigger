package entities

import play.api.libs.json.{Format, Json}

case class TorrentTrackerMeta(infoHash: String, name: String, category: String) //etc

object TorrentTrackerMeta {
  implicit val jsonFormat: Format[TorrentTrackerMeta] = Json.format[TorrentTrackerMeta]
}

