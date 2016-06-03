package at.pria.hedgehog.protocol

import at.pria.hedgehog.protocol.proto.HedgehogP.HedgehogMessage
import at.pria.hedgehog.protocol.proto.ProcessP

/**
  * Created by clemens on 01.06.16.
  */
object Process {

  case class ExecuteRequest(workingDir: String, args: Seq[String]) extends Message {
    val Type = ExecuteRequest
  }

  object ExecuteRequest extends MessageType[ExecuteRequest] {
    val payloadCase = HedgehogMessage.PROCESS_EXECUTE_REQUEST_FIELD_NUMBER

    def getPayload(proto: HedgehogMessage): Msg = {
      val payload = proto.getProcessExecuteRequest
      ExecuteRequest(payload.workingDir, payload.args)
    }

    def setPayload(proto: HedgehogMessage, message: Msg): Unit = {
      val payload = new ProcessP.ProcessExecuteRequest()
      payload.workingDir = message.workingDir
      payload.args = message.args.toArray
      proto.setProcessExecuteRequest(payload)
    }
  }

  case class ExecuteReply(pid: Int) extends Message {
    val Type = ExecuteReply
  }

  object ExecuteReply extends MessageType[ExecuteReply] {
    val payloadCase = HedgehogMessage.PROCESS_EXECUTE_REPLY_FIELD_NUMBER

    def getPayload(proto: HedgehogMessage): Msg = {
      val payload = proto.getProcessExecuteReply
      ExecuteReply(payload.pid)
    }

    def setPayload(proto: HedgehogMessage, message: Msg): Unit = {
      val payload = new ProcessP.ProcessExecuteReply()
      payload.pid = message.pid
      proto.setProcessExecuteReply(payload)
    }
  }

  case class StreamAction(pid: Int, fileno: Int, chunk: Binary) extends Message {
    val Type = StreamAction
  }

  object StreamAction extends MessageType[StreamAction] {
    val payloadCase = HedgehogMessage.PROCESS_STREAM_ACTION_FIELD_NUMBER

    def getPayload(proto: HedgehogMessage): Msg = {
      val payload = proto.getProcessStreamAction
      StreamAction(payload.pid, payload.fileno, payload.chunk)
    }

    def setPayload(proto: HedgehogMessage, message: Msg): Unit = {
      val payload = new ProcessP.ProcessStreamAction()
      payload.pid = message.pid
      payload.fileno = message.fileno
      payload.chunk = message.chunk
      proto.setProcessStreamAction(payload)
    }
  }

  case class StreamUpdate(pid: Int, fileno: Int, chunk: Binary) extends Message {
    val Type = StreamUpdate
  }

  object StreamUpdate extends MessageType[StreamUpdate] {
    val payloadCase = HedgehogMessage.PROCESS_STREAM_UPDATE_FIELD_NUMBER

    override val async = true

    def getPayload(proto: HedgehogMessage): Msg = {
      val payload = proto.getProcessStreamUpdate
      StreamUpdate(payload.pid, payload.fileno, payload.chunk)
    }

    def setPayload(proto: HedgehogMessage, message: Msg): Unit = {
      val payload = new ProcessP.ProcessStreamUpdate()
      payload.pid = message.pid
      payload.fileno = message.fileno
      payload.chunk = message.chunk
      proto.setProcessStreamUpdate(payload)
    }
  }

  type StreamCallback = StreamUpdate => Unit

  case class ExitUpdate(pid: Int, exitCode: Int) extends Message {
    val Type = ExitUpdate
  }

  type ExitCallback = ExitUpdate => Unit

  object ExitUpdate extends MessageType[ExitUpdate] {
    val payloadCase = HedgehogMessage.PROCESS_EXIT_UPDATE_FIELD_NUMBER

    override val async = true

    def getPayload(proto: HedgehogMessage): Msg = {
      val payload = proto.getProcessExitUpdate
      ExitUpdate(payload.pid, payload.exitCode)
    }

    def setPayload(proto: HedgehogMessage, message: Msg): Unit = {
      val payload = new ProcessP.ProcessExitUpdate()
      payload.pid = message.pid
      payload.exitCode = message.exitCode
      proto.setProcessExitUpdate(payload)
    }
  }

}
