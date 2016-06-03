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

  case class Request(port: Int) extends Message {
    val Type = Request
  }

  object Request extends MessageType[Request] {
    val payloadCase = HedgehogMessage.MOTOR_REQUEST_FIELD_NUMBER

    def getPayload(proto: HedgehogMessage): Msg = {
      val payload = proto.getMotorRequest
      Request(payload.port)
    }

    def setPayload(proto: HedgehogMessage, message: Msg): Unit = {
      val payload = new MotorP.MotorRequest()
      payload.port = message.port
      proto.setMotorRequest(payload)
    }
  }

  case class Update(port: Int, velocity: Int, position: Int) extends Message {
    val Type = Update
  }

  object Update extends MessageType[Update] {
    val payloadCase = HedgehogMessage.MOTOR_UPDATE_FIELD_NUMBER

    def getPayload(proto: HedgehogMessage): Msg = {
      val payload = proto.getMotorUpdate
      Update(payload.port, payload.velocity, payload.position)
    }

    def setPayload(proto: HedgehogMessage, message: Msg): Unit = {
      val payload = new MotorP.MotorUpdate()
      payload.port = message.port
      payload.velocity = message.velocity
      payload.position = message.position
      proto.setMotorUpdate(payload)
    }
  }

  case class StateUpdate(port: Int, state: Int) extends Message {
    val Type = StateUpdate
  }

  object StateUpdate extends MessageType[StateUpdate] {
    val payloadCase = HedgehogMessage.MOTOR_STATE_UPDATE_FIELD_NUMBER

    def getPayload(proto: HedgehogMessage): Msg = {
      val payload = proto.getMotorStateUpdate
      StateUpdate(payload.port, payload.state)
    }

    def setPayload(proto: HedgehogMessage, message: Msg): Unit = {
      val payload = new MotorP.MotorStateUpdate()
      payload.port = message.port
      payload.state = message.state
      proto.setMotorStateUpdate(payload)
    }
  }

  case class SetPositionAction(port: Int, position: Int) extends Message {
    val Type = SetPositionAction
  }

  object SetPositionAction extends MessageType[SetPositionAction] {
    val payloadCase = HedgehogMessage.MOTOR_SET_POSITION_ACTION_FIELD_NUMBER

    def getPayload(proto: HedgehogMessage): Msg = {
      val payload = proto.getMotorSetPositionAction
      SetPositionAction(payload.port, payload.position)
    }

    def setPayload(proto: HedgehogMessage, message: Msg): Unit = {
      val payload = new MotorP.MotorSetPositionAction()
      payload.port = message.port
      payload.position = message.position
      proto.setMotorSetPositionAction(payload)
    }
  }

}
