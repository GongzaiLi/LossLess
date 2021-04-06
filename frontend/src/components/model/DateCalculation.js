
exports.memberSince = function (userDate) {
  let registeredDate = new Date(userDate);//userData.created
  const timeElapsed = getMonthsAndYearsBetween(registeredDate, Date.now());
  const registeredYears = timeElapsed.years;
  const registeredMonths = timeElapsed.months;

  const months = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
  let message = "Member since: " + registeredDate.getDate() + " " + months[registeredDate.getMonth()] + " " + registeredDate.getFullYear() + " (";
  if (registeredYears > 0) {
    message += registeredYears + ((registeredYears === 1) ? " Year" : " Years");
    if (registeredMonths > 0) {
      message += " and ";
    }
  }
  if (registeredMonths > 0 || registeredYears === 0) {
    message += registeredMonths + ((registeredMonths === 1) ? " Month" : " Months");
  }
  return message + ") ";
}

function getMonthsAndYearsBetween(start, end) {
  const timeElapsed = end - start;
  const daysElapsed = Math.floor(timeElapsed / (1000 * 60 * 60 * 24));
  const yearsElapsed = Math.floor(daysElapsed / 365);
  const monthsElapsed = Math.floor(((daysElapsed / 365) - yearsElapsed) * 12);
  return {
    months: monthsElapsed,
    years: yearsElapsed
  };
}