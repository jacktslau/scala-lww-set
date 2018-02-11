package crdt.test

import crdt.{Element, LWWSet}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import play.api.libs.json.Json

import scala.util.Random
import scala.concurrent.duration._

class ClientViewer extends Simulation {

  val httpConf = http.baseURL("http://localhost:9000")
  val path = s"/client-viewer-${System.currentTimeMillis()}"
  val values = 1 to 10 toArray  // random values

  val rnd = new Random

  protected def getLWWSet(session: Session): LWWSet[String] = {
    session("lwwset").asOption[LWWSet[String]].getOrElse(LWWSet())
  }

  val saveScenario = scenario("Client Update")
    .doIfOrElse(_ => rnd.nextInt(100) > 50) {
      exec(http("add_request")
        .post(path)
        .body(StringBody(s"""[{ "value": "${values(rnd.nextInt(values.size))}", "ts": ${System.currentTimeMillis} }]""")).asJSON
        .check(status.is(200))
      )
    } {
      exec(http("delete_request")
        .delete(path)
        .body(StringBody(s"""[{ "value": "${values(rnd.nextInt(values.size))}", "ts": ${System.currentTimeMillis()} }]""")).asJSON
        .check(status.is(200))
      )
    } pause(10 millisecond, 100 milliseconds)

  val viewScenario = scenario("Client Viewer")
      .during(10 seconds, exitASAP = true) {
        exec(http("get_request")
          .get(path)
          .check(status.is(200))
          .check(bodyString.saveAs("body"))
        )
        .pause(1 second)
        // TODO add sync to merge LWW-Set between server and local
      }


  setUp(
    viewScenario.inject(atOnceUsers(1)).protocols(httpConf),
    saveScenario.inject(constantUsersPerSec(5).during(5 seconds)).protocols(httpConf)
  )

}
