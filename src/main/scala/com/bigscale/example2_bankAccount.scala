package com.bigscale

import java.time.LocalDate

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.bigscale.BankAccountDriver.User.Transaction

object BankAccountDriver extends App{

  val actorSystem = ActorSystem.create("example2ActorSystem")

  object BankAccount {

    case class Deposit(amount: Double)
    case class WithDraw(amount: Double)
    case object Statement

    case class Success(reason: String)
    case class Failure(reason: String)
  }
  class BankAccount extends Actor {
    var amount: Double = _
    import BankAccount._

    override def receive: Receive = {
      case Deposit(amount) =>
        if (amount < 0) sender ! Failure("Invalid amount...")

        this.amount += amount
        sender ! Success(s"Deposited amount ${amount}, balance ${this.amount}...")

      case WithDraw(amount) =>
        if (this.amount - amount < 0) {
          sender ! Failure(s"Unable to withdraw ${amount}, Insufficient funds ${this.amount}...")
        }
        else {
          this.amount -= amount
          sender ! Success(s"Successfully withdrawl  amount ${amount}, balance ${this.amount}...")
        }

      case Statement => sender ! Success("Statement: Amount as on " + LocalDate.now() + " is " + amount + " ...")
    }
  }

  object User {
    case class Transaction(bankAccount: ActorRef)
  }
  class User extends Actor {
    import BankAccount._
    override def receive: Receive ={
      case Transaction(bankAccount) =>
        bankAccount ! Deposit(20)
        bankAccount ! Deposit(40)
        bankAccount ! Deposit(100)
        bankAccount ! WithDraw(100)
        bankAccount ! Statement
        bankAccount ! WithDraw(40)
        bankAccount ! WithDraw(30)
        bankAccount ! Statement
        bankAccount ! Deposit(-20)
      case Failure(reason) => println(reason)
      case Success(reason) => println(reason)
    }
  }

  val bankAccount = actorSystem.actorOf(Props(new BankAccount), "BankAccountActor")
  val user = actorSystem.actorOf(Props(new User), "UserActor")

  user ! Transaction(bankAccount)

}
