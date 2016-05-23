package controllers

import javax.inject.{Inject, Singleton}

import play.api.Environment
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

/**
  * This controller create actions to handle the CRUD of the reward system.
  * Write a program that receives a text file with the input and exposes the ranking on a JSON
  * format on a HTTP endpoint. Also, create another endpoint to add a new invitation.
  */
@Singleton
class RewardController @Inject() (rewar: Environment) extends Controller {
  def read = Action {
    NotImplemented
  }

  def create = Action {
    NotImplemented
  }
}
