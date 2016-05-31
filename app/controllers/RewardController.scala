package controllers

import javax.inject.{Inject, Singleton}

import forms.InvitesForm
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.{RewardFormat, RewardService}

import scala.io.Source

/**
  * Receives a text file with the input and exposes the ranking on a JSON format on a HTTP endpoint.
  * Also, create another endpoint to add a new invitation.
  */
@Singleton
class RewardController @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport
    with RewardFormat {

  def read = Action {
    NotImplemented
  }

  def create = Action(parse.temporaryFile) { request =>
    val file = Source.fromFile(request.body.file)
    val data = file.getLines.map(_.toString).toList

    InvitesForm().fillAndValidate(data).fold(
      formWithErrors => {
        BadRequest(Json.obj("errors" -> formWithErrors.errorsAsJson))
      },
      invites => {
        val rewards = RewardService(invites.map(_.split(" ") match {
          case Array(from, to, other @ _*) => (from.toInt, to.toInt)
        }))

        Ok(Json.toJson(rewards))
      }
    )
  }

  def update = Action {
    NotImplemented
  }
}
