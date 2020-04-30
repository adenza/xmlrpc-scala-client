package com.github.adenza.xmlrpc.exceptions

/**
  * Exception of xml rpc deserialization
  *
  * @param message Error Message
  */
class XmlRpcDeserializationException(message: String) extends XmlRpcScalaClientException(0, message)
