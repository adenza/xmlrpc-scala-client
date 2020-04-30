package com.github.adenza.xmlrpc.exceptions

/**
  * Exception for xml rpc server errors which has code
  *
  * @param code Processor result code
  * @param message Processor result text
  */
class XmlRpcScalaClientException(code: Int = 0, message: String = "Generic error occurred") extends Exception(message) {
  def getCode: Int = code
}
