import akka.actor.{ActorLogging, ActorSystem}
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import spray.http.{StatusCode, StatusCodes}
import spray.routing.SimpleRoutingApp
import spray.httpx.marshalling.ToResponseMarshallable.isMarshallable
import spray.routing.Directive.pimpApply

object DummyBidder extends App {
    new DummyBidder().start(8090, 5.5f)
}

class DummyBidder extends SimpleRoutingApp  {
  implicit val system = ActorSystem("my-system")
  val logger = Logger(LoggerFactory.getLogger(this.getClass))

  val bidResponse = BidderUtil.readFile("bid_response.json")

  def start(port: Int, price: => Float) = {
    startServer(interface = "0.0.0.0", port = port) {
      path("bidresponse") {
        post { ctx =>
          ctx.complete {
            println(ctx.request.entity.asString)
            logger.debug(s"===> ${ctx.request.entity.asString}")
            bidResponse.replaceAll("\\$price", price.toString)
          }
        }
      }
    }
  }

  def shutdown() = {
    system.shutdown()
    system.awaitTermination()
  }
}
