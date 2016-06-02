import java.io.File

import helpers.MultipartFormDataWritable
import org.scalatest.TestData
import org.scalatestplus.play._
import play.api.Application
import play.api.cache.CacheApi
import play.api.http.Writeable
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.Files.TemporaryFile
import play.api.libs.json.JsNumber
import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc.{AnyContentAsMultipartFormData, MultipartFormData}
import play.api.test.Helpers._
import play.api.test._
import services.{InviteService, InviteService$}

import scala.io.Source

/**
 * Test the application controllers.
 */
class ApplicationSpec extends PlaySpec with OneAppPerTest {

  val invites = Seq((1,2), (1,3), (3,4), (2,4), (4,5), (4,6))

  implicit val anyContentAsMultipartFormWritable: Writeable[AnyContentAsMultipartFormData] = {
    MultipartFormDataWritable.singleton.map(_.mdf)
  }

  /**
    * At each test setup a the invitations.
    */
  override def newAppForTest(testData: TestData): Application = {
    val app = new GuiceApplicationBuilder().build()

    // Setup base invites
    val inviteService = app.injector.instanceOf[InviteService]
    inviteService.set(invites)

    app
  }

  "HomeController" should {
    "render the application name and version" in {
      val home = route(app, FakeRequest(GET, "/")).get

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      contentAsString(home) must include ("version")
      contentAsString(home) must include ("name")
    }

    "allow cross-origin resource sharing" in {
      val home = route(app, FakeRequest(GET, "/")
        .withHeaders(ORIGIN -> "http://localhost:9000")
        .withHeaders(HOST -> "www.example.com")
      ).get

      status(home) mustBe OK
      headers(home) must contain ("Access-Control-Allow-Origin" -> "http://localhost:9000")
    }
  }

  "RewardController" when {
    "GET /rewards" should {
      "render the current reward points" in {
        val rewards = route(app, FakeRequest(GET, "/rewards")).get

        status(rewards) mustBe OK
        contentType(rewards) mustBe Some("application/json")

        val output = contentAsJson(rewards)
        (output \ "1").toOption mustBe Some(JsNumber(2.5))
        (output \ "2").toOption mustBe Some(JsNumber(0.0))
        (output \ "3").toOption mustBe Some(JsNumber(1.0))
      }
    }

    "POST /rewards" should {
      val uploadFile = new File("./test/resources/upload.txt")
      val wrongFile = new File("./test/resources/wrong.txt")

      "parse the input body and render the rewards" in {
        val filePart = FilePart("invites", "a.txt", Some("text/plain"), TemporaryFile(uploadFile))

        val form = MultipartFormData(Map.empty, Seq(filePart), Seq.empty)
        val rewards = route(app, FakeRequest(POST, "/rewards").withMultipartFormDataBody(form)).get

        status(rewards) mustBe OK
        contentType(rewards) mustBe Some("application/json")

        val output = contentAsJson(rewards)
        (output \ "1").toOption mustBe Some(JsNumber(1.5))
        (output \ "2").toOption mustBe Some(JsNumber(1.0))
        (output \ "3").toOption mustBe Some(JsNumber(0.0))
        (output \ "4").toOption mustBe Some(JsNumber(0.0))
      }

      "render an error when a bad formatted file is sent" in {
        val filePart = FilePart("invites", "a.txt", Some("text/plain"), TemporaryFile(wrongFile))

        val form = MultipartFormData(Map.empty, Seq(filePart), Seq.empty)
        val rewards = route(app, FakeRequest(POST, "/rewards").withMultipartFormDataBody(form)).get
      }

      "render an error when a bad request is sent" in {
        val filePart = FilePart("invites", "a.jpg", Some("image/jpeg"), TemporaryFile())

        val form = MultipartFormData(Map.empty, Seq(filePart), Seq.empty)
        val rewards = route(app, FakeRequest(POST, "/rewards").withMultipartFormDataBody(form)).get

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
        (output \ "1").toOption mustBe Some(JsNumber(2.75))
        (output \ "3").toOption mustBe Some(JsNumber(1.5))
        (output \ "4").toOption mustBe Some(JsNumber(1))
        (output \ "5").toOption mustBe Some(JsNumber(0))
      }

      "render errors when a bad request is sent" in {
        val rewards = route(app, FakeRequest(PUT, "/rewards").withBody("a b")).get

        status(rewards) mustBe BAD_REQUEST
        contentType(rewards) mustBe Some("application/json")
        contentAsString(rewards) must include ("errors")
      }
    }

    "DELETE /reward" should {
      "clear all invites" in {
        val delete = route(app, FakeRequest(DELETE, "/rewards")).get
        status(delete) mustBe OK

        val rewards = route(app, FakeRequest(GET, "/rewards")).get
        status(rewards) mustBe OK
        contentType(rewards) mustBe Some("application/json")

        val output = contentAsJson(rewards)
        (output \ "1").toOption mustBe None
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
