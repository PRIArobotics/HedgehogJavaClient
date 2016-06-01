package at.pria.hedgehog.protocol

import at.pria.hedgehog.protocol.proto.HedgehogP.HedgehogMessage
import com.google.protobuf.nano.MessageNano
import org.zeromq.ZMQ.Socket

/**
  * Created by clemens on 01.06.16.
  */
class ReqWrapper(socket: Socket) {
  def sendRaw(msgRaw: RawMessage): Boolean =
    sendMultipartRaw(msgRaw)

  def sendMultipartRaw(msgsRaw: RawMessage*): Boolean =
    socket.sendMultipart(msgsRaw: _*)

  def send(msg: Message): Boolean =
    sendMultipart(msg)

  def sendMultipart(msgs: Message*): Boolean =
    sendMultipartRaw((for(msg <- msgs) yield MessageNano.toByteArray(msg)): _*)

  def recvRaw(): RawMessage = {
    val Seq(msgRaw) = recvMultipartRaw()
    msgRaw
  }

  def recvMultipartRaw(): Seq[RawMessage] =
    socket.recvMultipart()

  def recv(): Message = {
    val Seq(msg) = recvMultipart()
    msg
  }

  def recvMultipart(): Seq[Message] = {
    for(msgRaw <- recvMultipartRaw()) yield HedgehogMessage.parseFrom(msgRaw)
  }

  def close() =
    socket.close()
}
