import com.google.inject.Inject
import io.netty.handler.codec.http.HttpResponseStatus
import play.api.http.HttpErrorHandler
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc._
import play.api.{Environment, Logger, Mode}

import scala.concurrent._

/**
  * This class handles errors thrown by the application and returns the reason as an JSON.
  */
class ErrorHandler @Inject() (env: Environment) extends HttpErrorHandler {

  /**
    * Returns a humanized phrase from an HTTP status code.
    *
    * @param statusCode The HTTP status code
    * @return The human readable phrase
    */
  def reasonPhrase(statusCode: Int): String = HttpResponseStatus.valueOf(statusCode).reasonPhrase

  /**
    * Returns an error output as an JSON string (HTTP status code not between 200-299).
    *
    * @param requestHeader The request header
    * @param statusCode The HTTP status code
    * @param message The error message
    * @return The error JSON output
    */
  def onClientError(requestHeader: RequestHeader, statusCode: Int, message: String) = {
    val reason: String = if (message.isEmpty) reasonPhrase(statusCode) else message
    Future.successful(
      Status(statusCode)(Json.obj("error" -> reason))
    )
  }

  /**
    * Returns an error output as an JSON string.
    *
    * @param requestHeader The request header
    * @param exception The exception object
    * @return The error JSON output
    */
  def onServerError(requestHeader: RequestHeader, exception: Throwable) = {
    Logger.error(exception.getMessage, exception)

    val reason = reasonPhrase(HttpResponseStatus.INTERNAL_SERVER_ERROR.code)
    Future.successful(
      InternalServerError(Json.obj("error" -> reason))
    )
  }
}
