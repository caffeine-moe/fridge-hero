package moe.caffeine.fridgehero.domain.mapper

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.until


fun Long.toInstant(): Instant = Instant.fromEpochMilliseconds(this)
fun Long.toDate(): LocalDate =
  this.toInstant().toLocalDateTime(TimeZone.currentSystemDefault()).date

fun Long.toReadableDate(): String =
  this.toDate()
    .let { localDate ->
      "${
        localDate.dayOfMonth.let { dayOfMonth ->
          if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth
        }
      }" +
              "/${
                localDate.monthNumber.let { monthNumber ->
                  if (monthNumber < 10) "0$monthNumber" else monthNumber
                }
              }" +
              "/${localDate.year}"
    }

fun Long.daysUntil(): Int =
  Clock.System.now().daysUntil(this.toInstant(), TimeZone.currentSystemDefault())

fun Long.hoursUntil(): Long =
  Clock.System.now().until(this.toInstant(), DateTimeUnit.HOUR)
