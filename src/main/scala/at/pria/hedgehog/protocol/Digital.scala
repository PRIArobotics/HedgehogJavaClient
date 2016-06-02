package at.pria.hedgehog.protocol

import at.pria.hedgehog.protocol.proto.HedgehogP.HedgehogMessage
import at.pria.hedgehog.protocol.proto.IOP

/**
  * Created by clemens on 01.06.16.
  */
object Digital {

  case class Request(port: Int) extends Message {
    val Type = Request
  }

  object Request extends MessageType[Request] {
    val payloadCase = HedgehogMessage.DIGITAL_REQUEST_FIELD_NUMBER

    def getPayload(proto: HedgehogMessage): Msg = {
      val payload = proto.getDigitalRequest
      Request(payload.port)
    }

    def setPayload(proto: HedgehogMessage, message: Msg): Unit = {
      val payload = new IOP.DigitalRequest()
      payload.port = message.port
      proto.setDigitalRequest(payload)
    }
  }

  case class Update(port: Int, value: Boolean) extends Message {
    val Type = Update
  }

  object Update extends MessageType[Update] {
    val payloadCase = HedgehogMessage.DIGITAL_UPDATE_FIELD_NUMBER

    def getPayload(proto: HedgehogMessage): Msg = {
      val payload = proto.getDigitalUpdate
      Update(payload.port, payload.value)
    }

    def setPayload(proto: HedgehogMessage, message: Msg): Unit = {
      val payload = new IOP.DigitalUpdate()
      payload.port = message.port
      payload.value = message.value
      proto.setDigitalUpdate(payload)
    }
  }

}
