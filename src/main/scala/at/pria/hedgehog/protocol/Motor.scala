package at.pria.hedgehog.protocol

import at.pria.hedgehog.protocol.proto.HedgehogP.HedgehogMessage
import at.pria.hedgehog.protocol.proto.MotorP

/**
  * Created by clemens on 01.06.16.
  */
object Motor {

  case class Action(port: Int, state: Int, amount: Int, reachedState: Int, relative: Integer, absolute: Integer) extends Message {
    val Type = Action
  }

  object Action extends MessageType[Action] {
    val payloadCase = HedgehogMessage.MOTOR_ACTION_FIELD_NUMBER

    def getPayload(proto: HedgehogMessage): Msg = {
      val payload = proto.getMotorAction
      val relative = if(payload.hasRelative) payload.getRelative: Integer else null
      val absolute = if(payload.hasAbsolute) payload.getAbsolute: Integer else null
      Action(payload.port, payload.state, payload.amount, payload.reachedState, relative, absolute)
    }

    def setPayload(proto: HedgehogMessage, message: Msg): Unit = {
      val payload = new MotorP.MotorAction()
      payload.port = message.port
      payload.state = message.state
      payload.amount = message.amount
      payload.reachedState = message.reachedState
      (message.relative, message.absolute) match {
        case (null, null) =>
        case (relative, null) => payload.setRelative(relative)
        case (null, absolute) => payload.setAbsolute(absolute)
      }
      proto.setMotorAction(payload)
    }
  }

}
