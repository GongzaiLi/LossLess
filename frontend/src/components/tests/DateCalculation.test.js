const dateCalculation = require('../model/DateCalculation');

const months = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];

test('Check Current Time', () => {
  const date = new Date();
  expect(dateCalculation.memberSince(date))
    .toBe(`Member since: ${date.getDate()} ${months[date.getMonth()]} ${date.getFullYear()} (0 Months) `);
})

test('Check almost-a-month', () => {
  const date = new Date();
  console.log(date);
  date.setDate(date.getDay()-26);
  console.log(date);
  expect(dateCalculation.memberSince(date))
    .toBe(`Member since: ${date.getDate()} ${months[date.getMonth()]} ${date.getFullYear()} (0 Months) `);
})

test('Check one-month', () => {
  const date = new Date();
  date.setMonth(date.getMonth()-1);

  expect(dateCalculation.memberSince(date))
    .toBe(`Member since: ${date.getDate()} ${months[date.getMonth()]} ${date.getFullYear()} (1 Month) `);
})

test('Check almost-a-year', () => {
  const date = new Date();
  date.setMonth(date.getMonth()-11);

  expect(dateCalculation.memberSince(date))
    .toBe(`Member since: ${date.getDate()} ${months[date.getMonth()]} ${date.getFullYear()} (11 Months) `);
})


test('Check 1-year-difference', () => {
  const date = new Date();
  date.setMonth(date.getMonth()-12);

  expect(dateCalculation.memberSince(date))
    .toBe(`Member since: ${date.getDate()} ${months[date.getMonth()]} ${date.getFullYear()} (1 Year) `);
})

test('Check 5-years-5-months', () => {
  const date = new Date();
  date.setMonth(date.getMonth()-5);
  date.setFullYear(date.getFullYear()-5);

  expect(dateCalculation.memberSince(date))
    .toBe(`Member since: ${date.getDate()} ${months[date.getMonth()]} ${date.getFullYear()} (5 Years and 5 Months) `);
})


test('Check 10-years-11-months', () => {
  const date = new Date();
  date.setMonth(date.getMonth()-11);
  date.setFullYear(date.getFullYear()-10);

  expect(dateCalculation.memberSince(date))
    .toBe(`Member since: ${date.getDate()} ${months[date.getMonth()]} ${date.getFullYear()} (10 Years and 11 Months) `);
})