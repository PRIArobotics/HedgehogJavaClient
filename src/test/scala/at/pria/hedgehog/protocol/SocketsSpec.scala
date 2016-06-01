package at.pria.hedgehog.protocol

import at.pria.hedgehog.client.protocol.proto.AckP.{Acknowledgement, OK}
import at.pria.hedgehog.client.protocol.proto.HedgehogP.HedgehogMessage
import at.pria.hedgehog.client.protocol.proto.IOP.{AnalogRequest, AnalogUpdate}
import org.scalatest.{FlatSpec, Matchers}
import org.zeromq.ZMQ

/**
  * Created by clemens on 31.05.16.
  */
class SocketsSpec extends FlatSpec with Matchers {
  behavior of "Socket wrappers"

  they should "send raw messages correctly" in {
    val ctx = ZMQ.context(1)
    val endpoint = "inproc://test"

    val router = new DealerRouterWrapper({
      val router = ctx.socket(ZMQ.ROUTER)
      router.bind(endpoint)
      router
    })

    val req = new ReqWrapper({
      val router = ctx.socket(ZMQ.REQ)
      router.connect(endpoint)
      router
    })

    val msg1 = "ab".getBytes("utf-8")
    req.sendRaw(msg1)
    val (header, msg1Recv) = router.recvRaw()
    msg1Recv shouldBe msg1

    val msg2 = "cd".getBytes("utf-8")
    router.sendRaw(header, msg2)
    val msg2Recv = req.recvRaw()
    msg2Recv shouldBe msg2
  }

  they should "send raw multipart messages correctly" in {
    val ctx = ZMQ.context(1)
    val endpoint = "inproc://test"

    val router = new DealerRouterWrapper({
      val router = ctx.socket(ZMQ.ROUTER)
      router.bind(endpoint)
      router
    })

    val req = new ReqWrapper({
      val router = ctx.socket(ZMQ.REQ)
      router.connect(endpoint)
      router
    })

    val msg1 = for(str <- Seq("a", "b")) yield str.getBytes("utf-8")
    req.sendMultipartRaw(msg1: _*)
    val (header, msg1Recv) = router.recvMultipartRaw()
    msg1Recv shouldBe msg1

    val msg2 = for(str <- Seq("c", "d")) yield str.getBytes("utf-8")
    router.sendMultipartRaw(header, msg2: _*)
    val msg2Recv = req.recvMultipartRaw()
    msg2Recv shouldBe msg2
  }

  they should "send messages correctly" in {
    val ctx = ZMQ.context(1)
    val endpoint = "inproc://test"

    val router = new DealerRouterWrapper({
      val router = ctx.socket(ZMQ.ROUTER)
      router.bind(endpoint)
      router
    })

    val req = new ReqWrapper({
      val router = ctx.socket(ZMQ.REQ)
      router.connect(endpoint)
      router
    })

    val msg1 = {
      val msg = new HedgehogMessage()
      val req = new AnalogRequest()
      req.port = 1
      msg.setAnalogRequest(req)
      msg
    }
    req.send(msg1)
    val (header, msg1Recv) = router.recv()
    msg1Recv.getPayloadCase shouldBe msg1.getPayloadCase
    msg1Recv.getAnalogRequest.port shouldBe msg1.getAnalogRequest.port

    val msg2 = {
      val msg = new HedgehogMessage()
      val upd = new AnalogUpdate()
      upd.port = 1
      upd.value = 200
      msg.setAnalogUpdate(upd)
      msg
    }
    router.send(header, msg2)
    val msg2Recv = req.recv()
    msg2Recv.getPayloadCase shouldBe msg2.getPayloadCase
    msg2Recv.getAnalogUpdate.port shouldBe msg2.getAnalogUpdate.port
    msg2Recv.getAnalogUpdate.value shouldBe msg2.getAnalogUpdate.value
  }

  they should "send multipart messages correctly" in {
    val ctx = ZMQ.context(1)
    val endpoint = "inproc://test"

    val router = new DealerRouterWrapper({
      val router = ctx.socket(ZMQ.ROUTER)
      router.bind(endpoint)
      router
    })

    val req = new ReqWrapper({
      val router = ctx.socket(ZMQ.REQ)
      router.connect(endpoint)
      router
    })

    val msg11 = {
      val msg = new HedgehogMessage()
      val req = new AnalogRequest()
      req.port = 1
      msg.setAnalogRequest(req)
      msg
    }
    val msg12 = {
      val msg = new HedgehogMessage()
      val req = new AnalogRequest()
      req.port = 2
      msg.setAnalogRequest(req)
      msg
    }
    req.sendMultipart(msg11, msg12)
    val (header, Seq(msg11Recv, msg12Recv)) = router.recvMultipart()
    msg11Recv.getPayloadCase shouldBe msg11.getPayloadCase
    msg11Recv.getAnalogRequest.port shouldBe msg11.getAnalogRequest.port
    msg12Recv.getPayloadCase shouldBe msg12.getPayloadCase
    msg12Recv.getAnalogRequest.port shouldBe msg12.getAnalogRequest.port

    val msg21 = {
      val msg = new HedgehogMessage()
      val upd = new AnalogUpdate()
      upd.port = 1
      upd.value = 200
      msg.setAnalogUpdate(upd)
      msg
    }
    val msg22 = {
      val msg = new HedgehogMessage()
      val upd = new AnalogUpdate()
      upd.port = 2
      upd.value = 100
      msg.setAnalogUpdate(upd)
      msg
    }
    router.sendMultipart(header, msg21, msg22)
    val Seq(msg21Recv, msg22Recv) = req.recvMultipart()
    msg21Recv.getPayloadCase shouldBe msg21.getPayloadCase
    msg21Recv.getAnalogUpdate.port shouldBe msg21.getAnalogUpdate.port
    msg21Recv.getAnalogUpdate.value shouldBe msg21.getAnalogUpdate.value
    msg22Recv.getPayloadCase shouldBe msg22.getPayloadCase
    msg22Recv.getAnalogUpdate.port shouldBe msg22.getAnalogUpdate.port
    msg22Recv.getAnalogUpdate.value shouldBe msg22.getAnalogUpdate.value
  }
}
