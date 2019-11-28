package service.impl

import java.time.Instant
import java.util.concurrent.TimeoutException

import akka.Done
import com.google.inject.{Inject, Singleton}
import com.typesafe.scalalogging.LazyLogging
import common.MessageHandler
import entities.{Torrent, TorrentMessage, TorrentMeta}
import monix.eval.Task
import monix.reactive.Observable
import service._

import scala.util.control.NonFatal

@Singleton
class TorrentMessageHandler @Inject()(queue: SqliteQueueService,
                                      subProc: SubProcesses,
                                      trackerMeta: FetchTrackerMeta,
                                      outputService: OutputService,
                                      webSearch: WebSearch) extends MessageHandler[TorrentMessage] with LazyLogging {

  val FetchDhtParallelism = 2
  val FetchTrackerParallelism = 2
  val WebSearchParallelism = 1
  val OutputParallelism = 1

  private def evalTorrentBackToQueue(hash: TorrentMessage): Task[TorrentMeta] = {
    subProc.evalTorrent(hash.hash.hash).onErrorRecoverWith {
      case NonFatal(e: TimeoutException) =>
        queue.failMessages(Seq(hash))
          .flatMap(_ => Task.raiseError(e))
    }
  }

  private def pushFromWebSearch(meta: TorrentMeta): Task[Done] = {
    for {
      hashes <- webSearch.searchSafe(meta.name)
      _ <- Task.sequence(hashes.map(h => queue.push(TorrentMessage(h, Instant.now()))))
    } yield Done
  }

  override def handle(messages: Seq[TorrentMessage]): Task[Boolean] = {
    Observable.fromIterable(messages)
      .mapParallelUnordered(FetchDhtParallelism)(message => evalTorrentBackToQueue(message))
      .mapParallelUnordered(WebSearchParallelism)(meta => pushFromWebSearch(meta).map((meta, _)))
      .mapParallelUnordered(FetchTrackerParallelism) { case (meta, _) => trackerMeta.crawlSafe(meta.infoHash).map((meta, _)) }
      .map { case (meta, maybeTrackerMeta) => Torrent.from(meta, maybeTrackerMeta) }
      .mapParallelUnordered(OutputParallelism) { torrent => outputService.outputTorrent(torrent) }
      .toListL.map(_ => true)
  }
}
