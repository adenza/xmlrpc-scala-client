organization := "com.github.adenza"
name := "xmlrpc-scala-client"
description := "XML-RPC Scala client"
startYear := Some(2020)
scmInfo := Some(
  ScmInfo(
    browseUrl = url("https://github.com/adenza/xmlrpc-scala-client/"),
    connection = "scm:git:git@github.com:adenza/xmlrpc-scala-client.git",
    devConnection = "scm:git:ssh://git@github.com:adenza/xmlrpc-scala-client.git"
  )
)
homepage := scmInfo.value map (_.browseUrl)
developers += Developer(
  id = "chameleon82",
  name = "Aleksandr Nekrasov",
  email = "@chameleon82",
  url = url("https://github.com/chameleon82")
)

licenses += ("The Apache Software License, Version 2.0", url("https://www.apache.org/licenses/LICENSE-2.0.txt"))

publishTo := Some(if (version.value.endsWith("SNAPSHOT"))
  "Sonatype Snapshots Nexus" at "https://oss.sonatype.org/content/repositories/snapshots"
else
  "Sonatype Releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
credentials += Credentials(Path.userHome / ".sbt" / ".credentials")

scalaVersion := "2.12.11"

crossScalaVersions := Seq(scalaVersion.value, "2.13.1")
scalacOptions ++= Seq("-deprecation", "-feature")

lazy val xmlRpcScalaClient = Project(id = "xmlrpc-scala-client", base = file("."))
  .enablePlugins(SbtPgp)

libraryDependencies ++= Seq(
  "org.apache.xmlrpc" % "xmlrpc-client" % "3.1.3",
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.scalatest" %% "scalatest" % "3.1.1" % Test,
  "org.mockito" %% "mockito-scala-scalatest" % "1.13.10" % Test
)

coverageEnabled in Test := true

