import com.google.inject.Inject
import io.netty.handler.codec.http.HttpResponseStatus
import play.api.http.HttpErrorHandler
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc._
import play.api.{Environment, Mode}

import scala.concurrent._

/**
  * This class handles errors thrown by the application and returns the reason phrase as an JSON
  */
class ErrorHandler @Inject() (env: Environment) extends HttpErrorHandler {
  def reasonPhrase(statusCode: Int) = HttpResponseStatus.valueOf(statusCode).reasonPhrase

  def onClientError(request: RequestHeader, statusCode: Int, message: String) = {
    Future.successful(
      if (message == "") {
        Status(statusCode)(Json.obj("error" -> reasonPhrase(statusCode)))
      } else {
        Status(statusCode)(Json.obj("error" -> message))
      }
    )
  }

  def onServerError(request: RequestHeader, exception: Throwable) = {
    Future.successful(
      if (env.mode == Mode.Dev) {
        InternalServerError(Json.obj("error" -> exception.getMessage))
      } else {
        InternalServerError(Json.obj("error" -> reasonPhrase(500)))
      }
    )
  }
}
