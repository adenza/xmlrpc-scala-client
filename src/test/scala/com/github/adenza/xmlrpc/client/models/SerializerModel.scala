package com.github.adenza.xmlrpc.client.models

import java.time.{LocalDate, LocalDateTime, OffsetDateTime}

case class SerializerModel(currency: String,
                           someDate: LocalDate,
                           someDateTime: LocalDateTime,
                           someDateTimeOffset: OffsetDateTime,
                           nestedObject: SerializerNestedModel,
                           optionalNestedObject: Option[SerializerNestedModel],
                           seqProperty: Seq[SerializerNestedModel],
                           listProperty: List[SerializerNestedModel],
                           emptyList: List[SerializerNestedModel])
