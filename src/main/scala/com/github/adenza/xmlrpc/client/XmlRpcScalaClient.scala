package com.github.adenza.xmlrpc.client

import com.github.adenza.xmlrpc.exceptions.XmlRpcScalaClientException
import org.apache.xmlrpc.XmlRpcException
import org.apache.xmlrpc.client.XmlRpcClient

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

/**
  * Scala XmlRpc client
  * @param config XmlRpcClientConfigImpl
  * @param xmlRpcClient XmlRpcClient
  * @param executionContext ExecutionContext
  */
class XmlRpcScalaClient(
  config: XmlRpcScalaConfig,
  xmlRpcClient: XmlRpcClient = new XmlRpcClient
)(implicit val executionContext: ExecutionContext) {

  xmlRpcClient.setConfig(config)

  /**
    * Make call to XMLRPC service
    *
    * @param methodName XmlRpc method
    * @param params case class parameters
    * @tparam RESPONSE case class response type
    * @return
    */
  def call[RESPONSE <: Product: TypeTag: ClassTag](
    methodName: String,
    params: Product
  ): concurrent.Future[RESPONSE] =
    Future {
      val javaParams = XmlRpcSerializer.toParams(params)
      scala.util.Try {
        xmlRpcClient.execute(methodName, javaParams)
      } match {
        case scala.util.Failure(ex: XmlRpcException) =>
          throw new XmlRpcScalaClientException(ex.code, ex.getMessage)
        case scala.util.Failure(ex) =>
          throw new XmlRpcScalaClientException(0, ex.getMessage)
        case scala.util.Success(response) =>
          XmlRpcSerializer.fromResponse[RESPONSE](response)
      }
    }
}
