package entities

import play.api.libs.json.{Format, Json}

case class TorrentMeta(infoHash: String, numPeers: Int, name: String, files: Seq[TorrentFileMeta])

object TorrentMeta {
  implicit val jsonFormat: Format[TorrentMeta] = Json.format[TorrentMeta]
}

case class TorrentFileMeta(index: Int, length: Long, path: String)

object TorrentFileMeta {
  implicit val jsonFormat: Format[TorrentFileMeta] = Json.format[TorrentFileMeta]
}