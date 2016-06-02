package at.pria.hedgehog.protocol

import at.pria.hedgehog.protocol.proto.AckP.{Acknowledgement, OK}
import at.pria.hedgehog.protocol.proto.HedgehogP.HedgehogMessage
import at.pria.hedgehog.protocol.proto.IOP.{AnalogRequest, AnalogUpdate}
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

    val msg1 = Analog.Request(1)
    req.send(msg1)
    val (header, msg1Recv) = router.recv()
    msg1Recv shouldBe msg1

    val msg2 = Analog.Update(1, 100)
    router.send(header, msg2)
    val msg2Recv = req.recv()
    msg2Recv shouldBe msg2
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

    val msg11 = Analog.Request(1)
    val msg12 = Analog.Request(2)
    req.sendMultipart(msg11, msg12)
    val (header, Seq(msg11Recv, msg12Recv)) = router.recvMultipart()
    msg11Recv shouldBe msg11
    msg12Recv shouldBe msg12

    val msg21 = Analog.Update(1, 200)
    val msg22 = Analog.Update(2, 100)
    router.sendMultipart(header, msg21, msg22)
    val Seq(msg21Recv, msg22Recv) = req.recvMultipart()
    msg21Recv shouldBe msg21
    msg22Recv shouldBe msg22
  }
}
