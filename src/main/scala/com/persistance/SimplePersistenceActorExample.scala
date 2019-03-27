package com.persistance

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.PersistentActor

object SimplePersistenceActorExample extends App{

  //Command
  case class SimpleCommand(content: String)
  case object PrintContentCommand

  //Event
  case class SimpleEvent(content: String)

  class SimplePersistanceActor extends PersistentActor with ActorLogging {

    var content: StringBuilder = new StringBuilder

    override def persistenceId: String = "simple_prsistanc_aActor_id"

    override def receiveCommand: Receive = {
      case SimpleCommand(text) =>
        content ++= text
        log.info(s"Received command...$text")
        persist(SimpleEvent(text)){
          event => log.info(s"Event persisted...$event")
        }
      case PrintContentCommand => log.info("Current state of content= " + content.toString())
    }
    override def receiveRecover: Receive = {
      case SimpleEvent(values) =>
        log.info(s"Event recovered: $values")
        content ++= values
    }
  }

  val actorSystem = ActorSystem("SimplePersistanceSystem")
  val actor1 = actorSystem.actorOf(Props[SimplePersistanceActor], "SimplePersistanceActor")


  //Note: This should be uncommented each time, the current object state wants to be restored.
//  for(i <- 1 to 5) {
//    actor1 ! SimpleCommand("test message " + i)
//  }

  actor1 ! PrintContentCommand


}
