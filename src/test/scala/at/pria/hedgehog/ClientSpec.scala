package at.pria.hedgehog

import org.scalatest.{FlatSpec, Matchers}
import at.pria.hedgehog.protocol.proto.{AckP, HedgehogP}
import HedgehogP.HedgehogMessage
import AckP.{Acknowledgement, OK}
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
    socket2.recvStr() shouldBe "ab"
  }
}
