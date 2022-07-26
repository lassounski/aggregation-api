package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._


class AggregationSimulation extends Simulation {

  val feeder = csv("search.csv").random

  val aggregate =
      feed(feeder)
      .exec(
        http("Aggregate")
          .get(s"/aggregation?countryNames=#{countries}&shippingNumbers=#{shippingNums}&trackingNumbers=#{trackingNums}")
          .requestTimeout(15.seconds)
          .check(status.is(200))
      )
      .pause(1)

  val httpProtocol =
    http.baseUrl("http://localhost:8081")

  val users = scenario("Users").exec(aggregate)

  setUp(
    users.inject(
      atOnceUsers(20),
      rampUsers(100000) during (3600 second)
    )
  ).protocols(httpProtocol)
}
