package at.pria.hedgehog.client

import at.pria.hedgehog.protocol.proto.AckP
import at.pria.hedgehog.protocol.{Ack, Analog, Message, Motor, ReqWrapper}
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
    reply match {
      case Ack.Acknowledgement(AckP.OK, _) => null
      case Ack.Acknowledgement(code, message) => null //TODO
      case _ => reply
    }
  }

  private def sendMultipart(cmds: Message*): Seq[Message] = {
    socket.sendMultipart(cmds: _*)
    socket.recvMultipart()
  }

  def getAnalog(port: Int): Int = {
    send(Analog.Request(port)) match {
      case Analog.Update(`port`, value) => value
    }
  }

  def move(port: Int, amount: Int, state: Int): Unit = {
    send(Motor.Action(port, amount, state, 0, null, null))
  }
}
