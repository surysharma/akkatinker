package com.persistance.snapshotting

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.{PersistentActor, SaveSnapshotFailure, SaveSnapshotSuccess, SnapshotOffer}

import scala.collection.mutable

object PersistentActorWithSnapshottingDriver extends App {

  case class Trade(id: Int, amount: Double)

  //Command
  case class PersistTrade(trade: Trade)
  case object PrintTrade

  //Event
  case class TradePersisted(trade: Trade)

  class PersistentWithSnapShottingActor extends PersistentActor with ActorLogging {

    var totalAmount = 0.0
    var trade: Trade=null
    val MAX_MESSAGES = 10

    val queue = new mutable.Queue[Trade]()

    override def persistenceId: String = "PersistentWithSnapShottingActor_id"

    override def receiveCommand: Receive = {
      case PersistTrade(trade) =>
        persist(TradePersisted(trade)){ event =>

          log.info(s"Event persisted...$event")

          totalAmount += event.trade.amount
          this.trade = trade
          if (queue.size >= MAX_MESSAGES) {
            log.info(s"Saving snapshot... $queue")
            saveSnapshot(queue)
            queue.dequeueAll(_ => true)
          }
          queue.enqueue(trade)

        }
      case SaveSnapshotSuccess(metadata) => log.info(s"Snapshot saved successfully $metadata ...")
      case SaveSnapshotFailure(metadata, cause) => log.info(s"Snapshot saved failed for $metadata with exception $cause ...")
      case PrintTrade => log.info("Current state of trade= " + trade+ ", amount=" + totalAmount)
    }
    override def receiveRecover: Receive = {
      case TradePersisted(trade) =>
        log.info(s"Event recovered: sequenceId: $trade")
        totalAmount += trade.amount
        this.trade = trade
      case SnapshotOffer(metadata, offer) =>
        offer.asInstanceOf[mutable.Queue[Trade]].foreach(trade => queue.enqueue(trade))
        log.info(s"Recovered snapshot $metadata ...")

    }
  }

  val actorSystem = ActorSystem("PersistentWithSnapShottingSystem")
  val actor = actorSystem.actorOf(Props[PersistentWithSnapShottingActor], "PersistentWithSnapShottingActor")


  //Note: This should be uncommented each time, the current object state wants to be restored.
    for(i <- 1 to 20000) {
      actor ! PersistTrade(Trade(i, 10.0))
    }

  actor ! PrintTrade

}
