/**
 * Takes a starting and ending date, then calculates the integer number of years and months elapsed since that date.
 * The months elapsed is not the total number of months elapsed, but the number months elapsed in
 * addition to the years also elapsed. For example, 1 year and 2 months instead of 1 year, 14 months.
 * Assumes that a year is 365 days, and every month is exactly 1/12 of a year.
 * Returns data in the format {months: months_elapsed, years: years_elapsed}
 */
export function getMonthsAndYearsBetween(start, end) {
  const timeElapsed = end - start;
  const daysElapsed = Math.floor(timeElapsed / (1000 * 60 * 60 * 24));
  const yearsElapsed = Math.floor(daysElapsed / 365);
  const monthsElapsed = Math.floor(((daysElapsed / 365) - yearsElapsed) * 12);
  return {
    months: monthsElapsed,
    years: yearsElapsed
  }
}

/**
 * Get today's date without the time
 * need to add one to get correct date
 * @return today's date in format yyyy-mm-dd
 **/
export function getToday() {
  let date = new Date();
  return date.getFullYear() + "-" + (date.getMonth() + 1).toString().padStart(2, '0') + '-' + date.getDate().toString().padStart(2, '0');
}

/**
 * Get any date without the time
 * need to add one to get correct date
 * @return date in format yyyy-mm-dd
 **/
export function formatDate(date) {
  return date.getFullYear() + "-" + (date.getMonth() + 1).toString().padStart(2, '0') + '-' + date.getDate().toString().padStart(2, '0');
}

/**
 * get a number of mouth the translate to the mouth name
 * @param mouthNum
 * @returns string
 */
export function getMonthName(mouthNum) {
  const monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
  return monthNames[mouthNum];
}

/**
 * Builds a string containing the address. Takes a privacy level allowing only certain fields to be returned depending on its value.
 *
 * Privacy Levels:
 * 1 and below - All fields returned
 * 2 - Suburb, city, region, country, postcode are returned
 * 3 - City, region, country are returned
 * 4 - Region and city are returned
 * 5 - Country is returned
 * 6 or above - Message "Withheld for privacy" returned
 *
 * If address ends up empty, message "No address available" returned;
 *
 * @param address       Address object containing fields {streetNumber, streetName, suburb, city, region, country, postcode}.
 *                      All fields theoretically optional
 * @param privacyLevel  The level of privacy to return the address with
 * @returns {string}
 */
export function formatAddress(address, privacyLevel) {

  if (privacyLevel > 5) return "Withheld for privacy";

  let addressString = "";

  (address.streetNumber != null && privacyLevel <= 1 && address.streetName != null) && (addressString += `${address.streetNumber} `);
  (address.streetName != null && privacyLevel <= 1) && (addressString += `${address.streetName}, `);
  (address.suburb && privacyLevel <= 2) && (addressString += address.city || address.country || address.region ? `${address.suburb}, ` : `${address.suburb} ` );
  (address.city != null && privacyLevel <= 3) && (addressString += `${address.city}, `);
  (address.region != null && privacyLevel <= 4) && (addressString += address.country != null ? `${address.region}, ` : `${address.region} `);
  (address.country != null && privacyLevel <= 5) && (addressString += `${address.country} `);
  (address.postcode != null && privacyLevel <= 2) && (addressString += `${address.postcode}`);

  if (addressString === "") {
    addressString = "No address available"
  }

  return addressString.trimEnd();
}
