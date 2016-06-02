package at.pria.hedgehog.protocol

import at.pria.hedgehog.protocol.proto.HedgehogP.HedgehogMessage
import com.google.protobuf.nano.MessageNano

/**
  * Created by clemens on 01.06.16.
  */
trait Message {
  val Type: MessageType[_]
}

object Message {
  val fieldToType = {
    val fieldToType = Map.newBuilder[Int, MessageType[_ <: Message]]

    def register(Type: MessageType[_ <: Message]) = {
      fieldToType += ((Type.payloadCase, Type))
    }

    register(Ack.Acknowledgement)
    register(IO.StateAction)
    register(Analog.Request)
    register(Analog.Update)

    fieldToType.result()
  }

  def parse(data: Array[Byte]): Message = {
    val proto = HedgehogMessage.parseFrom(data)

    val Type = fieldToType(proto.getPayloadCase)
    Type.getPayload(proto)
  }

  def serialize[Msg <: Message](message: Msg): Array[Byte] = {
    val Type = message.Type.asInstanceOf[MessageType[Msg]]

    val proto = new HedgehogMessage()
    Type.setPayload(proto, message)
    MessageNano.toByteArray(proto)
  }
}

trait MessageType[Msg_ <: Message] {
  type Msg = Msg_
  val payloadCase: Int

  val async = false

  def getPayload(proto: HedgehogMessage): Msg
  def setPayload(proto: HedgehogMessage, message: Msg): Unit
}