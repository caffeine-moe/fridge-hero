package moe.caffeine.fridgehero.domain.helper

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.until
import java.util.Calendar


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
  Clock.System.now().daysUntil(this.toInstant(), TimeZone.currentSystemDefault()) + 1

fun Long.hoursUntil(): Long =
  Clock.System.now().until(this.toInstant(), DateTimeUnit.HOUR)

fun Long.expiryImminent(): Boolean =
  (this - Clock.System.now().toEpochMilliseconds()).toDate().toEpochDays() in -3..3

fun Long.isExpired(): Boolean =
  this.toDate().toEpochDays() - Clock.System.now().toEpochMilliseconds().toDate()
    .toEpochDays() < 0

private fun hoursTillMidnight() =
  Clock.System.now().until(
    Calendar.getInstance().apply {
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      add(Calendar.DAY_OF_MONTH, 1)
    }.toInstant().toKotlinInstant(),
    DateTimeUnit.HOUR
  )

fun Long.readableDaysUntil(): String =
  if (!this.isExpired()) {
    when {
      this == -1L -> "Never."
      this.hoursUntil() in 0..hoursTillMidnight() -> "Tomorrow."
      this.daysUntil() == 1 -> "Today."
      else -> "${this.daysUntil()}d."
    }
  } else "Expired."
