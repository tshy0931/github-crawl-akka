package com.gitmining.crawl.akka.scheduler

import akka.actor.{Actor, ActorLogging, ActorRef, Terminated}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.routing.RoundRobinPool
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.gitmining.crawl.akka.crawler.Crawler
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._
import com.gitmining.crawl.akka.scheduler.Scheduler.Crawl
import com.gitmining.crawl.akka.utils.JsonFormat
import akka.http.scaladsl.unmarshalling.Unmarshal
/**
  * Each scheduler has multiple crawlers as child actors,
  * and manages crawling from specific GitHub APIs.
  */
class Scheduler extends Actor with ActorLogging with JsonFormat{

  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  val http = Http(context.system)
  val crawlerCount = 5
  val perPage = 1
//  val crawlers:ActorRef = context.actorOf(Crawler.props)
  val crawlers:ActorRef = context.actorOf(RoundRobinPool(crawlerCount).props(Crawler.props))

  context.watch(crawlers)

  override def receive: Receive = {
    case Crawl("/users", since, to) =>
      http.singleRequest(HttpRequest(GET, s"https://api.github.com/users?per_page=$perPage&since=$since")) foreach { resp =>
        Unmarshal(resp.entity).to[List[UserBrief]] foreach { users =>
          println(s"${users.size} users")
          users foreach { user =>
            println(user.id)
            crawlers ! user
          }
        }
        resp.headers filter {_.lowercaseName() == "link"} map {_.value()} foreach {
          log.info(_)
        }
      }
//      http.singleRequest(HttpRequest(GET, s"/users?per_page=$perPage&since=$")) pipeTo self

    case Terminated(actor) =>
      log.info(s"Actor ${actor.path} has terminated")
  }
}

object Scheduler {

  case class Crawl(url:String, since:Long, to:Long)
  case class Retry() // TODO: define how to retry those failed crawls

}
