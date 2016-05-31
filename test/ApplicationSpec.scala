import java.io.{File, FileInputStream, FileOutputStream}

import org.scalatestplus.play._
import play.api.libs.json.JsNumber
import play.api.test.Helpers._
import play.api.test._

/**
 * Test the application controllers.
 */
class ApplicationSpec extends PlaySpec with OneAppPerTest {

  val defaultFile = new File("./invites.txt")
  val templateFile = new File("./test/resources/rewards.txt")

  /** Helper method to copy files from one location to another */
  private def copyFile(src: File, dest: File) {
    if(!dest.exists()) {
      dest.createNewFile()
    }

    val input = new FileInputStream(src).getChannel
    val output = new FileOutputStream(dest).getChannel
    output.transferFrom(input, 0, input.size())
    input.close()
    output.close()
  }

  /**
    * Don't mess up with the invites file. Each test make a backup of any original file, setup with
    * a test template, an undo at the end of the test.
    */
  override def withFixture(test: NoArgTest) = {
    val bkpFile = new File("./invites.txt.bkp")
    // Make a backup
    copyFile(defaultFile, bkpFile)
    copyFile(templateFile, defaultFile)

    try super.withFixture(test)
    finally {
      // Undo then delete backup
      copyFile(bkpFile, defaultFile)
      bkpFile.delete()
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

  "RewardController" when {
    "GET /rewards" should {
      "render the current reward points" in {
        val rewards = route(app, FakeRequest(GET, "/rewards")).get

        status(rewards) mustBe OK
        contentType(rewards) mustBe Some("application/json")

        val output = contentAsJson(rewards)
        (output \ "1").get mustBe JsNumber(2.5)
        (output \ "2").get mustBe JsNumber(0.0)
        (output \ "3").get mustBe JsNumber(1.0)
      }
    }

    "POST /rewards" should {
      "parse the input body and render the rewards" in {
        val rewards = route(app, FakeRequest(POST, "/rewards").withBody(
          """
            |1 2
            |2 3
            |3 4
          """.stripMargin.trim)).get

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

    "PUT /rewards" should {
      "append new invites and render the updated rewards" in {
        val rewards = route(app, FakeRequest(PUT, "/rewards").withBody("5 7")).get

        status(rewards) mustBe OK
        contentType(rewards) mustBe Some("application/json")

        val output = contentAsJson(rewards)
        (output \ "1").get mustBe JsNumber(2.75)
        (output \ "3").get mustBe JsNumber(1.5)
        (output \ "4").get mustBe JsNumber(1)
        (output \ "5").get mustBe JsNumber(0)
      }

      "render errors when a bad request is sent" in {
        val rewards = route(app, FakeRequest(PUT, "/rewards").withBody("a b")).get

        status(rewards) mustBe BAD_REQUEST
        contentType(rewards) mustBe Some("application/json")
        contentAsString(rewards) must include ("errors")
      }
    }
  }

  "ErrorHandler" should {
    "send not found on a bad request" in  {
      val error = route(app, FakeRequest(GET, "/bad")).get

      status(error) mustBe NOT_FOUND
      contentType(error) mustBe Some("application/json")
      contentAsString(error) must include ("error")
    }
  }
}
