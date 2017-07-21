package com.gitmining.crawl.akka.crawler

import akka.actor.{Actor, ActorLogging, Props}
import akka.dispatch.{BoundedMessageQueueSemantics, RequiresMessageQueue}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken, `User-Agent`}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.gitmining.crawl.akka.crawler.Worker.Repos
import com.gitmining.crawl.akka.utils.JsonFormat
import com.typesafe.config.ConfigFactory
/**
  * Created by weiwang on 14/07/17.
  */
object Worker {
  case class Repos(ownerId:Long, repos_url:String, page:Int)
  case class Orgs(userId:Long)
  case class Subscriptions(userId:Long)
  case class Followers(userId:Long)
  case class Following(userId:Long)
  case class Starred(userId:Long)

  def props = Props[Worker]
}
class Worker extends Actor with ActorLogging with JsonFormat {
//  with RequiresMessageQueue[BoundedMessageQueueSemantics] {
  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  val http = Http(context.system)
  val defaultHeaders = List(
    `User-Agent`("tshy0931"),//crawlerConfig.getString("user-agent")),
    Authorization(OAuth2BearerToken("452252c32694ce84a4fd047cbf9b94b2df220917"))//crawlerConfig.getString("personal-access-token")))
  )

  override def receive: Receive = {
    case Repos(id, repos_url, page) =>
      for{
        resp <- http.singleRequest(HttpRequest(GET, s"$repos_url?per_page=100&page=$page", defaultHeaders))
      }yield{
        println(s"$repos_url?per_page=100&page=$page -> ${resp.status}")
        Unmarshal(resp.entity).to[List[Repo]] foreach { repos =>
          println(s"${repos.size} repos for user $id")
          repos foreach { repo =>
            println(repo)
          }
        }
      }
  }
}
