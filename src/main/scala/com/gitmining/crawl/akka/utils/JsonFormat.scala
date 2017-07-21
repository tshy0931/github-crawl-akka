package com.gitmining.crawl.akka.utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, NullOptions}
/**
  * Created by weiwang on 10/07/17.
  */
trait JsonFormat extends SprayJsonSupport with DefaultJsonProtocol with NullOptions {
  implicit val userBriefFormat = jsonFormat11(UserBrief)
  implicit val userFormat = jsonFormat13(User)
  implicit val repoFormat = jsonFormat14(Repo)

  final case class UserBrief(
                              login:String,
                              id:Long,
                              url:String,
                              followers_url:String,
                              following_url:String,
                              starred_url:String,
                              subscriptions_url:String,
                              organizations_url:String,
                              repos_url:String,
                              `type`:String,
                              site_admin:Boolean
                            )
  final case class User(
                         id:Long,
                         login:String,
                         name:Option[String],
                         `type`:String,
                         company:Option[String],
                         location:Option[String],
                         hireable:Option[Boolean],
                         created_at:String,
                         updated_at:String,
                         followers:Int,
                         following:Int,
                         public_repos:Int,
                         public_gists:Int
                       )
  final case class Repo(
                         id:Long,
                         name:String,
                         full_name:String,
//                         description:String,
                         fork:Boolean,
//                         url:String,
                         created_at:String, //DateTime,
                         updated_at:String, //DateTime,
                         pushed_at:Option[String], //DateTime,
                         homepage:Option[String],
                         size:Long,
                         stargazers_count:Int,
                         language:Option[String],
                         network_count:Option[Int],
                         forks_count:Int,
                         open_issues_count:Int
//                         collaboratorsCount:Int,
//                         contributorsCount:Int,
//                         subscribers_count:Int
                       )
  final case class Owner(
                          login:String,
                          id:Long,
                          url:String
                        )
  final case class Org(
                        id:Long,
                        login:String,
                        url:String
                      )
  final case class Assignee(
                           id:Long,
                           login:String,
                           url:String
                           )
}