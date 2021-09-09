import Api from "../Api";

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
 * Updates a notification if it has been read.
 * @param notification The notification to be updated.
 * @param notificationUpdate The updated data of the notification to be sent in the api request.
 */
export async function markNotificationRead(notification, notificationUpdate) {
  notification.read = true;
  notificationUpdate.read = true;
  let response;
  await Api.readNotification(notification.id, notificationUpdate)
      .then((res) => {
        response = res.data;
      })
      .catch((error) => {
        response = error;
      })
  return response;
}


export default getMonthsAndYearsBetween;