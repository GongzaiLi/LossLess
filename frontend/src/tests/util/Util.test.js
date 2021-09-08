import {getMonthsAndYearsBetween, formatDate, getMouthName} from '../../util';

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

describe('test the getMouthName method', () => {
  test('number month 0', () => {
    expect(getMouthName(0)).toEqual("January");
  });

  test('number month 1', () => {
    expect(getMouthName(1)).toEqual("February");
  });

  test('number month 2', () => {
    expect(getMouthName(2)).toEqual("March");
  });

  test('number month 3', () => {
    expect(getMouthName(3)).toEqual("April");
  });

  test('number month 4', () => {
    expect(getMouthName(4)).toEqual("May");
  });

  test('number month 5', () => {
    expect(getMouthName(5)).toEqual("June");
  });

  test('number month 6', () => {
    expect(getMouthName(6)).toEqual("July");
  });

  test('number month 7', () => {
    expect(getMouthName(7)).toEqual("August");
  });

  test('number month 8', () => {
    expect(getMouthName(8)).toEqual("September");
  });

  test('number month 9', () => {
    expect(getMouthName(9)).toEqual("October");
  });

  test('number month 10', () => {
    expect(getMouthName(10)).toEqual("November");
  });

  test('number month 11', () => {
    expect(getMouthName(11)).toEqual("December");
  });

  test('number month 12', () => {
    expect(getMouthName(12)).toEqual(undefined);
  });

});