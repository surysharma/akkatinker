package com.actorbehaviourPart1

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object MotherChildCommunicationWithBecomeRunner extends App {

  object Kid {
    case object KidReject
    case object KidAccept
    case object NO_TO_PLAY
    case object YES_TO_PLAY
  }
  class Kid extends Actor {
    import Kid._
    import Mother._
    override def receive: Receive = happyReceive

    def happyReceive: Receive = {
      case Food(VEG) =>
        println("2. Kid: kid, happyReceive veg...")
        context become sadReceive
       sender ! KidReject
      case Food(CHOC) =>
        println("2. Kid: kid, happyReceive Chocs...")
        sender ! KidAccept
      case Ask =>
        println("4. Kid: kid, happyReceive ask...")
        sender ! YES_TO_PLAY
      case _ => println("Unknown message")

    }

    def sadReceive: Receive = {
      case Food(CHOC) =>
        println("2. Kid: kid, sadReceive Chocs...")
        context become happyReceive
        sender ! KidAccept
      case Food(VEG) =>
        println("2. Kid: kid, sadReceive veg...")

        sender ! KidReject
      case Ask =>
        println("4. Kid: kid, sadReceive ask...")
        sender ! NO_TO_PLAY
      case _ =>
    }

  }

  object Mother {
    case class Food(foodType: String)
    case class StartChatWith(kid: ActorRef, food: Food)
    case object Ask
    val VEG = "veggies"
    val CHOC = "chocs"

  }
  class Mother extends Actor {
    import Kid._
    import Mother._

    override def receive: Receive = {
      case StartChatWith(kid, foodType) =>
        println(s"1. Mother: Starting the chatting kid with ${foodType}...")
        kid ! foodType
      case e @ (KidReject | KidAccept) =>
        println(s"3. Mother: ${e}'ed food...")
        sender ! Ask
      case NO_TO_PLAY =>
        println("5. Mother: My kid got Veggi's and don't want to play")
      case YES_TO_PLAY =>  println("5. Mother: My kid is Happy and want to play")
    }
  }

  val actorSystem = ActorSystem("MotherChildChatSystem")
  val mother = actorSystem.actorOf(Props[Mother], "MotherActor")
  val kid = actorSystem.actorOf(Props[Kid], "KidActor")

  import Mother._
  mother ! StartChatWith(kid, Food(VEG))
  Thread.sleep(5000)
  println("----------")
  mother ! StartChatWith(kid, Food(CHOC))


}
