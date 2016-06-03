package at.pria.hedgehog.client

import at.pria.hedgehog.protocol.proto.{AckP, IOP, ProcessP}
import at.pria.hedgehog.protocol.{Ack, Analog, Binary, Digital, IO, Message, Motor, Process, ReqWrapper, Servo}
import org.zeromq.ZMQ
import org.zeromq.ZMQ.Context

/**
  * Created by clemens on 01.06.16.
  */
class HedgehogClient(endpoint: String)(implicit ctx: Context) {
  private val socket = {
    val socket = ctx.socket(ZMQ.REQ)
    socket.connect(endpoint)
    new ReqWrapper(socket)
  }

  private def send(cmd: Message): Message = {
    val Seq(reply) = sendMultipart(cmd)
    reply match {
      case Ack.Acknowledgement(AckP.OK, _) => null
      case Ack.Acknowledgement(code, message) => null //TODO
      case _ => reply
    }
  }

  private def sendMultipart(cmds: Message*): Seq[Message] = {
    socket.sendMultipart(cmds: _*)
    socket.recvMultipart()
  }

  def getAnalog(port: Int): Int =
    send(Analog.Request(port)) match {
      case Analog.Update(`port`, value) => value
    }

  def setAnalogState(port: Int, pullup: Boolean): Unit =
    send(IO.StateAction(port, if (pullup) IOP.ANALOG_PULLUP else IOP.ANALOG_FLOATING))

  def getDigital(port: Int): Boolean =
    send(Digital.Request(port)) match {
      case Digital.Update(`port`, value) => value
    }

  def setDigitalState(port: Int, pullup: Boolean): Unit =
    send(IO.StateAction(port, if (pullup) IOP.DIGITAL_PULLUP else IOP.DIGITAL_FLOATING))

  def setDigitalOutput(port: Int, level: Boolean): Unit =
    send(IO.StateAction(port, if (level) IOP.OUTPUT_ON else IOP.OUTPUT_OFF))

  private def setMotor(action: Motor.Action, onReached: Motor.ReachedCallback): Unit = {
    //TODO reached callback
    send(action)
  }

  def move(port: Int, amount: Int, state: Int): Unit =
    setMotor(Motor.Action(port, amount, state, 0, null, null), null)

  //def moveRelativePosition(port: Int, amount: Int, relative: Int, state: Int, onReached: Motor.ReachedCallback): Unit =
  //  setMotor(Motor.Action(port, amount, state, 0, relative, null), onReached)
  //
  //def moveAbsolutePosition(port: Int, amount: Int, absolute: Int, state: Int, onReached: Motor.ReachedCallback): Unit =
  //  setMotor(Motor.Action(port, amount, state, 0, null, absolute), onReached)

  def getMotor(port: Int): Motor.Update =
    send(Motor.Request(port)) match {
      case reply@Motor.Update(`port`, _, _) => reply
    }

  def getMotorVelocity(port: Int): Int =
    getMotor(port).velocity

  def getMotorPosition(port: Int): Int =
    getMotor(port).position

  def setMotorPosition(port: Int, position: Int): Unit =
    send(Motor.SetPositionAction(port, position))

  def setServo(port: Int, active: Boolean, position: Int): Unit =
    send(Servo.Action(port, active, position))

  //def executeProcess(workingDir: String, args: Seq[String],
  //                   onStdout: Process.StreamCallback, onStderr: Process.StreamCallback,
  //                   onExit: Process.ExitCallback): Int = {
  //  //TODO reached callback
  //  send(Process.ExecuteRequest(workingDir, args)) match {
  //    case Process.ExecuteReply(pid) => pid
  //  }
  //}
  //
  //def sendProcessData(pid: Int, chunk: Binary): Unit =
  //  send(Process.StreamAction(pid, ProcessP.STDIN, chunk))
}
