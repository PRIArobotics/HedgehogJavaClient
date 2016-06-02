package at.pria.hedgehog.protocol

import at.pria.hedgehog.protocol.proto.HedgehogP.HedgehogMessage
import at.pria.hedgehog.protocol.proto.IOP

/**
  * Created by clemens on 01.06.16.
  */
object Analog {

  case class Request(port: Int) extends Message {
    val Type = Request
  }

  object Request extends MessageType[Request] {
    val payloadCase = HedgehogMessage.ANALOG_REQUEST_FIELD_NUMBER

    def getPayload(proto: HedgehogMessage): Msg = {
      val payload = proto.getAnalogRequest
      Request(payload.port)
    }

    def setPayload(proto: HedgehogMessage, message: Msg): Unit = {
      val payload = new IOP.AnalogRequest()
      payload.port = message.port
      proto.setAnalogRequest(payload)
    }
  }

  case class Update(port: Int, value: Int) extends Message {
    val Type = Update
  }

  object Update extends MessageType[Update] {
    val payloadCase = HedgehogMessage.ANALOG_UPDATE_FIELD_NUMBER

    def getPayload(proto: HedgehogMessage): Msg = {
      val payload = proto.getAnalogUpdate
      Update(payload.port, payload.value)
    }

    def setPayload(proto: HedgehogMessage, message: Msg): Unit = {
      val payload = new IOP.AnalogUpdate()
      payload.port = message.port
      payload.value = message.value
      proto.setAnalogUpdate(payload)
    }
  }

}
