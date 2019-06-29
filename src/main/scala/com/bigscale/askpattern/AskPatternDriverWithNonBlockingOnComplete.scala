package com.bigscale.askpattern

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object AskPatternDriverWithNonBlockingOnComplete extends App {

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
  val futureResult: Future[Any] = dataAccessActor ? GetContent

  /**
    * Non-blocking onComplete
    */
  println("Before calling onComplete...")
  futureResult onComplete {
    case Success(result: String) =>  println("Result from Async complete call: " + result)
    case Failure(exp: Throwable) => println("Exception occurred...exp=" + exp.printStackTrace())
  }

  println("After calling onComplete...")

}
