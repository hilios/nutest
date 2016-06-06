package forms

import play.api.data.Form
import play.api.data.Forms._
import services.InviteService

/**
  * Companion object to validate invites.
  */
object InvitesForm {

  /**
    * Tests if the invitation line is in the correct format.
    * @param invite The invite line
    * @return <true> if its valid
    */
  private def lineConstraint(invite: String): Boolean = {
    invite.trim.split("\n").exists(_.matches("^\\d+ \\d+$"))
  }

  /**
    * Returns an Form for invitations.
    */
  def apply() = Form(
    single(
      "invites" -> text.verifying("Wrong format", lineConstraint(_))
        .transform[Seq[(Int, Int)]](
          InviteService.parse, _.map(x => s"${x._1} ${x._2}").mkString("\n")
        )
    )
  )
}
