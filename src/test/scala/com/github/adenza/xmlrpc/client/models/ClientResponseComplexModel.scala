package com.github.adenza.xmlrpc.client.models

import java.time.{LocalDateTime, OffsetDateTime}
import java.util.UUID

case class ClientResponseComplexModel(
  intValue: Int,
  booleanValue: Boolean,
  stringValue: String,
  doubleValue: Double,
  dateValue: LocalDateTime,
  byteValue: Array[Byte],
  structType: ClientResponseNestedModel,
  optionStructType: Option[ClientResponseNestedModel],
  seqStringValue: Seq[String],
  listStringValue: Seq[String],
  listInListValue: Seq[Seq[String]],
  exNilValue: Option[String],
  exByteValue: Byte,
  exFloatValue: Float,
  exLongValue: Long,
  //  exDom: org.w3c.dom.Node,
  exShortValue: Short,
  //exSerializableValue: Serializable
  exBigDecimalValue: BigDecimal,
  exBigIntegerValue: BigInt,
  exDateTimeValue: OffsetDateTime,
  uuidValue: UUID
)
