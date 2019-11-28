package entities

import slick.jdbc.SQLiteProfile.api._

case class TorrentHash private(hash: String)

object TorrentHash {

  implicit val torrentHashColumnType = MappedColumnType.base[TorrentHash, String](
    { case TorrentHash(hash) => hash.toLowerCase },
    hash => TorrentHash(hash)
  )

  def create(hash: String) = TorrentHash(hash.toLowerCase)
}
