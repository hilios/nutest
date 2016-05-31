package forms

import play.api.data.Form
import play.api.data.Forms._

/**
  * Companion object to validate invites requests.
  */
object InvitesForm {

  /**
    * Tests if the invitation line is in the correct format.
    * @param invite The invite line
    * @return
    */
  private def lineConstraint(invite: String): Boolean = {
    invite.matches("^\\d+ \\d+$")
  }

  /**
    * Returns an Form for invitations.
    * @param any
    * @return
    */
  def apply(any: Any = "") = Form(
    single(
      "invites" -> seq(text.verifying(lineConstraint(_)))
    )
  )
}
