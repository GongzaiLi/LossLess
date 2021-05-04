/**
 * Takes a starting and ending date, then calculates the integer number of years and months elapsed since that date.
 * The months elapsed is not the total number of months elapsed, but the number months elapsed in
 * addition to the years also elapsed. For example, 1 year and 2 months instead of 1 year, 14 months.
 * Assumes that a year is 365 days, and every month is exactly 1/12 of a year.
 * Returns data in the format {months: months_elapsed, years: years_elapsed}
 */
function getMonthsAndYearsBetween(start, end) {
  const timeElapsed = end - start;
  const daysElapsed = Math.floor(timeElapsed / (1000 * 60 * 60 * 24));
  const yearsElapsed = Math.floor(daysElapsed / 365);
  const monthsElapsed = Math.floor(((daysElapsed / 365) - yearsElapsed) * 12);
  return {
    months: monthsElapsed,
    years: yearsElapsed
  }
}

export default getMonthsAndYearsBetween;