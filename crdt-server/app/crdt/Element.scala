package crdt

import play.api.libs.json._

object ElementFormat {

  // Json formatter for Element
  implicit def fmt[T](implicit format: Format[T]): Format[Element[T]] = Json.format[Element[T]]

}
