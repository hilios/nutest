package forms

import play.api.data.Form
import play.api.data.Forms._

/**
  * Companion object to validate invites.
  */
object InvitesForm {

  /**
    * Tests if the invitation line is in the correct format.
    * @param invite The invite line
    * @return <true> if its valid
    */
  private def lineConstraint(invite: String): Boolean = invite.matches("^\\d+ \\d+$")

  /**
    * Returns an Form for invitations.
    */
  def apply() = Form(
    single(
      "invites" -> seq(text.verifying(lineConstraint(_)))
    )
  )
}
