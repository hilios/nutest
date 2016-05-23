import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._

/**
 * Test the application controllers.
 */
class ApplicationSpec extends PlaySpec with OneAppPerTest {

  "Routes" should {
    "send 404 on a bad request as an JSON" in  {
      val error = route(app, FakeRequest(GET, "/boum")).get

      status(error) mustBe NOT_FOUND
      contentType(error) mustBe Some("application/json")
      contentAsString(error) must include ("error")
      contentAsString(error) must include ("error")
    }
  }

  "HomeController" should {
    "render the application name and version" in {
      val home = route(app, FakeRequest(GET, "/")).get

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      contentAsString(home) must include ("version")
      contentAsString(home) must include ("name")
    }
  }
}
