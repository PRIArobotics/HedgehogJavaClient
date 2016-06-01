package at.pria.hedgehog

import at.pria.hedgehog.protocol.proto.HedgehogP.HedgehogMessage
import org.zeromq.ZMQ.Socket
import zmq.ZMQ

import scala.annotation.tailrec

/**
  * Created by clemens on 01.06.16.
  */
package object protocol {
  type Binary = Array[Byte]
  type Header = Seq[Binary]

  type RawMessage = Binary
  type Message = HedgehogMessage

  val delimiter: Binary = Array()

  implicit class SocketWrapper(socket: Socket) {
    def sendMultipartMore(frames: Binary*): Boolean =
      frames.forall(socket.sendMore)

    def sendMultipart(frames: Binary*): Boolean =
      sendMultipartMore(frames.dropRight(1): _*) && socket.send(frames.last)

    def recvMultipart(): Seq[Binary] = {
      val frames = Seq.newBuilder[Binary]
      frames += socket.recv()
      while(socket.base().getSocketOpt(ZMQ.ZMQ_RCVMORE) != 0)
        frames += socket.recv()
      frames.result
    }
  }

}
