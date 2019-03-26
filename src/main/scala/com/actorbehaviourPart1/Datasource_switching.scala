package com.actorbehaviourPart1

import akka.actor.{Actor, ActorSystem, Props}

object DataSourceSwitchingDriver extends App {

  /*

   */

  case object SwitchToPhoenix
  case object SwitchToOracle

  class DataSourceActor extends Actor {

    override def receive: Receive = phoenixReceive

    def phoenixReceive: Receive = {
      case SwitchToOracle =>
        println("*********** Switching to Oracle *********8")
        context.become(oracleReceive)
      case msg: String => println("PhoenixReceive:"+ msg)

    }

    def oracleReceive: Receive = {
      case msg: String => println("oracleReceive:"+ msg)
      case SwitchToPhoenix =>
        println("-------Switching to phoenix-------")
        context.become(phoenixReceive)
    }
  }

  val system = ActorSystem("DataSource_switch_system")
  val persister = system.actorOf(Props[DataSourceActor], "Persistance_actor")

  persister ! "default req1"

  Thread.sleep(300)

  persister ! SwitchToOracle

  Thread.sleep(500)

  persister ! "phoenix request?...."

  persister ! SwitchToPhoenix

  Thread.sleep(300)

  persister ! "Oracle request...."




}
