package crdt.controllers

import javax.inject._

import crdt._
import crdt.ElementFormat._
import crdt.services.LWWSetService
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class LWWSetController @Inject()(service: LWWSetService, cc: ControllerComponents) extends AbstractController(cc) with APIController {

  /** Add / Remove Function of LWW-Element Set based on the body
    *
    * @param key   LWW-Set key
    * @param isAdd boolean to determine to add or remove value
    * @return
    */
  def save(key: String, isAdd: Boolean) = api { implicit req =>
    val resultOpt = for {
      json <- req.body.asJson
      elem <- json.asOpt[Element[String]]
    } yield {
      if(isAdd) service.add(key, elem)
      else service.remove(key, elem)
    }

    resultOpt match {
      case Some(resultFuture) =>
        resultFuture.map { success =>
          if(success) {
            val resp = SaveResponse(1)
            Ok(Json.toJson(resp))
          } else {
            val resp = ErrorResponse("ds.error", "Fail to write into redis")
            InternalServerError(Json.toJson(resp))
          }
        }

      case None =>
        val resp = ErrorResponse("param.error", "Unable to parse the body")
        Future.successful(BadRequest(Json.toJson(resp)))
    }
  }


  /** Computes the current state of LWW-Set
    *
    * @param key LWW-Set key
    * @return
    */
  def lookup(key: String) = api { implicit req =>
    service.get(key).map {
      case Some(lwwSet) =>
        val elems = lwwSet.lookupElements
        val json = Json.toJson(elems)
        Ok(json)

      case None =>
        // return empty array if key not found
        Ok(Json.arr())
    }
  }

}
