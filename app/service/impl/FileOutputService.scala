package service.impl

import java.time.Instant
import java.time.format.DateTimeFormatter

import akka.Done
import better.files.File
import com.google.inject.Singleton
import entities.Torrent
import monix.eval.Task
import play.api.libs.json.Json
import service.OutputService

@Singleton
class FileOutputService(baseFilePath: String) extends OutputService {

  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  override def outputTorrent(torrent: Torrent): Task[Done] = Task {
    val file = File.apply(baseFilePath + "_" + formatter.format(Instant.now()))
    file.appendLine(Json.stringify(Json.toJson(torrent)))
  }.map(_ => Done)

}
