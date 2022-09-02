package {{packageName}}.support;

import static org.junit.jupiter.api.Assertions.*;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import org.junit.jupiter.api.Test;

public class CarbonTest {

  private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");
  private static final ZoneId UTC_ZONE = ZoneId.of("UTC");

  @Test
  public void parse() {
    final Carbon sut = Carbon.parse("2020-01-01T00:00:00.000+00:00");

    System.out.println(sut);
    assertEquals(2020, sut.getYear());
    assertEquals(1, sut.getMonth());
    assertEquals(1, sut.getDay());
    assertEquals(0, sut.getHour());
    assertEquals(0, sut.getMinute());
    assertEquals(0, sut.getSecond());
  }

  @Test
  public void seoul() {
    final Carbon sut = Carbon.seoul();

    System.out.println(sut);
    assertEquals(SEOUL_ZONE, sut.getZone());
  }

  @Test
  public void utc() {
    final Carbon sut = Carbon.utc();

    System.out.println(sut);
    assertEquals(UTC_ZONE, sut.getZone());
  }

  @Test
  public void nowWithZoneId() {
    final Carbon sut = Carbon.now(SEOUL_ZONE);

    System.out.println(sut);
    assertEquals(SEOUL_ZONE, sut.getZone());
  }

  @Test
  public void fromInstantAndZoneId() {
    final Carbon sut = Carbon.from(Instant.now(), SEOUL_ZONE);

    System.out.println(sut);
    assertEquals(SEOUL_ZONE, sut.getZone());
  }

  @Test
  public void fromOffsetDateTimeAndZoneId() {
    final Carbon sut = Carbon.from(OffsetDateTime.now(), SEOUL_ZONE);

    System.out.println(sut);
    assertEquals(SEOUL_ZONE, sut.getZone());
  }

  @Test
  public void add() {
    final Carbon a = Carbon.now(SEOUL_ZONE);
    final Duration duration = Duration.of(1, ChronoUnit.DAYS);
    final Carbon b = a.add(duration);

    System.out.println(a);
    System.out.println(b);
  }

  @Test
  public void sub() {
    final Carbon a = Carbon.now(SEOUL_ZONE);
    final Duration duration = Duration.parse("P2DT3H4M"); // 2 days, 3 hours and 4 minutes
    final Carbon b = a.sub(duration);

    System.out.println(a);
    System.out.println(b);
  }

  @Test
  public void addYears() {
    final Carbon a = Carbon.now(SEOUL_ZONE);
    final Carbon b = a.addYears(1);

    System.out.println(a);
    System.out.println(b);
    assertEquals(ZonedDateTime.now(SEOUL_ZONE).getYear() + 1, b.getYear());
  }

  @Test
  public void subYears() {
    final Carbon a = Carbon.now(SEOUL_ZONE);
    final Carbon b = a.subYears(1);

    System.out.println(a);
    System.out.println(b);
    assertEquals(ZonedDateTime.now(SEOUL_ZONE).getYear() - 1, b.getYear());
  }

  @Test
  public void addMonths() {
    final Carbon a = Carbon.now(SEOUL_ZONE);
    final Carbon b = a.addMonths(9);

    System.out.println(a);
    System.out.println(b);
  }

  @Test
  public void subMonths() {
    final Carbon a = Carbon.now(SEOUL_ZONE);
    final Carbon b = a.subMonths(4);

    System.out.println(a);
    System.out.println(b);
  }

  @Test
  public void addDays() {
    final Carbon a = Carbon.now(SEOUL_ZONE);
    final Carbon b = a.addDays(366);

    System.out.println(a);
    System.out.println(b);
  }

  @Test
  public void subDays() {
    final Carbon a = Carbon.now(SEOUL_ZONE);
    final Carbon b = a.subDays(367);

    System.out.println(a);
    System.out.println(b);
  }

  @Test
  public void addHours() {
    final Carbon a = Carbon.now(SEOUL_ZONE);
    final Carbon b = a.addHours(25);

    System.out.println(a);
    System.out.println(b);
  }

  @Test
  public void subHours() {
    final Carbon a = Carbon.now(SEOUL_ZONE);
    final Carbon b = a.subHours(25);

    System.out.println(a);
    System.out.println(b);
  }

  @Test
  public void addMinutes() {
    final Carbon a = Carbon.now(SEOUL_ZONE);
    final Carbon b = a.addMinutes(70);

    System.out.println(a);
    System.out.println(b);
  }

  @Test
  public void subMinutes() {
    final Carbon a = Carbon.now(SEOUL_ZONE);
    final Carbon b = a.subMinutes(70);

    System.out.println(a);
    System.out.println(b);
  }

  @Test
  public void addSeconds() {
    final Carbon a = Carbon.now(SEOUL_ZONE);
    final Carbon b = a.addSeconds(70);

    System.out.println(a);
    System.out.println(b);
  }

  @Test
  public void subSeconds() {
    final Carbon a = Carbon.now(SEOUL_ZONE);
    final Carbon b = a.subSeconds(70);

    System.out.println(a);
    System.out.println(b);
  }

  @Test
  public void startOfYear() {
    final Carbon a = Carbon.parse("2020-01-01T08:59:59+09:00");
    final Carbon b = a.startOfYear();

    System.out.println(a);
    System.out.println(b);
    assertEquals(1, b.getMonth());
    assertEquals(1, b.getDay());
    assertEquals(0, b.getHour());
    assertEquals(0, b.getMinute());
    assertEquals(0, b.getSecond());
  }

  @Test
  public void endOfYear() {
    final Carbon a = Carbon.parse("2020-01-01T08:59:59+09:00");
    final Carbon b = a.endOfYear();

    System.out.println(a);
    System.out.println(b);
    assertEquals(12, b.getMonth());
    assertEquals(31, b.getDay());
    assertEquals(23, b.getHour());
    assertEquals(59, b.getMinute());
    assertEquals(59, b.getSecond());
  }

  @Test
  public void startOfMonth() {
    final Carbon a = Carbon.parse("2020-01-31T08:59:59+09:00");
    final Carbon b = a.startOfMonth();

    System.out.println(a);
    System.out.println(b);
    assertEquals(1, b.getDay());
    assertEquals(0, b.getHour());
    assertEquals(0, b.getMinute());
    assertEquals(0, b.getSecond());
  }

  @Test
  public void endOfMonth() {
    final Carbon a = Carbon.parse("2020-01-01T08:59:59+09:00");
    final Carbon b = a.endOfMonth();

    System.out.println(a);
    System.out.println(b);
    final int expected = ZonedDateTime
        .ofInstant(a.toInstant(), SEOUL_ZONE)
        .with(TemporalAdjusters.lastDayOfMonth())
        .getDayOfMonth();
    assertEquals(expected, b.getDay());
    assertEquals(23, b.getHour());
    assertEquals(59, b.getMinute());
    assertEquals(59, b.getSecond());
  }

  @Test
  public void startOfDay() {
    final Carbon a = Carbon.parse("2020-01-01T08:59:59+09:00");
    final Carbon b = a.startOfDay();

    System.out.println(a);
    System.out.println(b);
    assertEquals(0, b.getHour());
    assertEquals(0, b.getMinute());
    assertEquals(0, b.getSecond());
  }

  @Test
  public void endOfDay() {
    final Carbon a = Carbon.parse("2020-01-01T08:59:59+09:00");
    final Carbon b = a.endOfDay();

    System.out.println(a);
    System.out.println(b);
    assertEquals(23, b.getHour());
    assertEquals(59, b.getMinute());
    assertEquals(59, b.getSecond());
  }

  @Test
  public void toInstant() {
    final Carbon a = Carbon.now(SEOUL_ZONE);
    final Instant b = a.toInstant();

    System.out.println(b);
    System.out.println(b.getClass().getSimpleName());
  }

  @Test
  public void toOffsetDateTime() {
    final Carbon a = Carbon.now(SEOUL_ZONE);
    final OffsetDateTime b = a.toOffsetDateTime();

    System.out.println(b);
    System.out.println(b.getClass().getSimpleName());
  }

  @Test
  public void toZonedDateTime() {
    final Carbon a = Carbon.now(SEOUL_ZONE);
    final ZonedDateTime b = a.toZonedDateTime();

    System.out.println(b);
    System.out.println(b.getClass().getSimpleName());
  }

  @Test
  public void toIso8601String() {
    final Carbon sut = Carbon.now(SEOUL_ZONE);
    System.out.println(sut);
  }

  @Test
  public void isPast() {
    final Carbon a = Carbon.now(SEOUL_ZONE).subDays(1);
    final Carbon b = Carbon.now(SEOUL_ZONE).addDays(1);

    assertTrue(a.isPast());
    assertFalse(b.isPast());
  }

  @Test
  public void isFuture() {
    final Carbon a = Carbon.now(SEOUL_ZONE).subDays(1);
    final Carbon b = Carbon.now(SEOUL_ZONE).addDays(1);

    assertFalse(a.isFuture());
    assertTrue(b.isFuture());
  }

  @Test
  public void eq() {
    final Carbon a = Carbon.parse("2020-01-01T09:00:00.000+09:00");
    final Carbon b = Carbon.parse("2020-01-01T00:00:00.000000000+00:00");

    assertTrue(a.eq(b));
  }

  @Test
  public void ne() {
    final Carbon a = Carbon.parse("2020-01-01T00:00:00.000+09:00");
    final Carbon b = Carbon.parse("2020-01-01T00:00:00.000+00:00");

    assertTrue(a.ne(b));
  }

  @Test
  public void isAfter() {
    final Carbon a = Carbon.now(SEOUL_ZONE);
    final Carbon b = a.subDays(1);

    assertTrue(a.isAfter(b));
  }

  @Test
  public void gte() {
    final Carbon a = Carbon.parse("2020-01-01T00:00:00.000000001+09:00");
    final Carbon b = Carbon.parse("2020-01-01T00:00:00.000+09:00");

    assertTrue(a.gte(b));
  }

  @Test
  public void isBefore() {
    final Carbon a = Carbon.now(SEOUL_ZONE);
    final Carbon b = a.addDays(1);

    assertTrue(a.isBefore(b));
  }

  @Test
  public void lte() {
    final Carbon a = Carbon.parse("2020-01-01T00:00:00.999+09:00");
    final Carbon b = Carbon.parse("2020-01-01T00:00:01.000+09:00");

    assertTrue(a.lte(b));
  }

  @Test
  public void isBetween() {
    final Carbon sut = Carbon.now(SEOUL_ZONE);
    final Carbon a = Carbon.now(SEOUL_ZONE).subDays(1);
    final Carbon b = Carbon.now(SEOUL_ZONE).addDays(1);

    assertTrue(sut.isBetween(a, b));
    assertTrue(sut.isBetween(b, a));
  }

  @Test
  public void duration() {
    final Carbon a = Carbon.now(SEOUL_ZONE);
    final Carbon b = a.addDays(1).addHours(2).addMinutes(3).addSeconds(4);
    final Carbon c = a.addDays(1).subHours(2).subMinutes(3).subSeconds(4);

    final Duration durationAB = a.duration(b);
    final Duration durationAC = a.duration(c);

    System.out.println(durationAB);
    assertEquals((1 * 24 * 60 * 60) + (2 * 60 * 60) + (3 * 60) + 4, durationAB.getSeconds());

    System.out.println(durationAC);
    assertEquals((1 * 24 * 60 * 60) - (2 * 60 * 60) - (3 * 60) - 4, durationAC.getSeconds());
  }
}
