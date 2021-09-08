<!--
  Member since (date calculation)
  Date: Sprint_1
-->
<template>
  <span class="mb-1" >{{ memberSince }}</span>
</template>

<script>
import {getMonthsAndYearsBetween, getMouthName} from '../../util'

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

      let message = registeredDate.getDate() + " " + getMouthName(registeredDate.getMonth()) + " " + registeredDate.getFullYear() + " (";
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