package com.advancescala

import scala.util.Try

object part1Driver extends App {

  def singleArgMethod(arg: Int) = s"$arg is the argument..."

  /**
  * Dark syntactic sugar 1 - A method can be called with a curly braces and initialized with any custom code
  */
    //Example a.
  val caller = singleArgMethod{
    //Pre-initialized first
    println("This will be called before the method")
    //Argument passed here
    22
  }
  println(caller)

  //Example b.
  val exp = Try {
    throw new RuntimeException
  }

  //Example c.
  val numWithAs = List(1,2,3).map { x =>
    x + "a"
  }
  println("numWithAs: " + numWithAs)

  /**
    * Dark syntactic sugar 2 - A trait or Abstract class with an abstract method
    */
  abstract class Incrementer {
    def increment(a: Int): Int
    def show(): String = "Showing off"
  }

  val incementer  = (a: Int) => a + 2

  List(1,2,3).map(incementer)

  /**
    * Dark syntactic sugar 3 - :: and #:: methods
    */
  //Example a. As per scala spec if a method ends with : it is right associative else it is left associative
  val adder = 1:: 2 :: 3 ::  List(4,5,6)
  //List(4,5,6).:: 3 ---> List(3,4,5,6)
  //2 :: List(3,4,5,6) -----> List(2,3,4,5,6)
  //1 :: List(2,3,4,5,6) ------> List(1,2,3,4,5,6)

  /**
    * Dark syntactic sugar 4 - Infix in generic types
    */
  //Example a.
  class Composite[A,B]
  val composite: Int Composite String = new Composite[Int, String]

  //Example b.
  class --->[A,B]
  val pointer: Int ---> String = new  --->[Int, String]

  /**
    * Dark syntactic sugar 5 - update is special in Array
    */
    //Example a.
  val upd = Array(1,2,3)
  upd(2) = 20 //Here = is treated as upd.update(2,20)
  upd.foreach(print(_))

  /**
    * Dark syntactic sugar 6 - Getters and setters
    */
  class Mutable {
    private var item: Int = 0
    def manner: Int = item
    def manner_=(i: Int) =
      item = i
  }

  val m1 = new Mutable
  m1.manner = 2 //What looks like an assignment is actually a call to manner_= method.

  println("\nManners :" + m1.manner)


  /**
    * Dark syntactic sugar 7 - method naming
    */
  class ExpItems {
    def `an Item in`(item: String) = {}
  }

  val exp1 = new ExpItems
  exp1 `an Item in` "Group"

}
