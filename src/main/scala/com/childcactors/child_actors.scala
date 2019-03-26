package com.childcactors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.childcactors.childActorsDriver.Parent.{CreateChild, TellChild}

object childActorsDriver extends App {

  object Parent {
    case class CreateChild(name: String)
    case class TellChild(msg: String)
  }
  class Parent extends Actor{

    override def receive: Receive = {
      case CreateChild(name) =>
        println(s"${self.path} creating child with name " + name)
        val childActor = context.actorOf(Props[Child], name)
        context become(childReceive(childActor))
    }

    def childReceive(child: ActorRef) : Receive = {
      case TellChild(msg) =>
        println(s"${self.path} sending childActor the message...")
        child ! msg
    }
  }

  class Child extends Actor {
    override def receive: Receive = {
      case msg => println(s"${self.path} received message: " +  msg)
    }

  }

  val system = ActorSystem create("ParentChildActorSystem")

  val parent = system actorOf(Props[Parent], "ParentActor")



  parent ! CreateChild("childActor")
  parent ! TellChild("I am daddy!")

  /*
  Actor Hierarchies:
  Guardian Actor system: This is used to maintain internal system actors
  User Actor System: This is used to maintain the user actors. Eg.g /user/... in akka://ParentChildActorSystem/user/ParentActor
  Root Actor System: This is the parent of both guardian and user actor system.
  */

  /*
  Actor Selection
  */
  val actorRef = system actorSelection "/user/ParentActor/childActor"

  actorRef ! "Hello you have been selected!"




}
