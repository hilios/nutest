package controllers

import javax.inject._

import play.api._
import play.api.libs.json.Json
import play.api.mvc._


/**
 * This controller creates an action to to showoff the application's information.
 */
@Singleton
class HomeController @Inject() (env: Environment, conf: Configuration) extends Controller {
  /**
   * Returns the application status.
   */
  def index = Action {
    Ok(Json.obj("name" -> conf.getString("app.name"), "version" -> conf.getString("app.version"))
  }
}
