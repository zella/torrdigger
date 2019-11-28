package service.impl

import java.util.concurrent.TimeUnit

import app.Implicits._
import com.github.zella.rxprocess2.RxProcess
import com.google.inject.{Inject, Singleton}
import com.zaxxer.nuprocess.NuProcessBuilder
import config.DiggerConfig
import entities.{TorrentHash, TorrentMeta}
import monix.eval.Task
import monix.reactive.Observable
import play.api.libs.json.Json
import service.SubProcesses

@Singleton
class DefaultSubProcess @Inject()(conf: DiggerConfig) extends SubProcesses {

  def evalTorrent(hash: String): Task[TorrentMeta] = {
    RxProcess
      .reactive(new NuProcessBuilder(conf.fetchTorrentExecutable: _*))
      .asStdOutSingle(conf.fetchTorrentTimeout.toSeconds, TimeUnit.SECONDS)
      .asTask
      .map(bytes => Json.parse(bytes).as[TorrentMeta])
  }

  def spider(): Observable[TorrentHash] = {
    RxProcess.reactive(new NuProcessBuilder(conf.spiderExecutable: _*))
      .asStdOut()
      .map(a => new String(a))
      .map(s => TorrentHash.create(s))
      .asObservable
      .timeoutOnSlowUpstream(conf.spiderTimeout)
      .onErrorRestartUnlimited
  }

}
