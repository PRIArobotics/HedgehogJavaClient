package at.pria.hedgehog.client

import at.pria.hedgehog.protocol.proto.HedgehogP.HedgehogMessage
import at.pria.hedgehog.protocol.proto.AckP.{Acknowledgement, OK}
import at.pria.hedgehog.protocol.proto.MotorP.POWER
import org.scalatest.{FlatSpec, Matchers}
import org.zeromq.ZMQ

/**
  * Created by clemens on 31.05.16.
  */
class ClientSpec extends FlatSpec with Matchers {
  behavior of "HedgehogClient"

  it should "be able to access sensors and motors" in {
    implicit val ctx = ZMQ.context(1)
    val endpoint = "tcp://127.0.0.1:5555"

//    val server = ctx.socket(ZMQ.ROUTER)
//    server.bind(endpoint)

    val client = new HedgehogClient(endpoint)
    System.err.println(client.getAnalog(0))
    client.move(0, 1000, POWER)
    Thread.sleep(1000)
    client.move(0, 0, POWER)

//    {
//      val payload = new Acknowledgement()
//      payload.code = OK
//
//      val msg = new HedgehogMessage()
//      msg.setAcknowledgement(payload)
//
//    }
  }
}
