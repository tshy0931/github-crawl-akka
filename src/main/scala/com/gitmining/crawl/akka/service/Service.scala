package com.gitmining.crawl.akka.service

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import com.gitmining.crawl.akka.scheduler.Scheduler
import com.gitmining.crawl.akka.scheduler.Scheduler.Crawl

/**
  * Created by weiwang on 09/07/17.
  */
object Service {

  def main(args: Array[String]): Unit = {

    implicit val actorSystem = ActorSystem("gitmining-crawl-akka")
    implicit val materializer = ActorMaterializer()
    implicit val ec = actorSystem.dispatcher

    val actor = actorSystem.actorOf(Props[Scheduler], "github-crawl-akka-scheduler")
    actor ! Crawl("/users", 1, 100000)
  }
}
