package controllers

import javax.inject._

import play.api._
import play.api.libs.json.Json
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() extends Controller {

  /**
   * Returns the application status.
   */
  def index = Action {
    Ok(Json.obj("status" -> "OK"))
  }

}
