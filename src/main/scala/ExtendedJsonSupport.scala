/**
  * Created by admin on 2017/12/1.
  */


import spray.json._
trait ExtendedJsonSupport extends DefaultJsonProtocol {

  //implicit val printer: JsonPrinter

  /*implicit object TimestampFormat extends JsonFormat[Timestamp] {
    def write(ts: Timestamp): JsValue = JsString(dateToIsoString(ts))
    def read(json: JsValue): Timestamp = json match {
      case JsString(s) => new Timestamp(parseIsoDateString(s).getTime)
      case other => deserializationError(s"Expected JsString, got $other")
    }
  }*/

  implicit object AnyJsonFormat extends JsonFormat[Any] {
    def write(x: Any): JsValue = x match {
      case null => JsNull
      case n: Int => JsNumber(n)
      case s: String => JsString(s)
      case b: Boolean => if (b) JsTrue else JsFalse
      case l: List[Any] => JsArray(l.toVector.map(v => write(v)))
      case m: Map[String, Any] => {
        JsObject(m.map { case (k, v) => (k, write(v)) })
      }
    }
    def read(value: JsValue): Any = value match {
      case JsNull => null
      case JsNumber(n) => n.intValue()
      case JsString(s) => s
      case JsTrue => true
      case JsFalse => false
      case JsArray(xs: Vector[JsValue]) => xs.toList.map { x => read(x) }
      case JsObject(fields: Map[String, JsValue]) => fields.map { case (k, jsv) => (k, read(jsv)) }
    }
  }
}

