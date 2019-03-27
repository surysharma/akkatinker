package com.persistance.snapshotting

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.{PersistentActor, SnapshotOffer}

import scala.collection.mutable

object SimplePersistenceActorExampleWithSnapshot extends App{

  //Command
  case class SimpleCommand(content: String)
  case object PrintContentCommand

  //Event
  case class SimpleEvent(content: String)

  class SimplePersistenceActor extends PersistentActor with ActorLogging {

    val MAX_MESSAGES = 10
    val queue = new mutable.Queue[String]()


    override def persistenceId: String = "simple_prsistanc_aActor_id"

    override def receiveCommand: Receive = {
      case SimpleCommand(text) =>
        log.info(s"Received command...$text")
        persist(SimpleEvent(text)){
            event => log.info(s"Event persisted...$event")
            if (queue.size >= MAX_MESSAGES) {
              saveSnapshot(queue)
              queue.dequeueAll(_=>true)
              log.info("Snapshot saved.... ")
            }
            queue.enqueue(text)
        }
      case PrintContentCommand => log.info("Current state of content= " + queue)
    }
    override def receiveRecover: Receive = {
      case SimpleEvent(values) =>
        log.info(s"Event recovered: $values")
        queue.enqueue(values)
      case SnapshotOffer(metadata, event) =>
        log.info(s"Recovered snapshot: $metadata")
        event.asInstanceOf[mutable.Queue[String]].foreach(queue.enqueue(_))
    }
  }

  val actorSystem = ActorSystem("SimplePersistanceSystemWithSnaphot")
  val actor1 = actorSystem.actorOf(Props[SimplePersistenceActor], "SimplePersistanceActor")


//  Note: This should be commented each time, the current object state wants to be restored.
  for(i <- 1 to 100000) {
    actor1 ! SimpleCommand("test message " + i)
  }

  actor1 ! PrintContentCommand


}
