package crdt.test

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.util.Random

class NormalChaosMonkeys extends Simulation {

  val httpConf = http.baseURL("http://localhost:9000")
  val path = s"/normal-chaos-${System.currentTimeMillis()}"
  val values = 1 to 10 toArray  // random values

  val rnd = new Random

  val scn = scenario("Normal Chaos Monkeys")
          .doIfOrElse(_ => rnd.nextInt(100) > 50) { // random to add / remove elements
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
          } pause(1 millisecond, 100 milliseconds)  // pause 1 ms to 100 ms


  setUp(scn.inject(constantUsersPerSec(5).during(5 seconds))).protocols(httpConf)
}
