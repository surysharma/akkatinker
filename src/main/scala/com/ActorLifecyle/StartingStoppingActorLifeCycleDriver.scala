package com.ActorLifecyle

import akka.actor.{Actor, ActorLogging, ActorSystem, PoisonPill, Props}

object StartingStoppingActorDriver extends App{

  val system = ActorSystem("StartingStoppingActorSystem")
  val parentActor = system.actorOf(Props[ParentActor], "ParentActor")

  case class CreateActor(name: String)
  case object CheckMessage
  case object StopActor
  class ParentActor extends Actor with ActorLogging{

    //1- The parent first gets called before starting any child.
    override def preStart(): Unit = log.info("Called preStart lifeCycle method...")
    //2- The parent is the last to be stopped after all its children get stopped.
    override def postStop(): Unit = log.info("Called postStop lifeCycle method...")

    override def receive: Receive = {
      case CreateActor(name) =>
        context.actorOf(Props[ChildActor], name)

    }
  }

  case class Message(msg: String)
  class ChildActor extends Actor with ActorLogging {

    //3- Child will be started after the parent has started.
    override def preStart(): Unit = log.info("Called preStart lifeCycle method...")
    //4- Child will be first stopped before the parent is stopped.
    override def postStop(): Unit = log.info("Called postStop lifeCycle method...")


    override def receive: Receive = {
      case Message(msg) => log.info(s"$msg received by1 $self...")
    }
  }

  parentActor ! CreateActor("ChildActor")

  val childActorRef = system.actorSelection("/user/ParentActor/ChildActor")

  childActorRef ! Message("ChildActorSender using actor selection")

  parentActor ! PoisonPill

}
