package com.bigscale.akkahttp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer

import scala.concurrent.Future

object AsyncServer extends App {

  implicit val system = ActorSystem("AsyncHttpActorSystem")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  def getData = {
    Thread.sleep(20000)
    println("Long running task completes")
    """
       Hello from Akka Http
    """.stripMargin

  }
  val requestHandler: HttpRequest => Future[HttpResponse] = {
    case HttpRequest(HttpMethods.GET, _, _, _, _) =>
      Future(
        HttpResponse(
          StatusCodes.OK,
          entity = HttpEntity(ContentTypes.`text/html(UTF-8)`,getData)
        )
      )
  }

  val bindingFutureAsync = Http().bindAndHandleAsync(requestHandler, "localhost", 8081)
  println("Server running in Async mode on 8081 port...")


}
