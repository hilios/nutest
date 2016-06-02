package services

import org.scalatestplus.play.PlaySpec

class InviteServiceSpec extends PlaySpec {
  "InviteService" when {
    "#parse" should {
      "split string lines into a sequence of tuple" in {
        val seq = InviteService.parse(
          """
            |1 2
            |
            |2 3
            |
            |3 4
          """.stripMargin)

        seq mustBe Seq((1,2), (2,3), (3,4))

        val singleLine = InviteService.parse("1 2")
        singleLine mustBe Seq((1,2))
      }
    }
  }
}
