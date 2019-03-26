import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class BasicSpec extends TestKit(ActorSystem("BasicSpec"))
with ImplicitSender
with WordSpecLike
with BeforeAndAfterAll {

  override def afterAll(): Unit =
    TestKit.shutdownActorSystem(system)

  "A Simple Actor" should {
    "echo the messages back" in {
      val echoActor = system.actorOf(Props[BasicActor], "Basic_actor")

      echoActor ! "Hello..."

      //1 - Assert the message returned.
      //expectMsg("Hello...")

      //2 - Extract the message type
     //val expMsg = expectMsgType[String]
     //assert(expMsg == "Hello...")

      //3- Expect the partial function
      expectMsgPF(){
        case "Hello..." =>
        case "Hi..."  =>
      }

    }
  }
}

class BasicActor extends Actor {

  override def receive: Receive = {
    case msg: String => sender() ! msg
  }
}
