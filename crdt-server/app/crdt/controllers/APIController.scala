package crdt.controllers

import crdt.ErrorResponse
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.Future

trait APIController { self: BaseController =>

  protected lazy val logger = Logger(getClass)

  def api(f: Request[AnyContent] => Future[Result]): Action[AnyContent] = Action.async { implicit req =>
    try {
      f(req)
    } catch {
      case e: Throwable =>
        logger.error("Error thrown", e)
        val resp = ErrorResponse("sys.error", "Internal Server Error")
        Future.successful(InternalServerError(Json.toJson(resp)))
    }

  }

}
