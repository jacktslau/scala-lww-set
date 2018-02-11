package crdt.test

import crdt.{Element, LWWSet}
import crdt.ElementFormat._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import play.api.libs.json.Json

import scala.util.Random
import scala.concurrent.duration._

class OfflineOnlineSync extends Simulation {

  val httpConf = http.baseURL("http://localhost:9000")
  val path = s"/offline-online-${System.currentTimeMillis()}"
  val values = 1 to 10 toArray  // random values

  val rnd = new Random

  protected def getLWWSet(session: Session): LWWSet[String] = {
    session("lwwset").asOption[LWWSet[String]].getOrElse(LWWSet())
  }

  protected def randomElement: Element[String] = {
    Element(values(rnd.nextInt(values.size)).toString)
  }

  val scn = scenario("Offline Online Sync")
      .repeat(20) {
        // random generate lwwset
        doIfOrElse(_ => rnd.nextInt(100) > 50) {
          exec { session =>
            val set = getLWWSet(session).add(randomElement)
            session.set("lwwset", set)
          }
        } {
          exec { session =>
            val set = getLWWSet(session).remove(randomElement)
            session.set("lwwset", set)
          }
        }
      }
      // online sync
      .doIf(s => !getLWWSet(s).addSet.isEmpty) {
        exec { session =>
          val elems = getLWWSet(session).addSet
          val body = Json.toJson(elems).toString()
          session.set("requestBody", body)
        }
        .exec(http("add_request")
          .post(path)
          .body(StringBody("${requestBody}")).asJSON
          .check(status.is(200))
        )
      }
      .doIf(s => !getLWWSet(s).removeSet.isEmpty) {
        exec { session =>
          val elems = getLWWSet(session).removeSet
          val body = Json.toJson(elems).toString
          session.set("requestBody", body)
        }
        .exec(http("delete_request")
            .delete(path)
            .body(StringBody("${requestBody}")).asJSON
            .check(status.is(200))
        )
      }


  setUp(scn.inject(constantUsersPerSec(5).during(5 seconds))).protocols(httpConf)

}
