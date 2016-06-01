package at.pria.hedgehog.client

import at.pria.hedgehog.protocol.proto.AckP
import at.pria.hedgehog.protocol.proto.HedgehogP.HedgehogMessage
import at.pria.hedgehog.protocol.proto.IOP.AnalogRequest
import at.pria.hedgehog.protocol.proto.MotorP.MotorAction
import at.pria.hedgehog.protocol.{Message, ReqWrapper}
import org.zeromq.ZMQ
import org.zeromq.ZMQ.Context

/**
  * Created by clemens on 01.06.16.
  */
class HedgehogClient(endpoint: String)(implicit ctx: Context) {
  private val socket = {
    val socket = ctx.socket(ZMQ.REQ)
    socket.connect(endpoint)
    new ReqWrapper(socket)
  }

  private def send(cmd: Message): Message = {
    val Seq(reply) = sendMultipart(cmd)
    val ack = reply.getAcknowledgement
    if(ack != null)
      if(ack.code != AckP.OK)
        null //TODO
      else
        null
    else
      reply
  }

  private def sendMultipart(cmds: Message*): Seq[Message] = {
    socket.sendMultipart(cmds: _*)
    socket.recvMultipart()
  }

  def getAnalog(port: Int): Int = {
    val payload = new AnalogRequest()
    payload.port = port

    val msg = new HedgehogMessage()
    msg.setAnalogRequest(payload)

    val reply = send(msg).getAnalogUpdate
    assert(reply.port == port)
    reply.value
  }

  def move(port: Int, amount: Int, state: Int): Unit = {
    val payload = new MotorAction()
    payload.port = port
    payload.amount = amount
    payload.state = state

    val msg = new HedgehogMessage()
    msg.setMotorAction(payload)

    send(msg)
  }
}
