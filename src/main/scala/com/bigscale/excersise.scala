package com.bigscale

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object example1Driver extends App{

  //Create actor system
  val actorSystem = ActorSystem.create("example1ActorSystem")
  //println("actorSystem: " + actorSystem)


  //Create an Actor
  class HelloActor extends Actor {
    override def receive: Receive = {
      case "Hola" =>
        println(s"Actor: ${self} ")
        sender ! "Hi there...."
      case msg:String => println(s"${msg} from " + self)
      case Greeting(content) => self ! content
      case SayHiTo(ref) =>
        println(s"Actor: ${self} ")
        ref ! "Hola"
    }
  }


  //Calling the actor
  val aliceActorRef = actorSystem.actorOf(Props(classOf[HelloActor]),"Alice_the_Actor")
  val bobActorRef = actorSystem.actorOf(Props(classOf[HelloActor]),"Bob_the_Actor")
  //println("julieActorRef: " + julieActorRef)

 // julieActorRef ! Greeting("Hi")


  case class Greeting(content: String)
  case class SayHiTo(actor: ActorRef)


  aliceActorRef ! SayHiTo(bobActorRef)

}
