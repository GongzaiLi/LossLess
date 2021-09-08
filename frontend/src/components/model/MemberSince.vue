<!--
  Member since (date calculation)
  Author: Gongzai Li
  Date: 04/07/2021
-->
<template>
  <span class="mb-1" >{{ memberSince }}</span>
</template>

<script>
import {getMonthsAndYearsBetween} from '../../util'

export default {
  name: "member-since",
  props: ['date'], // a kind of date and valuable
  computed: {
    /**
     * calculates in years and months the time since the user account was created
     */
    memberSince: function () {
      let registeredDate = new Date(this.date);
      const timeElapsed = getMonthsAndYearsBetween(registeredDate, Date.now());
      const registeredYears = timeElapsed.years;
      const registeredMonths = timeElapsed.months;

      const months = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
      let message = registeredDate.getDate() + " " + months[registeredDate.getMonth()] + " " + registeredDate.getFullYear() + " (";
      if (registeredYears > 0) {
        message += registeredYears + ((registeredYears === 1) ? " Year" : " Years");
        if (registeredMonths > 0) {
          message += " and ";
        }
      }
      if (registeredMonths > 0 || registeredYears === 0) {
        message += registeredMonths + ((registeredMonths === 1) ? " Month" : " Months");
      }
      return message + ")";
    }
  }
}
</script>