package com.actorbehaviourPart1

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.actorbehaviourPart1.MotherChildCommunicationRunner.Mother.{Ask, StartChatWith}

object MotherChildCommunicationRunner extends App {

    abstract class FoodType
    case object VEG extends FoodType
    case object CHOC extends FoodType
    case object KidReject
    case object KidAccept
    case object NO_TO_PLAY
    case object YES_TO_PLAY


  class Kid extends Actor {

    var happinessStatus: String = _
    override def receive: Receive = {
      case VEG =>
        println("2. Kid: kid, received veg...")
        happinessStatus = "SAD"
       sender ! KidReject
      case CHOC => happinessStatus = "HAPPY"
        println("2. Kid: kid, received Chocs...")
        sender ! KidAccept
      case Ask =>
        println("4. Kid: kid, received ask...")
        if (happinessStatus == "SAD") sender ! NO_TO_PLAY else sender ! YES_TO_PLAY
      case _ => println("Unknown message")

    }
  }

  object Mother {
    case class StartChatWith(kid: ActorRef, foodType: FoodType)
    case object Ask
  }
  class Mother extends Actor {
    import Mother._

    override def receive: Receive = {
      case StartChatWith(kid, foodType) =>
        println("1. Mother: Starting the chatting kid...")
        kid ! foodType
      case e @ (KidReject | KidAccept) =>
        println(s"3. Mother: $e veggies...")
        sender ! Ask
      case NO_TO_PLAY =>
        println("5. Mother: My kid got Veggi's and don't want to play")
      case YES_TO_PLAY =>  println("5. Mother: My kid is Happy and want to play")
    }
  }

  val actorSystem = ActorSystem("MotherChildChatSystem")
  val mother = actorSystem.actorOf(Props[Mother], "MotherActor")
  val kid = actorSystem.actorOf(Props[Kid], "KidActor")

  mother ! StartChatWith(kid, CHOC)
  //mother ! StartChatWith(kid, VEG)


}
