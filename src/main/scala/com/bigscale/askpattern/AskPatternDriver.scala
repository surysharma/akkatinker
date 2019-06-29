package com.bigscale.askpattern

import akka.actor.{Actor, ActorSystem, Props}

import scala.concurrent.{Await, Future}

object AskPatternDriver extends App {

  /**
    * Some DAO with slow response time.
    */
  object DataAccess {
    def getData() = {
      Thread.sleep(2000)
      println("Returning content...")
      "Some content from DAO"
    }
  }

  object GetContent
  class DataAccessActor extends Actor {
    override def receive: Receive = {
      case GetContent =>
        println("Inside GetContent actor...")
        sender() ! DataAccess.getData()
      case _ => println("UNKNOWN message....")
    }
  }

  val system = ActorSystem("AskActorSystem")

  val dataAccessActor = system.actorOf(Props(new DataAccessActor()), "DataAccessActor")

  import akka.pattern.ask
  import akka.util.Timeout
  import scala.concurrent.duration._
  implicit val timeout = Timeout(5 seconds)
  implicit val context = system.dispatcher
  val respFuture: Future[Any] = (dataAccessActor ? GetContent)

  println("response returned " + respFuture)

  val result = Await.result(respFuture, timeout.duration).asInstanceOf[String]

  println("Result from blocking call: " + result)

  system.stop(dataAccessActor)

  System.exit(1)

}
