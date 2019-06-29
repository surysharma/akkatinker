package com.bigscale.askpattern

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object AskPatternDriverWithBlockingAwait extends App {

  /**
    * Some DAO with slow response time.
    */
  object DataAccess {
    def getData() = {
      Thread.sleep(8000) //8 seconds
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

  /**
    * The implicit timeout for the ask should be greater than all other latency timeouts.
    * If not, you will get the Timeout exception.
    */
  implicit val timeout = Timeout(10 seconds)
  implicit val context = system.dispatcher

  val respFuture: Future[Any] = (dataAccessActor ? GetContent)


  /**
    * Blocking await call
    */
  println("Before calling the blocking call: " + respFuture)
  val result = Await.result(respFuture, timeout.duration).asInstanceOf[String] //Blocking call!!!
  println("Result after the blocking call: " + result)

}
