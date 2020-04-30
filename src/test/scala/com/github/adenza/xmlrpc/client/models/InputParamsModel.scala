package com.github.adenza.xmlrpc.client.models

import java.time.{LocalDate, LocalDateTime, OffsetDateTime}

case class InputParamsModel(currency: String,
                            amount: Int,
                            someDate: LocalDate,
                            someDateTime: LocalDateTime,
                            someDateTimeOffset: OffsetDateTime)
