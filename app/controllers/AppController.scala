package controllers

import java.time.Instant

import com.typesafe.scalalogging.LazyLogging
import dao.MqDao
import entities.{About, TorrentHash, TorrentMessage}
import javax.inject._
import monix.execution.Scheduler
import play.api.libs.json.Json
import play.api.mvc._

@Singleton
class AppController @Inject()(cc: ControllerComponents, dao: MqDao)(implicit val scheduler: Scheduler)
  extends AbstractController(cc) with LazyLogging {

  lazy val OkStatus =  Ok(Json.obj("status" -> "ok"))

  def putIntoQueue(hash: String): Action[AnyContent] = Action.async {
    logger.debug(s"Put into queue: $hash")
    dao.exec(dao.push(TorrentMessage(TorrentHash(hash), Instant.now())))
      .map(_ => OkStatus)
      .runToFuture
  }

  def about(): Action[AnyContent] =  Action.async {
    dao.exec(dao.count())
      .map(c => Ok(Json.toJson(About(c))))
      .runToFuture
  }

}
