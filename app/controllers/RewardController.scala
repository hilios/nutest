package controllers

import javax.inject.{Inject, Singleton}

import forms.InvitesForm
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.{InviteService, RewardFormat, RewardService}

/**
  * Receives a text file with the input and exposes the ranking on a JSON format on a HTTP endpoint.
  * Also, create another endpoint to add a new invitation.
  */
@Singleton
class RewardController @Inject() (val messagesApi: MessagesApi) extends Controller
    with I18nSupport with RewardFormat {

  /**
    * Renders the current reward points.
    */
  def read = Action {
    val invites = InviteService.get()
    val rewards = RewardService(invites)
    Ok(Json.toJson(rewards))
  }

  /**
    * Overwrite the invites and render the reward points.
    */
  def create = Action(parse.temporaryFile) { request =>
    val data = InviteService.load(request.body.file)

    InvitesForm().fillAndValidate(data).fold(
      formWithErrors => {
        BadRequest(Json.obj("errors" -> formWithErrors.errorsAsJson))
      },
      form => {
        // Save this file
        val invites = InviteService.save(request.body)
        val rewards = RewardService(invites)
        Ok(Json.toJson(rewards))
      }
    )
  }

  /**
    * Update the invites and render the reward points.
    */
  def update = Action { request =>
    val data = request.body.asText.getOrElse("").split("\n")

    InvitesForm().fillAndValidate(data).fold(
      formWithErrors => {
        BadRequest(Json.obj("errors" -> formWithErrors.errorsAsJson))
      },
      form => {
        // Write each invite to the file
        val invites = InviteService.add(data)
        val rewards = RewardService(invites)
        Ok(Json.toJson(rewards))
      }
    )
  }
}
