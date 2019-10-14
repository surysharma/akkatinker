package com.bigscale.akkastreams

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

object SimpleSourceSinkDriver extends App {

  //Create implicit actor system
  implicit val system = ActorSystem("streams-actor-system")
  //Create materializer
  implicit val  materializer = ActorMaterializer()

  //Create a source
  val source = Source(List(1,2,3,4,5,6,7,8,9,10))

  //Create a sink
  val sink = Sink.foreach[Int](println)




  //Create a flow
  val flowDouble = Flow[Int].map(i => i*2)
  val flowTenTimes = Flow[Int].map(i => i*10)

  //Create a graph
  val graph = source
    .via(flowDouble)
    .via(flowTenTimes)
    .to(sink)

  //Run the graph
  graph.run()

}
