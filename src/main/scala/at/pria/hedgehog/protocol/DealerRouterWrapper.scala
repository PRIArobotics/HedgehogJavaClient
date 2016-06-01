package at.pria.hedgehog.protocol

import at.pria.hedgehog.client.protocol.proto.HedgehogP.HedgehogMessage
import com.google.protobuf.nano.MessageNano
import org.zeromq.ZMQ.Socket

/**
  * Created by clemens on 01.06.16.
  */
class DealerRouterWrapper(socket: Socket) {
  def sendRaw(header: Header, msgRaw: RawMessage): Boolean =
    sendMultipartRaw(header, msgRaw)

  def sendMultipartRaw(header: Header, msgsRaw: RawMessage*): Boolean =
    socket.sendMultipartMore(header: _*) &&
      socket.sendMore(delimiter) &&
      socket.sendMultipart(msgsRaw: _*)

  def send(header: Header, msg: Message): Boolean =
    sendMultipart(header, msg)

  def sendMultipart(header: Header, msgs: Message*): Boolean =
    sendMultipartRaw(header, (for(msg <- msgs) yield MessageNano.toByteArray(msg)): _*)

  def recvRaw(): (Header, RawMessage) = {
    val (header, Seq(msgRaw)) = recvMultipartRaw()
    (header, msgRaw)
  }

  def recvMultipartRaw(): (Header, Seq[RawMessage]) = {
    val frames = socket.recvMultipart()
    val index = frames.lastIndexWhere((frame) => frame.isEmpty)
    (frames.take(index), frames.drop(index + 1))
  }

  def recv(): (Header, Message) = {
    val (header, Seq(msg)) = recvMultipart()
    (header, msg)
  }

  def recvMultipart(): (Header, Seq[Message]) = {
    val (header, msgsRaw) = recvMultipartRaw()
    (header, for(msgRaw <- msgsRaw) yield HedgehogMessage.parseFrom(msgRaw))
  }

  def close() =
    socket.close()
}
