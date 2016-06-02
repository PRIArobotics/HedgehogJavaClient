package at.pria.hedgehog.protocol

import at.pria.hedgehog.protocol.proto.HedgehogP.HedgehogMessage
import at.pria.hedgehog.protocol.proto.IOP

/**
  * Created by clemens on 01.06.16.
  */
object IO {

  case class StateAction(port: Int, flags: Int) extends Message {
    val Type = StateAction
  }

  object StateAction extends MessageType[StateAction] {
    val payloadCase = HedgehogMessage.IO_STATE_ACTION_FIELD_NUMBER

    def getPayload(proto: HedgehogMessage): Msg = {
      val payload = proto.getIoStateAction
      StateAction(payload.port, payload.flags)
    }

    def setPayload(proto: HedgehogMessage, message: Msg): Unit = {
      val payload = new IOP.IOStateAction()
      payload.port = message.port
      payload.flags = message.flags
      proto.setIoStateAction(payload)
    }
  }

}
