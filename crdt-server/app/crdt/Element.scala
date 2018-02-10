package crdt

import play.api.libs.json._

object ElementFormat {

  // Json formatter for Element
  implicit def fmt[T](implicit format: Format[T]): Format[Element[T]] = Json.format[Element[T]]

//  implicit def fmt[T](implicit format: Format[T]): Format[Element[T]] = new Format[Element[T]] {
//    def reads(json: JsValue): JsSuccess[Element[T]] = JsSuccess(new Element[T] (
//      (json \ "value").as[T],
//      (json \ "ts").as[Long]
//    ))
//
//    def writes(elem: Element[T]) = JsObject(Seq(
//      "ts" -> Json.toJson(elem.ts),
//      "value" -> Json.toJson(elem.value)
//    ))
//  }

}
