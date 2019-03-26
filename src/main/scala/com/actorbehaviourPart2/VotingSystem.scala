package com.actorbehaviourPart2

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}

object VotingSystemDriver extends App{


  /**
    1- A Citizen will vote for a Candidate
    */
  case class Vote(candidate: String)
  case object AskIfVotedYet
  case class CitizenResponseToVote(votedForCandidate: Option[String])
  class CitizenActor extends Actor with ActorLogging {
    var votedForCandidate: Option[String] = None

    override def receive: Receive = {
      case Vote(candidate) => votedForCandidate = Some(candidate)
        log.info(s"Voted ${self.path} voted for $candidate ...")
      case AskIfVotedYet => sender() ! CitizenResponseToVote(votedForCandidate)
    }
  }


  /**
    2- A Vote Aggregator will take the list of all the citizens and then will do two things:
  Ask the citizen if they have voted or not
    If they have not voted, then it will poll them again to request if they got a chance to vote yet.
    If they have voted, then it will ask which candidate they have voted for.
    Once it knows which candidates are voted, it will create a VotingResult count and prints it.
    */
  case class AggregateVotes(citizens: Set[ActorRef])
  class VoteAggregator extends Actor with ActorLogging{
    var votingResult: Map[String, Int] = Map()
    var stillWaiting: Set[ActorRef] = Set()
    override def receive: Receive = {
      case AggregateVotes(citizens) =>
        stillWaiting = citizens
        citizens.foreach(citizen => citizen ! AskIfVotedYet)
      case CitizenResponseToVote(None) => sender() ! AskIfVotedYet

      case CitizenResponseToVote(Some(candidate)) =>

        val newStillWaiting = stillWaiting - sender()
        //log.info(s"CitizenResponseToVote: Voted for $candidate ...")
        val resultCount = votingResult.getOrElse(candidate, 0)
        votingResult += (candidate -> (resultCount + 1))

        if (newStillWaiting.isEmpty) {
          log.info("Voting results:" + votingResult)
        }else{
          stillWaiting = newStillWaiting
          log.info(s"CitizenResponseToVote: Citizens yet to vote $stillWaiting")
        }
    }
  }

  val system = ActorSystem("VotingSystem")

  val votingAggregator = system.actorOf(Props[VoteAggregator], "VoteAggregator")
  val citizen1 = system.actorOf(Props[CitizenActor], "citizen1")
  val citizen2 = system.actorOf(Props[CitizenActor], "citizen2")
  val citizen3 = system.actorOf(Props[CitizenActor], "Citizen3")
  val citizen4 = system.actorOf(Props[CitizenActor], "Citizen4")
  val citizen5 = system.actorOf(Props[CitizenActor], "Citizen5")

  citizen1 ! Vote("Steve")
  citizen2 ! Vote("Steve")
  citizen3 ! Vote("Jack")
  citizen4 ! Vote("Moe")
  citizen5 ! Vote("Steve")

  votingAggregator ! AggregateVotes(Set(citizen1, citizen2, citizen3, citizen4, citizen5))

}
