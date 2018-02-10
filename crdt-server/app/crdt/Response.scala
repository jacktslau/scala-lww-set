package crdt

import play.api.libs.json.{Format, Json}

case class SaveResponse(inserted: Int)
case class ErrorResponse(code: String, message: String)

object SaveResponse {
  implicit lazy val format: Format[SaveResponse] = Json.format[SaveResponse]
}

object ErrorResponse {
  implicit lazy val format: Format[ErrorResponse] = Json.format[ErrorResponse]
}

object Errors {
  def BAD_REQUEST = ErrorResponse("", "")
}