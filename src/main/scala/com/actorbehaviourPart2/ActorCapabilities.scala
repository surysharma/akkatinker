package com.actorbehaviourPart2

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.actorbehaviourPart2.ActorCapabilities.SimpleActor.{ForwardParcel, Parcel, SpecialMessage}

object ActorCapabilities extends App {

  object SimpleActor {
    case class SpecialMessage(msg: String)
    case class Parcel(items: String, ref: ActorRef)
    case class ForwardParcel(item: String, ref: ActorRef)
  }
 class SimpleActor extends Actor {

    override def receive: Receive = {

      case ("Forwarded msg from Nick", seq: Int) =>
        println(s"${self} Nick responding back to ${sender()}, seq:${seq}")
        sender() ! "sending Back...."
      case "sending Back...." => println(s"${self}, back to Alice?")
      case "Hi" =>
        println(s"${self} sending response to sender:")
        sender() ! "Going to dead letter queue"

      case (message: String, seq: Int) =>
        println("Forwarding from nick... seq:" + (seq+1))
        nick forward ("Forwarded msg from Nick", (seq +1))
      case message: String => println(s"${message} message received by ${self}, sent by ${sender()}")

      // context.self gives the full actor instance along with path
      //context.sel.path gives only the path
      case SpecialMessage(content) =>
        println(s" Received ${SpecialMessage} by ${context.self} and path is ${context.self.path}")
        self ! content

      case Parcel(items, ref) => ref ! items

      case ForwardParcel(item, ref) =>
        println(" Forwarding the message...seq:" + 1)
        ref forward (item + " topup", 1)

      case _ => println("Unknown message!")

    }
  }

  /**  1. Each actor is passed an implicit self ref as part of the message
       2. Each actor has an ref to its context
    */
  val actorSystem = ActorSystem("actor_system")
  val simpleActor = actorSystem.actorOf(Props[SimpleActor], "simple_actor")
//  simpleActor ! SpecialMessage("Hello")

  /**  3. Actor can reply to its sender or any other actor ref */
  val alice = actorSystem.actorOf(Props[SimpleActor], "alice")
  val john = actorSystem.actorOf(Props[SimpleActor], "John")

//  alice ! Parcel("books", john)

  /**  4. Dead letters
    When a message is not sent to any receiving actor, then it goes to the dead letter queue.
    In the case below, when the "Hi" message is sent to the actor which sends the response back
    with sender(), the main actor system is not handling it and it goes to dead-letter queue.
    * */
  alice ! "Hi"

  /** 5. Message forwarding
      If Alice -> John -> Nick. Then forwarding will keep the original reference of Alice.
    */
  val nick = actorSystem.actorOf(Props[SimpleActor], "Nick")

  alice ! ForwardParcel("OriginalBook", john)

}
