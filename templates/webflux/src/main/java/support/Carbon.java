package {{packageName}}.support;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class Carbon implements Comparable<Carbon> {

  private final ZonedDateTime base;

  private Carbon(ZonedDateTime base) {
    this.base = base;
  }

  public static Carbon parse(CharSequence timeString) {
    return new Carbon(ZonedDateTime.parse(timeString));
  }

  public static Carbon seoul() {
    return new Carbon(ZonedDateTime.now(ZoneId.of("Asia/Seoul")));
  }

  public static Carbon utc() {
    return new Carbon(ZonedDateTime.now(ZoneId.of("UTC")));
  }

  public static Carbon now(ZoneId zoneId) {
    return new Carbon(ZonedDateTime.now(zoneId));
  }

  public static Carbon from(Instant instant, ZoneId zoneId) {
    return new Carbon(ZonedDateTime.ofInstant(instant, zoneId));
  }

  public static Carbon from(OffsetDateTime offsetDateTime, ZoneId zoneId) {
    return new Carbon(offsetDateTime.atZoneSameInstant(zoneId));
  }

  public int getYear() {
    return base.getYear();
  }

  public int getMonth() {
    return base.getMonthValue();
  }

  public int getDay() {
    return base.getDayOfMonth();
  }

  public int getHour() {
    return base.getHour();
  }

  public int getMinute() {
    return base.getMinute();
  }

  public int getSecond() {
    return base.getSecond();
  }

  public int getNano() {
    return base.getNano();
  }

  public ZoneId getZone() {
    return base.getZone();
  }

  public long getTimestamp() {
    return base.toInstant().toEpochMilli();
  }

  public Carbon add(Duration duration) {
    return new Carbon(base.plus(duration));
  }

  public Carbon sub(Duration duration) {
    return new Carbon(base.minus(duration));
  }

  public Carbon addYears(long years) {
    return new Carbon(base.plusYears(years));
  }

  public Carbon subYears(long years) {
    return new Carbon(base.minusYears(years));
  }

  public Carbon addMonths(long months) {
    return new Carbon(base.plusMonths(months));
  }

  public Carbon subMonths(long months) {
    return new Carbon(base.minusMonths(months));
  }

  public Carbon addDays(long days) {
    return new Carbon(base.plusDays(days));
  }

  public Carbon subDays(long days) {
    return new Carbon(base.minusDays(days));
  }

  public Carbon addHours(long hours) {
    return new Carbon(base.plusHours(hours));
  }

  public Carbon subHours(long hours) {
    return new Carbon(base.minusHours(hours));
  }

  public Carbon addMinutes(long minutes) {
    return new Carbon(base.plusMinutes(minutes));
  }

  public Carbon subMinutes(long minutes) {
    return new Carbon(base.minusMinutes(minutes));
  }

  public Carbon addSeconds(long seconds) {
    return new Carbon(base.plusSeconds(seconds));
  }

  public Carbon subSeconds(long seconds) {
    return new Carbon(base.minusSeconds(seconds));
  }

  public Carbon startOfYear() {
    return new Carbon(base.withMonth(1).withDayOfMonth(1)).startOfDay();
  }

  public Carbon endOfYear() {
    return new Carbon(base.withMonth(12).withDayOfMonth(31)).endOfDay();
  }

  public Carbon startOfMonth() {
    return new Carbon(base.withDayOfMonth(1)).startOfDay();
  }

  public Carbon endOfMonth() {
    return new Carbon(base.with(TemporalAdjusters.lastDayOfMonth())).endOfDay();
  }

  public Carbon startOfDay() {
    return new Carbon(base.withHour(0).withMinute(0).withSecond(0).withNano(0));
  }

  public Carbon endOfDay() {
    // Some collaborating system may not consume nano sec having more than 6 digits
    // So, for inter-operability do not use nano sec
    return new Carbon(base.withHour(23).withMinute(59).withSecond(59).withNano(0));
  }

  public Instant toInstant() {
    return base.toInstant();
  }

  public OffsetDateTime toOffsetDateTime() {
    return base.toOffsetDateTime();
  }

  public ZonedDateTime toZonedDateTime() {
    return ZonedDateTime.from(base);
  }

  public String toIso8601String() {
    return format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
  }

  public String format(DateTimeFormatter format) {
    return base.format(format);
  }

  public boolean isPast() {
    Carbon now = new Carbon(ZonedDateTime.now(base.getZone()));

    return isBefore(now);
  }

  public boolean isFuture() {
    Carbon now = new Carbon(ZonedDateTime.now(base.getZone()));

    return isAfter(now);
  }

  public boolean eq(Carbon that) {
    return equals(that);
  }

  public boolean ne(Carbon that) {
    return !equals(that);
  }

  public boolean isAfter(Carbon that) {
    return gt(that);
  }

  public boolean gt(Carbon that) {
    return compareTo(that) > 0;
  }

  public boolean gte(Carbon that) {
    return equals(that) || gt(that);
  }

  public boolean isBefore(Carbon that) {
    return lt(that);
  }

  public boolean lt(Carbon that) {
    return compareTo(that) < 0;
  }

  public boolean lte(Carbon that) {
    return equals(that) || lt(that);
  }

  public boolean isBetween(Carbon a, Carbon b) {
    final ArrayList<Carbon> args = new ArrayList<>(Arrays.asList(a, b));
    Collections.sort(args);

    return gt(args.get(0)) && lt(args.get(1));
  }

  public Duration duration(Carbon that) {
    return Duration.between(base, that.base);
  }

  @Override
  public String toString() {
    return toIso8601String();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Carbon)) return false;
    Carbon that = (Carbon) o;
    if (this.base.toEpochSecond() != that.base.toEpochSecond()) return false;
    if (this.base.getNano() != that.base.getNano()) return false;
    return true;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this);
  }

  @Override
  public int compareTo(Carbon o) {
    return base.compareTo(o.base);
  }
}
