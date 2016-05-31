import java.io.File

import org.scalatestplus.play._
import play.api.libs.Files.TemporaryFile
import play.api.libs.json.{JsDefined, JsNumber, JsValue}
import play.api.mvc.MultipartFormData
import play.api.mvc.MultipartFormData.FilePart
import play.api.test._
import play.api.test.Helpers._

import scala.io.Source

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

  "Rewards Controller" when {
    "GET /rewards" ignore {}

    "POST /rewards" should {
      "parse the input body and render the rewards" in {
        val file = new File("./test/resources/rewards.txt")
        val body = Source.fromFile(file).mkString

        val rewards = route(app, FakeRequest(POST, "/rewards").withBody(body)).get

        status(rewards) mustBe OK
        contentType(rewards) mustBe Some("application/json")

        val output = contentAsJson(rewards)
        (output \ "1").get mustBe JsNumber(2.5)
        (output \ "2").get mustBe JsNumber(0.0)
        (output \ "3").get mustBe JsNumber(1.0)
      }

      "render the errors when a bad request is sent" in {
        val rewards = route(app, FakeRequest(POST, "/rewards").withBody("a b")).get

        status(rewards) mustBe BAD_REQUEST
        contentType(rewards) mustBe Some("application/json")
        contentAsString(rewards) must include ("errors")
      }
    }

    "PUT /rewards" ignore {}
  }
}
