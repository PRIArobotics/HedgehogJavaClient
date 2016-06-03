package at.pria.hedgehog.protocol

import at.pria.hedgehog.protocol.proto.HedgehogP.HedgehogMessage
import at.pria.hedgehog.protocol.proto.ServoP

/**
  * Created by clemens on 01.06.16.
  */
object Servo {

  case class Action(port: Int, active: Boolean, position: Int) extends Message {
    val Type = Action
  }

  object Action extends MessageType[Action] {
    val payloadCase = HedgehogMessage.SERVO_ACTION_FIELD_NUMBER

    def getPayload(proto: HedgehogMessage): Msg = {
      val payload = proto.getServoAction
      Action(payload.port, payload.active, payload.position)
    }

    def setPayload(proto: HedgehogMessage, message: Msg): Unit = {
      val payload = new ServoP.ServoAction()
      payload.port = message.port
      payload.active = message.active
      payload.position = message.position
      proto.setServoAction(payload)
    }
  }

}
