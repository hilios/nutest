package controllers

import javax.inject.{Inject, Singleton}

import forms.InvitesForm
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.{InviteService, RewardFormat, RewardService}

import scala.io.Source

/**
  * Receives a text file with the input and exposes the ranking on a JSON format on a HTTP endpoint.
  * Also, create another endpoint to add a new invitation.
  */
@Singleton
class RewardController @Inject() (val messagesApi: MessagesApi, inviteService: InviteService)
    extends Controller with I18nSupport with RewardFormat {

  /**
    * Renders the current reward points.
    */
  def read = Action {
    val invites = inviteService.get
    val rewards = RewardService(invites)
    Ok(Json.toJson(rewards))
  }

  /**
    * Overwrite the invites and render the reward points.
    */
  def create = Action(parse.multipartFormData) { request =>
    request.body.file("invites").filter(_.contentType.getOrElse("") == "text/plain").map { upload =>
      val data = Source.fromFile(upload.ref.file).mkString

      InvitesForm().fillAndValidate(data).fold(
        formWithErrors => {
          BadRequest(Json.obj("errors" -> formWithErrors.errorsAsJson))
        },
        form => {
          val invites = InviteService.parse(form)
          inviteService.set(invites)

          val rewards = RewardService(inviteService.get)
          Ok(Json.toJson(rewards))
        }
      )
    }.getOrElse {
      BadRequest(Json.obj("errors" -> "Missing text file"))
    }
  }

  /**
    * Update the invites and render the reward points.
    */
  def update = Action(parse.tolerantText) { request =>
    InvitesForm().fillAndValidate(request.body).fold(
      formWithErrors => {
        BadRequest(Json.obj("errors" -> formWithErrors.errorsAsJson))
      },
      form => {
        val invites = InviteService.parse(form)
        inviteService.add(invites)

        val rewards = RewardService(inviteService.get)
        Ok(Json.toJson(rewards))
      }
    )
  }

  /**
    * Clear all invites.
    * @return
    */
  def delete = Action {
    inviteService.clear()
    Ok
  }
}
