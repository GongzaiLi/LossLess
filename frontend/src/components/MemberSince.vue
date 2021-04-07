<!--
  Member since (date calculation)
  Author: Gongzai Li
  Date: 04/07/2021
-->
<template>
  <p class="mb-1" >{{ memberSince }}</p>
</template>

<script>
export default {
  name: "member-since",
  props: ['date'], // a kind of date and valuable
  methods: {
    /**
     * Takes a starting and ending date, then calculates the integer number of years and months elapsed since that date.
     * The months elapsed is not the total number of months elapsed, but the number months elapsed in
     * addition to the years also elapsed. For example, 1 year and 2 months instead of 1 year, 14 months.
     * Assumes that a year is 365 days, and every month is exactly 1/12 of a year.
     * Returns data in the format {months: months_elapsed, years: years_elapsed}
     */
    getMonthsAndYearsBetween(start, end) {
      const timeElapsed = end - start;
      const daysElapsed = Math.floor(timeElapsed / (1000 * 60 * 60 * 24));
      const yearsElapsed = Math.floor(daysElapsed / 365);
      const monthsElapsed = Math.floor(((daysElapsed / 365) - yearsElapsed) * 12);
      return {
        months: monthsElapsed,
        years: yearsElapsed
      }
    },
  },
  computed: {
    /**
     * calculates in years and months the time since the user account was created
     */
    memberSince: function () {
      let registeredDate = new Date(this.date);
      const timeElapsed = this.getMonthsAndYearsBetween(registeredDate, Date.now());
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
  }
}
</script>