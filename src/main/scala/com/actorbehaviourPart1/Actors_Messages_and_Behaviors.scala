package com.actorbehaviourPart1

import akka.actor.{Actor, ActorSystem, Props}
import com.actorbehaviourPart1.ActorIntroDriver.PersonActor.Greet

object ActorIntroDriver extends App {


  /** Part-1 Actor system intro.
      -----------------------------
    * Actor system should only be one per application instance.
    * Actor system name should be only alphanumeric, and should not have spaces.
   */
  val actorSystem = ActorSystem("FirstActorSystem")
  println("Started: " + actorSystem)

   /** Part-2 Create actors in Actor System.
      ------------------------------------------
     * Always have a companion object corresponding to the Actor
   */
  object WordCountActor {
     case object Total
   }
   import com.actorbehaviourPart1.ActorIntroDriver.WordCountActor.Total
  class WordCountActor extends Actor{
    //Internal mutable State
    var totalWordCount = 0

     def receive = {
       case message: String => println(message)
         totalWordCount += message.split(" ").length
       case Total => println("total count is " + totalWordCount)
     }
  }

  /** Part-3 Instantiate actor.
      ------------------------------------------
    * We cannot have two actors with the same name in an actor system.
    * We cannot instantiate an actor with new keyword.
    */
  val actor = actorSystem.actorOf(Props[WordCountActor], "WordCount")
    actor ! "This is great!"
  //Actors are asynchronous
    Thread.sleep(500)
    actor ! Total
    actor ! "Mutable state is maintained"
    Thread.sleep(500)
    actor ! Total


  /** Part-4 Instantiate actor with args
      ------------------------------------------
    * When the actor requires args, best practice is to create factory method in companion object.
      and put the Props in the factory method.
    */
    object PersonActor {
      case object Greet
      def props(name: String) = Props(new PersonActor(name))
    }
    class PersonActor(val name: String) extends Actor {
      var age: Int = _

      override def receive: Receive = {
        case Greet => println(s"Say hello to $name aged $age")
        case default: Int => println(s"Setting age to $default")
          age = default
        case _ => println("Unknown msg")
      }
    }

    val actorSystem1 = ActorSystem("ActorSystem1")
    println("Started: " + actorSystem1)
    val person = actorSystem1.actorOf(PersonActor.props("John"), "Person_actor")
    person ! 20
    Thread.sleep(200)
    person ! Greet
}
