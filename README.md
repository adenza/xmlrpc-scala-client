# xmlrpc-scala-client

![Scala CI](https://github.com/adenza/xmlrpc-scala-client/workflows/Scala%20CI/badge.svg)
[![codecov](https://codecov.io/gh/adenza/xmlrpc-scala-client/branch/master/graph/badge.svg)](https://codecov.io/gh/adenza/xmlrpc-scala-client)
[![mvn](https://img.shields.io/badge/maven-0.1.0-blue)](https://mvnrepository.com/artifact/com.github.adenza/xmlrpc-scala-client/0.1.0)

Scala XML-RPC wrapper for apache java library https://ws.apache.org/xmlrpc/

It allows to work with input and output object with familar case classes.

## Installation

```sbt
libraryDependencies += "com.github.adenza" %% "xmlrpc-scala-client" % "0.1.0"
```
or 
```sbt
resovers += Resolver.sonatypeReso("snashots")
libraryDependencies += "com.github.adenza" %% "xmlrpc-scala-client" % "0.1.0-SNAPSHOT"
```

## Usage

First define input parameters and expected result:

```scala 
case class InputParams(currency: String, amount: Int)

case class Result(status: String)

```

Now it possible to instantiate client and make a call:

```scala
import com.github.adenza.xmlrpc.client._

val config = XmlRpcScalaConfig(
            serverUrl = new java.net.URL("http://localhost:8080"),
            basicUserName = "user" ,
            basicPassword = "password",
            enabledForExceptions = true,
            enabledForExtensions = false
)

val xmlRpcClient = new XmlRpcScalaClient(config)

val params = InputParams("USD", 1000)

val result: Future[Result] = xmlRpcClient.call[Result]("AddValue", params)

```

## Error handling

```scala
import com.github.adenza.xmlrpc.exceptions._

val handledResult = result.recoverWith { 
  case ex: XmlRpcScalaClientException => throw new Exception("Xml Rpc server return code " + ex.getCode)
  case ex => throw new Exception("Unexpected exception", ex)
}
```
