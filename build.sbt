organization := "com.github.adenza"
name := "xmlrpc-scala-client"
description := "XML-RPC Scala client"
startYear := Some(2020)
homepage := scmInfo.value map (_.browseUrl)
scmInfo := Some(
  ScmInfo(
    url("https://github.com/adenza/xmlrpc-scala-client/"),
    "scm:git:git@github.com:adenza/xmlrpc-scala-client.git"
  )
)

scalaVersion := "2.12.11"

crossScalaVersions := Seq(scalaVersion.value, "2.13.1")

lazy val xmlRpcScalaClient = Project(id = "vcard-api", base = file("."))

libraryDependencies ++= Seq(
  "org.apache.xmlrpc" % "xmlrpc-client" % "3.1.3",
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.scalatest" %% "scalatest" % "3.1.1" % Test,
  "org.mockito" %% "mockito-scala-scalatest" % "1.13.10" % Test
)

coverageEnabled in Test := true
