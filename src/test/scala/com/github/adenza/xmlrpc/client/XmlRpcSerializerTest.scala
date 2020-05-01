package com.github.adenza.xmlrpc.client

import com.github.adenza.xmlrpc.exceptions.XmlRpcDeserializationException
import java.time.{LocalDate, LocalDateTime, OffsetDateTime, ZoneId}
import java.util

import com.github.adenza.xmlrpc.client.models._
import org.scalatest.funsuite.AnyFunSuite

class XmlRpcSerializerTest extends AnyFunSuite {

  test("complex object params") {
    val expectedModel = Array[Object](
      "USD",
      1000.asInstanceOf[Integer],
      java.util.Date
        .from(LocalDateTime.parse("2020-02-02T00:00:00").atOffset(OffsetDateTime.now().getOffset).toInstant),
      java.util.Date
        .from(LocalDateTime.parse("2020-02-02T01:02:03").atOffset(OffsetDateTime.now().getOffset).toInstant),
      java.util.Date.from(OffsetDateTime.parse("2020-02-01T17:02:03Z").toInstant)
    )

    val params = XmlRpcSerializer.toParams(
      InputParamsModel(
        currency = "USD",
        amount = 1000,
        someDate = LocalDate.parse("2020-02-02"),
        someDateTime = LocalDateTime.parse("2020-02-02T01:02:03"),
        someDateTimeOffset = OffsetDateTime.parse("2020-02-02T01:02:03+08:00")
      )
    )

    assert(params.toList == expectedModel.toList)
  }
  test("complex object in response") {

    val mainObj = new util.HashMap[String, Any]()
    mainObj.put("currency", "USD")
    mainObj.put(
      "someDate",
      java.util.Date.from(LocalDate.parse("2020-02-02").atStartOfDay(ZoneId.systemDefault()).toInstant)
    )
    mainObj.put(
      "someDateTime",
      java.util.Date.from(LocalDateTime.parse("2020-02-02T01:02:03").atZone(ZoneId.systemDefault()).toInstant)
    )
    mainObj.put(
      "someDateTimeOffset",
      java.util.Date.from(OffsetDateTime.parse("2020-02-02T01:02:03+08:00").toInstant)
    )
    val internalObj = new util.HashMap[String, Any]()
    internalObj.put("name", "John")
    internalObj.put("surname", "Connor")
    mainObj.put("nestedObject", internalObj)

    val optionalInternalObj = new util.HashMap[String, Any]()
    optionalInternalObj.put("name", "Sara")
    optionalInternalObj.put("surname", "Connor")
    mainObj.put("optionalNestedObject", optionalInternalObj)

    val listObj1 = new util.HashMap[String, Any]()
    listObj1.put("name", "terminator")
    listObj1.put("surname", "T-1000")

    val listObj2 = new util.HashMap[String, Any]()
    listObj2.put("name", "terminator")
    listObj2.put("surname", "T-800")

    val internalArray = Array[Object](listObj1, listObj2)

    mainObj.put("seqProperty", internalArray)
    mainObj.put("listProperty", internalArray)

    val emptyArray = Array[Object](listObj1, listObj2)
    mainObj.put("emptyArray", emptyArray)

    val result = XmlRpcSerializer.fromResponse[SerializerModel](mainObj)

    assert(
      result == SerializerModel(
        currency = "USD",
        someDate = LocalDate.parse("2020-02-02"),
        someDateTime = LocalDateTime.parse("2020-02-02T01:02:03"),
        someDateTimeOffset =
          OffsetDateTime.parse("2020-02-02T01:02:03+08:00").toInstant.atOffset(OffsetDateTime.now().getOffset),
        nestedObject = SerializerNestedModel(name = "John", surname = "Connor"),
        optionalNestedObject = Some(SerializerNestedModel(name = "Sara", surname = "Connor")),
        seqProperty = Seq(SerializerNestedModel("terminator", "T-1000"), SerializerNestedModel("terminator", "T-800")),
        listProperty =
          List(SerializerNestedModel("terminator", "T-1000"), SerializerNestedModel("terminator", "T-800")),
        emptyList = List()
      )
    )

  }

  test("Deserialization Exception") {
    val mainObj = new util.HashMap[String, Any]()
    mainObj.put("currency", "USD")

    val thrown = intercept[XmlRpcDeserializationException] {
      XmlRpcSerializer.fromResponse[SerializerModel](mainObj)
    }
    assert(thrown.getMessage == "Required parameter constructor SerializerModel.someDate is missing in the response")
  }

  test("Casting Exception") {
    val mainObj = new util.HashMap[String, Any]()
    mainObj.put("currency", 123)

    val thrown = intercept[XmlRpcDeserializationException] {
      XmlRpcSerializer.fromResponse[SerializerModel](mainObj)
    }
    assert(thrown.getMessage == "Casting error [class SerializerModel.currency]: Cannot cast java.lang.Integer to java.lang.String")
  }
}
