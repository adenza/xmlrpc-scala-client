package com.github.adenza.xmlrpc.client.transport

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, InputStream}

import org.apache.xmlrpc.client.{XmlRpcClient, XmlRpcStreamTransport}
import org.apache.xmlrpc.common.XmlRpcStreamRequestConfig

/**
  * Mocked transport
  *
  * @param pClient XML RPC client
  */
class MockXmlRpcTransport(pClient: XmlRpcClient) extends XmlRpcStreamTransport(pClient) {

  override def close(): Unit = Unit

  override def isResponseGzipCompressed(pConfig: XmlRpcStreamRequestConfig): Boolean = false

  /**
    * Set XML response byte stream
    *
    * @return
    */
  def xmlResponseStream: InputStream =
    new ByteArrayInputStream(
      """<methodResponse>
      |  <fault>
      |    <value>
      |      <struct>
      |        <member>
      |          <name>faultCode</name>
      |          <value>
      |            <int>0</int>
      |          </value>
      |        </member>
      |        <member>
      |          <name>faultString</name>
      |          <value>
      |            <string>This is default XML fault response. Please, provide your response instead</string>
      |          </value>
      |        </member>
      |      </struct>
      |    </value>
      |  </fault>
      |</methodResponse>""".stripMargin
        .getBytes()
    )

  override def getInputStream: InputStream = xmlResponseStream

  /**
    * This class is useful to verify xml request was constructed
    *
    * @return
    */
  val xmlRequestStream = new ByteArrayOutputStream

  override def writeRequest(pWriter: XmlRpcStreamTransport.ReqWriter): Unit = {
    pWriter.write(xmlRequestStream)
  }
}
