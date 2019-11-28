package entities

import java.time.Instant

case class TorrentMessage(hash: TorrentHash, time: Instant, failures: Int = 0, failTime: Instant = Instant.EPOCH)
