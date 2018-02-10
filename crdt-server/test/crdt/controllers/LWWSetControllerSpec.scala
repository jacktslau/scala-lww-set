package crdt.controllers

import crdt.services.{LWWSetService, MockLWWSetServiceImpl}
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.inject._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers._
import play.api.test._

class LWWSetControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  override def fakeApplication() = new GuiceApplicationBuilder()
    .bindings(bind[LWWSetService].to[MockLWWSetServiceImpl])
    .build()


  "LWWSetController POST" should {

    "return 200 OK and record of saved if added successfully" in {
      val key = "success"
      val service = inject[LWWSetService]

      val controller = new LWWSetController(service, stubControllerComponents())
      val body: JsValue = Json.obj(
        "ts" -> 100,
        "value" -> "test"
      )
      val request = FakeRequest(POST, s"/$key").withJsonBody(body)
      val result = controller.save(key, true).apply(request)

      val expectedResult = Json.obj(
        "inserted" -> 1
      )

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      contentAsJson(result) mustBe expectedResult
    }

    "return 400 Bad Request if request is in wrong format" in {
      val key = "success"
      val service = inject[LWWSetService]

      val controller = new LWWSetController(service, stubControllerComponents())
      val body: JsValue = Json.obj(
        "something" -> "wrong-type"
      )
      val request = FakeRequest(POST, s"/$key").withJsonBody(body)
      val result = controller.save(key, true).apply(request)

      status(result) mustBe BAD_REQUEST
      contentType(result) mustBe Some("application/json")
      contentAsString(result) must include ("param.error")
    }

    "return 500 Error if record is saved unsuccessfully" in {
      val key = "fail"
      val service = inject[LWWSetService]

      val controller = new LWWSetController(service, stubControllerComponents())
      val body: JsValue = Json.obj(
        "ts" -> 100,
        "value" -> "test"
      )
      val request = FakeRequest(POST, s"/$key").withJsonBody(body)
      val result = controller.save(key, true).apply(request)

      status(result) mustBe INTERNAL_SERVER_ERROR
      contentType(result) mustBe Some("application/json")
      contentAsString(result) must include ("ds.error")
    }

    "return 500 Error if service has thrown exception" in {
      val key = "exception"
      val service = inject[LWWSetService]

      val controller = new LWWSetController(service, stubControllerComponents())
      val body: JsValue = Json.obj(
        "ts" -> 100,
        "value" -> "test"
      )
      val request = FakeRequest(POST, s"/$key").withJsonBody(body)
      val result = controller.save(key, true).apply(request)

      status(result) mustBe INTERNAL_SERVER_ERROR
      contentType(result) mustBe Some("application/json")
      contentAsString(result) must include ("sys.error")
    }
  }

  "LWWSetController DELETE" should {

    "return 200 OK and record of saved if remoed successfully" in {
      val key = "success"
      val service = inject[LWWSetService]

      val controller = new LWWSetController(service, stubControllerComponents())
      val body: JsValue = Json.obj(
        "ts" -> 100,
        "value" -> "test"
      )
      val request = FakeRequest(DELETE, s"/$key").withJsonBody(body)
      val result = controller.save(key, false).apply(request)

      val expectedResult = Json.obj(
        "inserted" -> 1
      )

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      contentAsJson(result) mustBe expectedResult
    }

    "return 400 Bad Request if request is in wrong format" in {
      val key = "success"
      val service = inject[LWWSetService]

      val controller = new LWWSetController(service, stubControllerComponents())
      val body: JsValue = Json.obj(
        "something" -> "wrong-type"
      )
      val request = FakeRequest(DELETE, s"/$key").withJsonBody(body)
      val result = controller.save(key, false).apply(request)

      status(result) mustBe BAD_REQUEST
      contentType(result) mustBe Some("application/json")
      contentAsString(result) must include ("param.error")
    }

    "return 500 Error if record is removed unsuccessfully" in {
      val key = "fail"
      val service = inject[LWWSetService]

      val controller = new LWWSetController(service, stubControllerComponents())
      val body: JsValue = Json.obj(
        "ts" -> 100,
        "value" -> "test"
      )
      val request = FakeRequest(DELETE, s"/$key").withJsonBody(body)
      val result = controller.save(key, false).apply(request)

      status(result) mustBe INTERNAL_SERVER_ERROR
      contentType(result) mustBe Some("application/json")
      contentAsString(result) must include ("ds.error")
    }

    "return 500 Error if service has thrown exception" in {
      val key = "exception"
      val service = inject[LWWSetService]

      val controller = new LWWSetController(service, stubControllerComponents())
      val body: JsValue = Json.obj(
        "ts" -> 100,
        "value" -> "test"
      )
      val request = FakeRequest(DELETE, s"/$key").withJsonBody(body)
      val result = controller.save(key, false).apply(request)

      status(result) mustBe INTERNAL_SERVER_ERROR
      contentType(result) mustBe Some("application/json")
      contentAsString(result) must include ("sys.error")
    }
  }

  "LWWSetController GET" should {

    "return 200 OK and lookup elements" in {
      val key = "set"
      val service = inject[LWWSetService]

      val controller = new LWWSetController(service, stubControllerComponents())
      val request = FakeRequest(GET, s"/$key")
      val result = controller.lookup(key).apply(request)

      val expectedResult = Json.arr(
        Json.obj(
          "ts" -> 1,
          "value" -> "test"
        )
      )

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      contentAsJson(result) mustBe expectedResult
    }

    "return 200 OK and empty elements" in {
      val key = "empty"
      val service = inject[LWWSetService]

      val controller = new LWWSetController(service, stubControllerComponents())
      val request = FakeRequest(GET, s"/$key")
      val result = controller.lookup(key).apply(request)

      val expectedResult = Json.arr()

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      contentAsJson(result) mustBe expectedResult
    }

    "return 200 OK and empty elements if key not exists" in {
      val key = "other"
      val service = inject[LWWSetService]

      val controller = new LWWSetController(service, stubControllerComponents())
      val request = FakeRequest(GET, s"/$key")
      val result = controller.lookup(key).apply(request)

      val expectedResult = Json.arr()

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      contentAsJson(result) mustBe expectedResult
    }
  }
}
