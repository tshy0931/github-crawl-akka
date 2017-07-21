package com.gitmining.crawl.akka.crawler

import akka.actor.{Actor, ActorLogging, Props, Terminated}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpHeader, HttpRequest, HttpResponse, StatusCodes}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import akka.http.scaladsl.model.HttpMethods._
import com.gitmining.crawl.akka.utils.JsonFormat
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.routing.RoundRobinPool
import com.gitmining.crawl.akka.crawler.Worker.Repos

/**
  * Created by weiwang on 09/07/17.
  */
object Crawler {

  def isLastPage(headers:Seq[HttpHeader]):Boolean = {
    headers.contains(Link)
  }

  def props = Props[Crawler]
}

class Crawler extends Actor with ActorLogging with JsonFormat{

  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  val http = Http(context.system)
//  val crawlerConfig = ConfigFactory.load("gitmining-crawl-akka")
  val defaultHeaders = List(
    `User-Agent`("tshy0931"),//crawlerConfig.getString("user-agent")),
    Authorization(OAuth2BearerToken("452252c32694ce84a4fd047cbf9b94b2df220917"))//crawlerConfig.getString("personal-access-token")))
  )

//  val workers = context.actorOf(Worker.props)
  private val workers = context.actorOf(RoundRobinPool(5).props(Worker.props))

  override def receive: Receive = {
    case UserBrief(
    login, id, url, followers_url, following_url, starred_url, subscriptions_url,
    organizations_url, repos_url, _, site_admin)  =>
//      workers ! Repos(id, repos_url, 1)
//      workers ! Orgs(id)
//      workers ! Subscriptions(id)
//      workers ! Followers(id)
//      workers ! Following(id)
//      workers ! Starred(id)
      for{
        userResp <- http.singleRequest(HttpRequest(GET, url, defaultHeaders))
//        followersResp <- http.singleRequest(HttpRequest(GET, followers_url, defaultHeaders))
//        followingResp <- http.singleRequest(HttpRequest(GET, url+"/following", defaultHeaders))
//        starredResp <- http.singleRequest(HttpRequest(GET, url+"/starred", defaultHeaders))
//        subsResp <- http.singleRequest(HttpRequest(GET, subscriptions_url, defaultHeaders))
//        orgsResp <- http.singleRequest(HttpRequest(GET, organizations_url, defaultHeaders))
      }yield{
//        println(userResp)
        Unmarshal(userResp.entity).to[User] foreach { user =>
          println(user.id)
          workers ! Repos(id, repos_url, 1)
        }
      }
    case Terminated(actor) =>
      println(s"actor ${actor.path} has stopped")
  }
}
