package com.bigscale.extraPattern

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.util.Timeout

import scala.concurrent.duration._

object ExtraPatternDriver extends App {

  /**
    * Some DAO with slow response time.
    */
  object DataAccess {
    def getData() = {
      Thread.sleep(2000)
      println("Returning content from DAO...")
      "Some content from DAO"
    }

    def compute() = {
      Thread.sleep(2000)
      println("Perform intensive computation...")
    }
  }

  object GetContent
  object ComputeOp
  class DataAccessActor extends Actor {
    override def receive: Receive = {
      case (GetContent, extraActor: ActorRef) =>
       extraActor ! GetContent
      case _ => println("UNKNOWN message....")
    }
  }
  class ServiceAccessActor extends Actor {
    override def receive: Receive = {
      case (ComputeOp, extraActor: ActorRef) =>
        extraActor ! ComputeOp
      case _ => println("UNKNOWN message....")
    }
  }

  class ExtraActor extends Actor {

    override def receive: Receive ={
      case ComputeOp => DataAccess.compute()
      case GetContent => DataAccess.getData()
    }

  }

  val system = ActorSystem("AskActorSystem")
  val dataAccessActor = system.actorOf(Props(new DataAccessActor()), "DataAccessActor")
  val serviceActor = system.actorOf(Props(new ServiceAccessActor()), "ServiceAccessActor")
  val extraActor = system.actorOf(Props(new ExtraActor()), "extraActor")


  implicit val timeout = Timeout(5 seconds)
  implicit val context = system.dispatcher
  dataAccessActor.tell(GetContent, extraActor)
  serviceActor.tell(ComputeOp, extraActor)

  /**
    * Non-blocking onComplete
    */
  println("Before calling Schedule...")
  system.scheduler.scheduleOnce(50 milliseconds, extraActor, "foo")


  println("After calling onComplete...")

}
