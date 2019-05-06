package com.Routers

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, Terminated}
import akka.routing.RoundRobinGroup

object RoundRobinRouterDriver extends App {

  case class Result(messageId: Long, result: String, actorRef: ActorRef)
  case class Message(messageId: Long)

  case class Process(messages: List[Message])
  case class ProcessMessage(message: Message)
  case object StopChild

  class RoundRobinMasterActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case Process(messages:  List[Message]) =>

        val childCount = if (messages.length < 5 ) messages.length % 5 else 5
        val actorPaths = for (i <- 1 to childCount)
          yield {
            val childActor = context.actorOf(Props[RoundRobinChildActor], "RoundRobinChildActor"+i)
            context.watch(childActor)
            childActor.path.toString
          }
        val routerActor = context.actorOf(RoundRobinGroup(actorPaths).props(), "RouterActor")

        messages.foreach(routerActor ! ProcessMessage(_))

      case Result(messageId: Long, result: String, actorRef: ActorRef) =>
        log.info(s"Parent Received $messageId from child $actorRef...")
        context.stop(actorRef)

      case Terminated(actor: ActorRef) => log.info(s"Actor $actor died...")

    }
  }
  class RoundRobinChildActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case ProcessMessage(msg: Message) =>
        log.info(s"Processing message $msg...")
        sender() ! Result(msg.messageId, s"messageWithResult_${msg.messageId}", self)
    }
  }

  val system = ActorSystem create("RoundRobinRouterSystem")

  val masterActor = system actorOf(Props[RoundRobinMasterActor], "RoundRobinMasterActor")

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
