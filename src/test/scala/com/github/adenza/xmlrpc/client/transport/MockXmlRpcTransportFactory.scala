package com.github.adenza.xmlrpc.client.transport

import org.apache.xmlrpc.client.{XmlRpcTransport, XmlRpcTransportFactoryImpl}

/**
  * Mock Factory provides Mock Xml rpc transport
  *
  * @param transport mocked transport
  */
class MockXmlRpcTransportFactory(transport: MockXmlRpcTransport)
    extends XmlRpcTransportFactoryImpl(transport.getClient) {

  override def getTransport: XmlRpcTransport = {
    this.transport
  }
}
