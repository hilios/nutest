package services

import org.scalatest.WordSpec
import org.scalatestplus.play.PlaySpec

/**
  * Given the input:
  * 1 2
  * 1 3
  * 3 4
  * 2 4
  * 4 5
  * 4 6
  *
  * The score is:
  * 1 - 2.5 (2 because he invited 2 and 3 plus 0.5 as 3 invited 4)
  * 3 - 1 (1 as 3 invited 4 and 4 invited someone)
  * 2 - 0 (even as 2 invited 4, it doesn't count as 4 was invited before by 3)
  * 4 - 0 (invited 5 and 6, but 5 and 6 didn't invite anyone)
  * 5 - 0 (no further invites)
  * 6 - 0 (no further invites)
  *
  * Note that 2 invited 4, but, since 3 invited 4 first, customer 3 gets the points.
  */
class RewardServiceSpec extends PlaySpec {
  "RewardService" should {
    "do something" in {
      true must be(true)
    }
  }
}
