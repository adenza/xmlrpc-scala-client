package com.github.adenza.xmlrpc.client

import java.net.URL

import org.apache.xmlrpc.client.XmlRpcClientConfigImpl

case class XmlRpcScalaConfig(config: XmlRpcClientConfigImpl)

object XmlRpcScalaConfig {
  def apply(serverUrl: URL,
            basicUserName: String,
            basicPassword: String,
            enabledForExceptions: Boolean = true,
            enabledForExtensions: Boolean = false): XmlRpcScalaConfig = {
    val config = new XmlRpcClientConfigImpl()
    config.setServerURL(serverUrl)
    config.setBasicUserName(basicUserName)
    config.setBasicPassword(basicPassword)
    config.setEnabledForExceptions(enabledForExceptions)
    config.setEnabledForExtensions(enabledForExtensions)
    XmlRpcScalaConfig(config)
  }
}
