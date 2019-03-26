package com.ActorLifecyle

import akka.actor.{Actor, ActorLogging, ActorSystem, PoisonPill, Props, Terminated}

object DeathWatchDriver extends App{

  val system = ActorSystem("LifeCyleActorSystem")
  val parentActor = system.actorOf(Props[ParentActor], "ParentActor")

  case class CreateActor(name: String)
  case object CheckMessage
  case object StopActor
  class ParentActor extends Actor with ActorLogging{

    override def receive: Receive = {
      case CreateActor(name) =>
        val childActor = context.actorOf(Props[ChildActor], name)
        //1- Parent can put a death watch on any child.
        context.watch(childActor)
        //2- Once the child is terminated, the Parent gets the Terminated message with the dead child Ref.
      case Terminated(ref) => log.info(s"The child $ref has died!")

    }
  }

  case class Message(msg: String)

  class ChildActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case Message(msg) => log.info(s"$msg received by1 $self...")
      case StopActor => context.stop(self)
    }
  }

  parentActor ! CreateActor("ChildActor")

  val childActorRef = system.actorSelection("/user/ParentActor/ChildActor")

  childActorRef ! Message("ChildActorSender using actor selection")

 childActorRef ! PoisonPill

}
