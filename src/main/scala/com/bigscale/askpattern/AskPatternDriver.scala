package com.bigscale.askpattern

import akka.actor.{Actor, ActorSystem, Props}

import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object AskPatternDriver extends App {

  /**
    * Some DAO with slow response time.
    */
  object DataAccess {
    def getData() = {
      Thread.sleep(2000)
      println("Returning content from DAO...")
      "Some content from DAO"
    }
  }

  object GetContent
  class DataAccessActor extends Actor {
    override def receive: Receive = {
      case GetContent =>
        sender() ! DataAccess.getData()
      case _ => println("UNKNOWN message....")
    }
  }

  val system = ActorSystem("AskActorSystem")

  val dataAccessActor = system.actorOf(Props(new DataAccessActor()), "DataAccessActor")

  implicit val timeout = Timeout(5 seconds)
  implicit val context = system.dispatcher
  val respFuture: Future[Any] = (dataAccessActor ? GetContent)
  val result = Await.result(respFuture, timeout.duration).asInstanceOf[String] //Blocking call!!!
  println("Result from blocking call: " + result)

  system.stop(dataAccessActor)

  System.exit(1)

}
