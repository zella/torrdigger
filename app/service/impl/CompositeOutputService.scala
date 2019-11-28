package service.impl

import akka.Done
import com.google.inject.Singleton
import entities.Torrent
import monix.eval.Task
import service.OutputService

@Singleton
class CompositeOutputService(outputs: Seq[OutputService]) extends OutputService {
  override def outputTorrent(torrent: Torrent): Task[Done] = {
    Task.sequence(outputs.map(_.outputTorrent(torrent))).map(_ => Done)
  }
}

