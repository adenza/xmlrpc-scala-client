package com.github.adenza.xmlrpc.client

import java.io.ByteArrayInputStream
import java.time.{LocalDateTime, OffsetDateTime, ZoneOffset}
import java.util.UUID

import com.github.adenza.xmlrpc.client.models._
import com.github.adenza.xmlrpc.client.transport.{MockXmlRpcTransport, MockXmlRpcTransportFactory}
import com.github.adenza.xmlrpc.exceptions.XmlRpcScalaClientException
import org.apache.xmlrpc.client.{XmlRpcClient, XmlRpcClientConfigImpl}
import org.mockito.{ArgumentMatchersSugar, MockitoSugar}
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.should.Matchers

/**
  * Test XmlRpc types https://ws.apache.org/xmlrpc/types.html
  */
class XmlRpcClientTest extends AsyncFunSuite with Matchers with MockitoSugar with ArgumentMatchersSugar {

  val config = new XmlRpcClientConfigImpl()
  val xmlRpcClient: XmlRpcClient = new XmlRpcClient
  val transport = new MockXmlRpcTransport(xmlRpcClient)
  val xmlRpcScalaClient: XmlRpcScalaClient = {
    config.setEnabledForExceptions(true)
    config.setEnabledForExtensions(true)
    new XmlRpcScalaClient(config, xmlRpcClient)
  }

  test("CardDetailResponse mapped successfully from XML response") {
    val response: String =
      """<?xml version="1.0" encoding="UTF-8"?>
        |<methodResponse xmlns:ex="http://ws.apache.org/xmlrpc/namespaces/extensions">
        |  <params>
        |    <param><value><struct>
        |    <member><name>intValue</name><value><int>123</int></value></member>
        |    <member><name>booleanValue</name><value><boolean>1</boolean></value></member>
        |    <member><name>stringValue</name><value><string>ABC</string></value></member>
        |    <member><name>doubleValue</name><value><double>12.34</double></value></member>
        |    <member><name>dateValue</name><value><dateTime.iso8601>20230331T00:00:00</dateTime.iso8601></value></member>
        |    <member><name>byteValue</name><value><base64>YWI=</base64></value></member>
        |    <member><name>resultText</name><value><string>Approved</string></value></member>
        |    <member><name>serverTransactionID</name><value><string>09EF5260-155D-00FA-994B4AEE4AE1DAD8</string></value></member>
        |    <member><name>cardLabel</name><value><string>TestCard</string></value></member>
        |    <member><name>structType</name>
        |      <value>
        |        <struct>
        |          <member><name>intValue</name><value><int>123</int></value></member>
        |        </struct>
        |      </value>
        |    </member>
        |    <member><name>optionStructType</name>
        |      <value>
        |        <array><data>
        |          <value><struct>
        |            <member><name>intValue</name><value><int>123</int></value></member>
        |          </struct></value>
        |        </data></array>
        |      </value>
        |    </member>
        |    <member><name>seqStringValue</name>
        |      <value>
        |        <array><data>
        |          <value><string>TAG1</string></value>
        |          <value><string>TAG2</string></value>
        |        </data></array>
        |      </value>
        |    </member>
        |    <member><name>listStringValue</name>
        |      <value>
        |        <array><data>
        |          <value><string>TAG1</string></value>
        |          <value><string>TAG2</string></value>
        |        </data></array>
        |      </value>
        |    </member>
        |    <member><name>listInListValue</name>
        |      <value>
        |        <array><data>
        |          <value>
        |            <array><data>
        |              <value><string>TAG33</string></value>
        |              <value><string>TAG44</string></value>
        |            </data></array>
        |          </value>
        |        </data></array>
        |      </value>
        |    </member>
        |
        |    <member><name>exNilValue</name><value><ex:nil></ex:nil></value></member>
        |    <member><name>exByteValue</name><value><ex:i1>25</ex:i1></value></member>
        |    <member><name>exFloatValue</name><value><ex:float>123.45</ex:float></value></member>
        |    <member><name>exLongValue</name><value><ex:i8>123</ex:i8></value></member>
        |    <member><name>exShortValue</name><value><ex:i2>123</ex:i2></value></member>
        |    <member><name>exBigDecimalValue</name><value><ex:bigdecimal>123456789012.12</ex:bigdecimal></value></member>
        |    <member><name>exBigIntegerValue</name><value><ex:biginteger>1234567890123456</ex:biginteger></value></member>
        |    <member><name>exDateTimeValue</name><value><ex:dateTime>1991-01-01T01:02:03+08:00</ex:dateTime></value></member>
        |    <member><name>uuidValue</name><value><string>a0931628-9b15-4f8b-9171-19168c1d9301</string></value></member>
        |    </struct></value></param>
        |  </params>
        |</methodResponse>
        |""".stripMargin

    val spiedTransport: MockXmlRpcTransport = spy(transport)
    xmlRpcClient.setTransportFactory(new MockXmlRpcTransportFactory(spiedTransport))
    when(spiedTransport.xmlResponseStream).thenReturn(new ByteArrayInputStream(response.getBytes()))

    val result = xmlRpcScalaClient.call[ClientResponseComplexModel]("methodName", (1, 2))
    result.map { response =>
      assert(response.intValue === 123)
      assert(response.booleanValue === true)
      assert(response.stringValue === "ABC")
      assert(response.doubleValue === 12.34)
      assert(response.dateValue === LocalDateTime.parse("2023-03-31T00:00:00"))
      assert(response.byteValue === Array('a'.toByte, 'b'.toByte))
      assert(response.structType === ClientResponseNestedModel(intValue = 123))
      assert(response.optionStructType === Some(ClientResponseNestedModel(intValue = 123)))
      assert(response.seqStringValue === Seq("TAG1", "TAG2"))
      assert(response.listStringValue === List("TAG1", "TAG2"))
      assert(response.listInListValue === Seq(Seq("TAG33", "TAG44")))
      assert(response.exNilValue === None)
      assert(response.exByteValue === 25.toByte)
      assert(response.exFloatValue === 123.45.toFloat)
      assert(response.exLongValue === 123)
//          //  exDom: org.w3c.dom.Node,
      assert(response.exShortValue === 123)
//          //exSerializableValue: Serializable
      assert(response.exBigDecimalValue === scala.math.BigDecimal("123456789012.12"))
      assert(response.exBigIntegerValue === scala.math.BigInt("1234567890123456"))
      assert(response.exDateTimeValue === OffsetDateTime.parse("1991-01-01T00:02:03+07:00").toInstant.atOffset(OffsetDateTime.now().getOffset))
      assert(response.uuidValue === UUID.fromString("a0931628-9b15-4f8b-9171-19168c1d9301"))
    }
  }

  test("CardDetailResponse Fault Result") {
    val response: String =
      """<?xml version="1.0"?>
        |<methodResponse>
        |  <fault>
        |    <value>
        |      <struct>
        |      <member>
        |        <name>faultCode</name>
        |        <value><int>4</int></value>
        |      </member>
        |      <member>
        |        <name>faultString</name>
        |        <value>
        |          <string>Too many parameters.</string>
        |        </value>
        |      </member>
        |      </struct>
        |    </value>
        |  </fault>
        |</methodResponse> 
        |""".stripMargin

    val spiedTransport: MockXmlRpcTransport = spy(transport)
    xmlRpcClient.setTransportFactory(new MockXmlRpcTransportFactory(spiedTransport))
    when(spiedTransport.xmlResponseStream).thenReturn(new ByteArrayInputStream(response.getBytes()))

    recoverToExceptionIf[XmlRpcScalaClientException] {
      xmlRpcScalaClient.call[ClientResponseComplexModel]("methodName", (1, 2))
    }.map { ex =>
      assert(ex.getCode == 4)
      assert(ex.getMessage == "Too many parameters.")
    }
  }

  test("CardDetailResponse Exception") {
    val response: String = ""

    val spiedTransport: MockXmlRpcTransport = spy(transport)
    xmlRpcClient.setTransportFactory(new MockXmlRpcTransportFactory(spiedTransport))
    when(spiedTransport.xmlResponseStream).thenReturn(new ByteArrayInputStream(response.getBytes()))

    recoverToExceptionIf[XmlRpcScalaClientException] {
      xmlRpcScalaClient.call[ClientResponseComplexModel]("methodName", (1, 2))
    }.map { ex =>
      assert(ex.getCode == 0)
      assert(
        ex.getMessage == "Failed to parse server's response: Premature end of file."
      )
    }
  }

  test("Unexpected Exception") {
    val mockClient = mock[XmlRpcClient]
    val config = new XmlRpcClientConfigImpl()
    val mockRpcScalaClient = new XmlRpcScalaClient(config, mockClient)

    when(mockClient.execute(any, any[Array[Object]])).thenThrow(new Exception("Unexpected Exception"))
    recoverToExceptionIf[XmlRpcScalaClientException] {
      mockRpcScalaClient.call[ClientResponseComplexModel]("methodName", ("1", "2"))
    }.map { ex =>
      assert(ex.getCode == 0)
      assert(ex.getMessage == "Unexpected Exception")
    }
  }

}
