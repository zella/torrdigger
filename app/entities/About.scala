package entities

import play.api.libs.json.{Format, Json}

case class About(messages: Int)

object About {
  implicit val jsonFormat: Format[About] = Json.format[About]
}
