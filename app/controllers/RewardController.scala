package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc.{Action, Controller}

/**
  * Receives a text file with the input and exposes the ranking on a JSON format on a HTTP endpoint.
  * Also, create another endpoint to add a new invitation.
  */
@Singleton
class RewardController @Inject() extends Controller {

  def read = Action {
    NotImplemented
  }

  def create = Action {
    NotImplemented
  }

  def update = Action {
    NotImplemented
  }
}
