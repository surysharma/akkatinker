package com.bigscale.akkahttp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

object ReqResponseServer extends App {

  implicit val system = ActorSystem("AsyncHttpActorSystem")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  def getData = {
    """
      |Hello from Akka Http
      |
    """.stripMargin
  }

  val route = path("home") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,getData))
      }
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  println("Server running in sync mode on 8080 port...")





}
