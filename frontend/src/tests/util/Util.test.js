import {getMonthsAndYearsBetween, formatDate} from '../../util';

test('same-start-end', () => {
  expect(getMonthsAndYearsBetween(
    new Date("2021-03-11T00:32:00Z"),
    new Date("2021-03-11T00:32:00Z")
  )).toEqual({years: 0, months: 0});
});

test('almost-a-month', () => {
  expect(getMonthsAndYearsBetween(
    new Date("2021-03-10T00:32:00Z"),
    new Date("2021-04-09T00:32:01Z")
  )).toEqual({years: 0, months: 0});
});

test('one-month', () => {
  expect(getMonthsAndYearsBetween(
    new Date("2021-05-10T00:32:00Z"),
    new Date("2021-06-10T00:32:01Z")
  )).toEqual({years: 0, months: 1});
});

test('almost-a-year', () => {
  expect(getMonthsAndYearsBetween(
    new Date("2020-01-01T00:00:00Z"),
    new Date("2020-12-30T23:59:58Z")
  )).toEqual({years: 0, months: 11});
});

test('1-year-difference', () => {
  expect(getMonthsAndYearsBetween(
    new Date("2020-05-10T00:32:00Z"),
    new Date("2021-05-10T00:32:01Z")
  )).toEqual({years: 1, months: 0});
});

test('5-years-3-months', () => {
  expect(getMonthsAndYearsBetween(
    new Date("2016-05-10T00:32:00Z"),
    new Date("2021-10-11T00:32:01Z")
  )).toEqual({years: 5, months: 5});
});

describe('test the formatDate method', () => {
  test('"2016-05-10T00:32:00Z" date format to 2016-05-10', () => {
    expect(formatDate(new Date("2016-05-10T00:32:00Z"))).toEqual("2016-05-10")
  });

  test('"2016-12-01T00:32:00Z" date format to YYYY-MM-DD', () => {
    expect(formatDate(new Date("2016-12-01T00:32:00Z"))).toEqual("2016-12-01")
  });
});