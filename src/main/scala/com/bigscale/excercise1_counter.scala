package com.bigscale

import akka.actor.{Actor, ActorSystem, Props}
import com.bigscale.exercise1Driver.Counter.{Decrement, Increment, Print}

object exercise1Driver extends App{


  //ActorSystem
  val system = ActorSystem.create("excersise1ActorSystem")


  object Counter {
    case object Increment
    case object Decrement
    case object Print
  }
  class Counter extends Actor {

    var counter: Int = _

    override def receive: Receive = {
      case Increment =>
        println("Incrementing...")
        counter +=1
      case Decrement =>
        println("Decrementing...")
        counter -=1
      case Print => println(s"Counter value: $counter")
    }
  }

  val counterActor = system.actorOf(Props(new Counter), "counterActor")

  counterActor ! Increment
  counterActor ! Increment
  counterActor ! Increment
  counterActor ! Increment

  counterActor ! Print

  counterActor ! Decrement
  counterActor ! Decrement
  counterActor ! Decrement


  counterActor ! Print


}
