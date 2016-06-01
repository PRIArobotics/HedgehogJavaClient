package at.pria.hedgehog.client

import at.pria.hedgehog.client.protocol.proto.HedgehogP.HedgehogMessage
import at.pria.hedgehog.client.protocol.proto.AckP.{Acknowledgement, OK}
import org.scalatest.{FlatSpec, Matchers}
import org.zeromq.ZMQ

/**
  * Created by clemens on 31.05.16.
  */
class ClientSpec extends FlatSpec with Matchers {
  "Protobuf messages" should "work" in {
    val msg = new HedgehogMessage()
    msg.setAcknowledgement({
      val msg = new Acknowledgement()
      msg.code = OK
      msg
    })

    msg.getAcknowledgement.code shouldBe OK
  }

  "JeroMQ" should "work" in {
    val ctx = ZMQ.context(1)

    val socket1 = ctx.socket(ZMQ.PAIR)
    socket1.bind("inproc://endpoint")

    val socket2 = ctx.socket(ZMQ.PAIR)
    socket2.connect("inproc://endpoint")

    socket1.send("ab")

    val poller = new ZMQ.Poller(1)
    val socket2Index = poller.register(socket2, ZMQ.Poller.POLLIN)

    while(poller.poll() == 0) {}
    poller.pollin(socket2Index) shouldBe true
    socket2.recvStr() shouldBe "ab"
  }
}
