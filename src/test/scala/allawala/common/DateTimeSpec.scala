package allawala.common

import java.time.Instant

import allawala.chassis.util.DateTimeProvider

trait DateTimeSpec extends EvenMoreSugar {
  val dateTimeProvider: DateTimeProvider = mock[DateTimeProvider]
  dateTimeProvider.now returns Instant.ofEpochMilli(0)

  val now: Instant = dateTimeProvider.now

}
