package controllers

import javax.inject.{Inject, Singleton}

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.{RewardService, RewardFormat}

import scala.io.Source

/**
  * Receives a text file with the input and exposes the ranking on a JSON format on a HTTP endpoint.
  * Also, create another endpoint to add a new invitation.
  */
@Singleton
class RewardController @Inject() extends Controller with RewardFormat {
  def read = Action {
    NotImplemented
  }

  def create = Action(parse.temporaryFile) { request =>
    val lines = Source.fromFile(request.body.file).getLines.map(_.split(" ") match {
      case Array(from, to) => (from.toInt, to.toInt)
    }).toSeq
    val rewards = RewardService(lines)

    Ok(Json.toJson(rewards))
  }

  def update = Action {
    NotImplemented
  }
}
