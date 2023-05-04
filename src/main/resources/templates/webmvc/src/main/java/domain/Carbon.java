package {{packageName}}.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class Carbon implements Comparable<Carbon>, Serializable {

  public static Pattern PATTERN = Pattern.compile("^(?<date>\\d{4}-\\d{2}-\\d{2})"
                                                  + "T"
                                                  + "(?<time>\\d{2}:\\d{2}:\\d{2})"
                                                  + "(?:\\.[+\\d]+)?"
                                                  + "(?<timezone>Z|\\+[\\d:]+)$", Pattern.CASE_INSENSITIVE);

  public static Carbon EPOCH = Carbon.parse("1970-01-01T00:00:00Z");

  private static final long serialVersionUID = 1L;

  private final ZonedDateTime base;

  private Carbon(ZonedDateTime base) {
    this.base = base;
  }

  /**
   * Create a carbon instance from a string.
   *
   * @param timeString e.g. 2007-12-03T10:15:30+01:00; The string must represent a valid date-time and is parsed using {@link DateTimeFormatter#ISO_ZONED_DATE_TIME}.
   * @return
   */
  public static Carbon parse(CharSequence timeString) {
    return new Carbon(ZonedDateTime.parse(timeString));
  }

  /**
   * Create a carbon instance with {@link ZoneId} of "Asia/Seoul".
   */
  public static Carbon seoul() {
    return new Carbon(ZonedDateTime.now(ZoneId.of("Asia/Seoul")));
  }

  /**
   * Create a carbon instance with {@link ZoneId} of "UTC".
   */
  public static Carbon utc() {
    return new Carbon(ZonedDateTime.now(ZoneId.of("UTC")));
  }

  /**
   * Create a carbon instance with the given {@link ZoneId}.
   */
  public static Carbon now(ZoneId zoneId) {
    return new Carbon(ZonedDateTime.now(zoneId));
  }

  /**
   * Create a carbon instance from the given epoch second.
   */
  public static Carbon from(long epochSecond, ZoneId zoneId) {
    return Carbon.from(Instant.ofEpochSecond(epochSecond), zoneId);
  }

  /**
   * Create a carbon instance from the given {@link Instant} and {@link ZoneId}.
   */
  public static Carbon from(Instant instant, ZoneId zoneId) {
    return new Carbon(ZonedDateTime.ofInstant(instant, zoneId));
  }

  /**
   * Create a carbon instance from the given {@link OffsetDateTime} and {@link ZoneId}.
   */
  public static Carbon from(OffsetDateTime offsetDateTime, ZoneId zoneId) {
    return new Carbon(offsetDateTime.atZoneSameInstant(zoneId));
  }

  /**
   * Create a carbon instance from the given {@link ZonedDateTime}.
   */
  public static Carbon from(ZonedDateTime zonedDateTime) {
    return new Carbon(zonedDateTime);
  }

  /**
   * Create a carbon instance from the given {@link LocalDateTime}.
   */
  public static Carbon from(LocalDateTime localDateTime, ZoneId zoneId) {
    return from(localDateTime.atZone(zoneId));
  }

  /**
   * Create a carbon instance with start of day of the given {@link LocalDate} and {@link ZoneId}.
   */
  public static Carbon from(LocalDate localDate, ZoneId zoneId) {
    return from(localDate.atStartOfDay().atZone(zoneId));
  }

  /**
   * Gets the year field. e.g. 2023
   */
  public int getYear() {
    return base.getYear();
  }

  /**
   * Gets the month-of-year field from 1 to 12.
   */
  public int getMonth() {
    return base.getMonthValue();
  }

  /**
   * Gets the day-of-month field, from 1 to 31.
   */
  public int getDay() {
    return base.getDayOfMonth();
  }

  /**
   * Gets the hour-of-day field, from 0 to 23.
   */
  public int getHour() {
    return base.getHour();
  }

  /**
   * Gets the minute-of-hour field, from 0 to 59.
   */
  public int getMinute() {
    return base.getMinute();
  }

  /**
   * Gets the second-of-minute field, from 0 to 59.
   */
  public int getSecond() {
    return base.getSecond();
  }

  /**
   * Gets the nano-of-second field, from 0 to 999,999,999.
   */
  public int getNano() {
    return base.getNano();
  }

  /**
   * Gets the time-zone, such as "Asia/Seoul".
   */
  public ZoneId getZone() {
    return base.getZone();
  }

  /**
   * Gets the number of milliseconds from the epoch of 1970-01-01T00:00:00Z.
   */
  public long getTimestamp() {
    return base.toInstant().toEpochMilli();
  }

  /**
   * Returns a copy of this date-time with the specified amount added.
   * e.g. add(Duration.ofDays(7L));
   */
  public Carbon add(Duration duration) {
    return new Carbon(base.plus(duration));
  }

  /**
   * Returns a copy of this date-time with the specified amount subtracted.
   * e.g. sub(Duration.ofDays(7L));
   */
  public Carbon sub(Duration duration) {
    return new Carbon(base.minus(duration));
  }

  /**
   * Returns a copy of this Carbon instance with the specified number of years added.
   * e.g. addYears(1L);
   */
  public Carbon addYears(long years) {
    return new Carbon(base.plusYears(years));
  }

  /**
   * Returns a copy of this Carbon instance with the specified number of years subtracted.
   * e.g. subYears(1L);
   */
  public Carbon subYears(long years) {
    return new Carbon(base.minusYears(years));
  }

  /**
   * Returns a copy of this Carbon instance with the specified number of months added.
   * e.g. addMonths(1L);
   */
  public Carbon addMonths(long months) {
    return new Carbon(base.plusMonths(months));
  }

  /**
   * Returns a copy of this Carbon instance with the specified number of months subtracted.
   * e.g. subMonths(1L);
   */
  public Carbon subMonths(long months) {
    return new Carbon(base.minusMonths(months));
  }

  /**
   * Returns a copy of this Carbon instance with the specified number of days added.
   * e.g. addDays(7L);
   */
  public Carbon addDays(long days) {
    return new Carbon(base.plusDays(days));
  }

  /**
   * Returns a copy of this Carbon instance with the specified number of days subtracted.
   * e.g. subDays(7L);
   */
  public Carbon subDays(long days) {
    return new Carbon(base.minusDays(days));
  }

  /**
   * Returns a copy of this Carbon instance with the specified number of hours added.
   * e.g. addHours(6L);
   */
  public Carbon addHours(long hours) {
    return new Carbon(base.plusHours(hours));
  }

  /**
   * Returns a copy of this Carbon instance with the specified number of hours subtracted.
   * e.g. subHours(6L);
   */
  public Carbon subHours(long hours) {
    return new Carbon(base.minusHours(hours));
  }

  /**
   * Returns a copy of this Carbon instance with the specified number of minutes added.
   * e.g. addMinutes(1_000L);
   */
  public Carbon addMinutes(long minutes) {
    return new Carbon(base.plusMinutes(minutes));
  }

  /**
   * Returns a copy of this Carbon instance with the specified number of minutes subtracted.
   * e.g. subMinutes(1_000L);
   */
  public Carbon subMinutes(long minutes) {
    return new Carbon(base.minusMinutes(minutes));
  }

  /**
   * Returns a copy of this Carbon instance with the specified number of seconds added.
   * e.g. addSeconds(486L);
   */
  public Carbon addSeconds(long seconds) {
    return new Carbon(base.plusSeconds(seconds));
  }

  /**
   * Returns a copy of this Carbon instance with the specified number of seconds subtracted.
   * e.g. subSeconds(486L);
   */
  public Carbon subSeconds(long seconds) {
    return new Carbon(base.minusSeconds(seconds));
  }

  /**
   * Returns a copy of this Carbon instance with January 1st 00:00:00.000000000.
   */
  public Carbon startOfYear() {
    return new Carbon(base.withMonth(1).withDayOfMonth(1)).startOfDay();
  }

  /**
   * Returns a copy of this Carbon instance with NEXT year's January 1st at 00:00:00.000000000.
   */
  public Carbon endOfYear() {
    return new Carbon(base.withMonth(12).withDayOfMonth(31)).endOfDay();
  }

  /**
   * Returns a copy of this Carbon instance with 1st day at 00:00:00.000000000 of the month.
   */
  public Carbon startOfMonth() {
    return new Carbon(base.withDayOfMonth(1)).startOfDay();
  }

  /**
   * Returns a copy of this Carbon instance with NEXT month's 1st day at 00:00:00.000000000.
   */
  public Carbon endOfMonth() {
    return new Carbon(base.with(TemporalAdjusters.lastDayOfMonth())).endOfDay();
  }

  /**
   * Returns a copy of this Carbon instance with 00:00:00.000000000 of the day.
   */
  public Carbon startOfDay() {
    return new Carbon(base.withHour(0).withMinute(0).withSecond(0).withNano(0));
  }

  /**
   * Returns a copy of this Carbon instance with TOMORROW 00:00:00.000000000.
   */
  public Carbon endOfDay() {
    // Some collaborating system may not consume nano sec having more than 6 digits
    // So, for inter-operability do not use nano sec
    return new Carbon(base.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
  }

  /**
   * Converts this date-time to an {@link Instant}.
   */
  public Instant toInstant() {
    return base.toInstant();
  }

  /**
   * Converts this date-time to an {@link OffsetDateTime}.
   */
  public OffsetDateTime toOffsetDateTime() {
    return base.toOffsetDateTime();
  }

  /**
   * Converts this date-time to an {@link ZonedDateTime}.
   */
  public ZonedDateTime toZonedDateTime() {
    return ZonedDateTime.from(base);
  }

  /**
   * Converts this date-time to an {@link LocalDateTime}.
   */
  public LocalDateTime toLocalDateTime() {
    return base.toLocalDateTime();
  }

  /**
   * Converts this date-time to an {@link LocalDate}.
   */
  public LocalDate toLocalDate() {
    return base.toLocalDate();
  }

  /**
   * Converts this date-time to an {@link Timestamp}.
   */
  public Timestamp toSqlTimestamp() {
    return Timestamp.from(base.toInstant());
  }

  /**
   * Format this date-time to ISO8601-compliant string.
   */
  public String toIso8601String() {
    return format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
  }

  /**
   * Format this date-time by the given {@link DateTimeFormatter} style.
   */
  public String format(DateTimeFormatter format) {
    return base.format(format);
  }

  /**
   * Check if this instance is past or not.
   */
  public boolean isPast() {
    Carbon now = new Carbon(ZonedDateTime.now(base.getZone()));

    return isBefore(now);
  }

  /**
   * Check if this instance is future or not.
   */
  public boolean isFuture() {
    Carbon now = new Carbon(ZonedDateTime.now(base.getZone()));

    return isAfter(now);
  }

  /**
   * Check if the given {@link Carbon} instance is same as this instance.
   */
  public boolean eq(Carbon that) {
    return equals(that);
  }

  /**
   * Check if the given {@link Carbon} instance is not same as this instance.
   */
  public boolean ne(Carbon that) {
    return !equals(that);
  }

  /**
   * Check if this instance is future time than the given instance.
   * alias method for {@link Carbon#gt(Carbon)}
   */
  public boolean isAfter(Carbon that) {
    return gt(that);
  }

  /**
   * Check if this instance is future time than the given instance.
   */
  public boolean gt(Carbon that) {
    return compareTo(that) > 0;
  }

  /**
   * Check if this instance is same as the given instance OR future time than the given instance.
   */
  public boolean gte(Carbon that) {
    return equals(that) || gt(that);
  }

  /**
   * Check if this instance is past time than the given instance.
   * alias method for {@link Carbon#lt(Carbon)}
   */
  public boolean isBefore(Carbon that) {
    return lt(that);
  }

  /**
   * Check if this instance is past time than the given instance.
   */
  public boolean lt(Carbon that) {
    return compareTo(that) < 0;
  }

  /**
   * Check if this instance is same as the given instance OR past time than the given instance.
   */
  public boolean lte(Carbon that) {
    return equals(that) || lt(that);
  }

  /**
   * Find whether this is in between a and b
   *
   * @param a inclusive
   * @param b exclusive
   * @return
   */
  public boolean isBetween(Carbon a, Carbon b) {
    final List<Carbon> args = new ArrayList<>(Arrays.asList(a, b));
    Collections.sort(args);

    return gte(args.get(0)) && lt(args.get(1));
  }

  /**
   * Obtains a {@link Duration} representing the duration between this and given.
   */
  public Duration duration(Carbon that) {
    return Duration.between(base, that.base);
  }

  @Override
  public String toString() {
    return toIso8601String();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Carbon)) return false;
    Carbon that = (Carbon) o;
    return compareTo(that) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(base);
  }

  @Override
  public int compareTo(Carbon other) {
    if (this == other) return 0;
    if (this.base.toEpochSecond() == other.base.toEpochSecond()) {
      if (this.base.getNano() == other.base.getNano()) {
        return 0;
      }
      return ((this.base.getNano() - other.base.getNano()) < 0) ? -1 : 1;
    }
    return ((this.base.toEpochSecond() - other.base.toEpochSecond()) < 0) ? -1 : 1;
  }
}
