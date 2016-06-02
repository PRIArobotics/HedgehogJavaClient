package at.pria.hedgehog.protocol

import at.pria.hedgehog.protocol.proto.HedgehogP.HedgehogMessage
import at.pria.hedgehog.protocol.proto.AckP

/**
  * Created by clemens on 01.06.16.
  */
object Ack {

  case class Acknowledgement(code: Int, message: String) extends Message {
    val Type = Acknowledgement
  }

  object Acknowledgement extends MessageType[Acknowledgement] {
    val payloadCase = HedgehogMessage.ACKNOWLEDGEMENT_FIELD_NUMBER

    def getPayload(proto: HedgehogMessage): Msg = {
      val payload = proto.getAcknowledgement
      Acknowledgement(payload.code, payload.message)
    }

    def setPayload(proto: HedgehogMessage, message: Msg): Unit = {
      val payload = new AckP.Acknowledgement()
      payload.code = message.code
      payload.message = message.message
      proto.setAcknowledgement(payload)
    }
  }

}
