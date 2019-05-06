package com.Routers

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, Terminated}

object ManualRoutingDriver extends App {

  case class Result(messageId: Long, result: String)
  case class Message(messageId: Long)

  case class Process(messages: List[Message])
  case class ProcessMessage(message: Message)
  case object StopChild

  class ManualMasterActor extends Actor with ActorLogging{

    override def receive: Receive = parentReceive
    def parentReceive: Receive = {
      case Process(messages) =>
        val childActorList: List[(Long, ActorRef)] = for (message <- messages) yield {
          val childActorRef = context.actorOf(Props[ManualChildActor], "ChildActor" + message.messageId)
          context.watch(childActorRef)
          childActorRef ! ProcessMessage(message)
          (message.messageId -> childActorRef)
        }
        val childActorMap: Map[Long, ActorRef] = childActorList.toMap
        context.become(receiveForChild(childActorMap))
    }

    def receiveForChild(childActorMap: Map[Long, ActorRef]): Receive = {
      case Result(messageId, resultInfo) =>

        val workerActor: ActorRef = childActorMap(messageId)
        log.info(s"Parent Received $resultInfo from child $workerActor...")
        workerActor ! StopChild
        log.info("Children remaining " + childActorMap.size)
        context.become(receiveForChild(childActorMap - messageId))
      case Terminated(actor: ActorRef) => log.info(s"Actor $actor died...")
    }
  }

  class ManualChildActor extends Actor with ActorLogging{
    override def receive: Receive = {
      case ProcessMessage(msg: Message) =>
        log.info(s"Processing message $msg...")
        Thread.sleep(500)
        log.info(s"Processed message $msg...")
        sender() ! Result(msg.messageId, s"messageWithResult_${msg.messageId}")

      case StopChild =>
        log.info("Stopping child...")
        context.stop(self)

    }
  }


  val system = ActorSystem create("SupervisorActorSystem")

  val masterActor = system actorOf(Props[ManualMasterActor], "MasterActor")

  val messages = List(
    Message(2122),
    Message(2132),
    Message(2152),
    Message(2153),
    Message(2154),
    Message(2155),
    Message(2162)
  )

  masterActor ! Process(messages)


}
