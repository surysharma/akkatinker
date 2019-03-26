package com.persistance

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.PersistentActor

import scala.collection.mutable.ArrayBuffer

object TradeAggregatorPersistenceDriver extends App{

  case class Trade(id: String, amount: Double)
  case class AggregatedTrade(aggTradeId: String, trades: List[Trade])

  //Command
  case class SubmitTradeCommand(trade: Trade)
  //Event
  case class TradeSubmittedEvent(trade: Trade)

  //Command
  case object GetTotalCommand


  class TradeAggActor extends PersistentActor with ActorLogging {

    var trades: ArrayBuffer[Trade] = new ArrayBuffer[Trade]()

    override def receiveCommand: Receive = {
      case SubmitTradeCommand(trade) =>
        persist(TradeSubmittedEvent(trade)) { e =>
          trades += trade
          log.info(s"Trade ${e.trade.id} persisted...")
        }

      case GetTotalCommand =>
        val totalAmount = trades.foldLeft(0.0){(sum, trade) => sum + trade.amount}
        log.info(s"Total amount for aggregated trade is $totalAmount ...")
    }

    override def receiveRecover: Receive = {
      case TradeSubmittedEvent(trade) =>
        trades += trade
        log.info(s"Recovered $trade ...")
    }

    override def persistenceId: String = "TradeAggActor_persistence_id"
  }


  val system = ActorSystem("TradeAggregatorSystem")

  val tradeAggregatorPersistenceActor = system.actorOf(Props[TradeAggActor], "TradeAggActor")


  //for (i <- 1 to 10) tradeAggregatorPersistenceActor ! SubmitTradeCommand(Trade("axx"+i, 10.0))

  tradeAggregatorPersistenceActor ! GetTotalCommand



}
