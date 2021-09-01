<template>
  <b-form @submit.prevent="submitPressed">
    <b-row>
        <b-col lg="2" md="4">
          <b-form-group label="Filter results by">
            <b-form-select v-model="dateType" id="dateTypeSelect">
              <option v-for="[option, name] in Object.entries(dateTypeOptions)" :key="option" :value="option">{{name}}</option>
            </b-form-select>
          </b-form-group>
        </b-col>

        <b-col lg="2" md="4" v-if="dateType === 'year' || dateType === 'month'">
          <b-form-group label="Year">
            <b-form-input v-model="selectedYear" type="number" min="2000" :max="(new Date()).getFullYear()" id="yearSelector"></b-form-input>
          </b-form-group>
        </b-col>

        <b-col lg="2" md="4" v-if="dateType === 'month'">
          <b-form-group label="Month">
            <b-form-select v-model="selectedMonth" id="monthSelector">
              <option v-for="(month, index) in selectableMonths" :key="index" :value="index">{{month}}</option>
            </b-form-select>
          </b-form-group>
        </b-col>

        <b-col lg="4" v-if="dateType === 'week'">
          <b-form-group label="Week starting:">
            <b-form-datepicker :date-disabled-fn="weekDateDisabled" value-as-date v-model="selectedWeek" class="eric-custom-week-picker"/>
          </b-form-group>
        </b-col>

        <b-col lg="4" v-if="dateType === 'day'">
          <b-form-group label="Select day">
            <b-form-datepicker :date-disabled-fn="dateInFuture" v-model="selectedDay"/>
          </b-form-group>
        </b-col>

        <b-col lg="4" v-if="dateType === 'customRange'">
          <b-form-group label="Start day">
            <b-form-datepicker :date-disabled-fn="dateInFuture" value-as-date v-model="startDay"/>
          </b-form-group>
        </b-col>
        <b-col lg="4" v-if="dateType === 'customRange'">
          <b-form-group label="End day">
            <b-form-datepicker :date-disabled-fn="dateInFuture" value-as-date v-model="endDay" id="endDayPicker" :state="endDateValidityState"/>
            <b-form-invalid-feedback>
              End date must be same as or after starting date.
            </b-form-invalid-feedback>
          </b-form-group>
        </b-col>

        <b-col lg="2">
          <b-button type="submit" :disabled="endDateValidityState === false" variant="primary" class="mt-4" id="filterDateBtn">
            Filter
          </b-button>
        </b-col>
    </b-row>
  </b-form>
</template>

<script>
const monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
export default {
  name: "DateRangeInput",
  data() {
    return {
      dateType: "any",
      dateTypeOptions: {
        any: "No Filter",
        year: "Year",
        month: "Month",
        week: "Week",
        day: "Day",
        customRange: "Custom Range"
      },
      monthNames: monthNames,
      selectedYear: (new Date()).getFullYear(),
      selectedMonth: (new Date()).getMonth(),
      selectedWeek: this.currentWeek(),
      selectedDay: new Date(),
      startDay: new Date(),
      endDay: new Date(),
    }
  },
  methods: {
    /**
     * Returns true if the day is in the future. This function is used for the date-disabled-fn prop
     * in the day pickers.
     */
    dateInFuture(ymd, date) {
      return date > Date.now();
    },
    /**
     * Function that determines if a date should be disabled on the week date picker.
     * Returns true if the day isn't a Sunday (so the user can only pick Sundays to represent a week)
     * or if the day is in the future.
     */
    weekDateDisabled(ymd, date) {
      return this.dateInFuture(ymd, date) || date.getDay() !== 0;
    },
    /**
     * Convenience function that returns the first day of this week.
     * Weeks start on Sundays.
     */
    currentWeek() {
      const today = new Date();
      today.setDate(today.getDate() - today.getDay());
      return today;
    },
    /**
     * Handler when the user presses the 'Filter' button. Emits an input event with the given date range.
     * Values emitted is null if the user has not selected any filtering. Otherwise, it is an array containing
     * the first and last day of the selected year/month/week/day, of the custom range the user has selected
     */
    submitPressed() {
      let dateRange;
      if (this.dateType === "year") {
        dateRange = [new Date(this.selectedYear, 0, 1), new Date(this.selectedYear, 11, 31)];
      } else if (this.dateType === "month") {
        dateRange = [new Date(this.selectedYear, this.selectedMonth, 1), new Date(this.selectedYear, this.selectedMonth + 1, 0)];
      } else if (this.dateType === "week") {
        const endOfWeek = new Date(this.selectedWeek.getTime());
        endOfWeek.setDate(endOfWeek.getDate() + 6);
        dateRange = [this.selectedWeek, endOfWeek];
      } else if (this.dateType === "day") {
        dateRange = [new Date(this.selectedDay.getTime()), new Date(this.selectedDay.getTime())]; // Have to copy the dates as they will be modified later
      } else if (this.dateType === "customRange") {
        dateRange = [this.startDay, this.endDay];
      }

      if (this.dateType === "any") {
        dateRange = null;
      } else {
        // Set time of start date to start of day, and time of end date to end of day, so the date range includes those two days
        dateRange[0].setHours(0, 0, 0, 0);
        dateRange[1].setHours(23, 59, 59, 999);
      }

      this.$emit('input', dateRange);
    }
  },
  computed: {
    /**
     * Computed property returning the months that can be selected as options for the
     * month drop-down selector.
     * Returns all the months if the year selected is before the current year, otherwise
     * returns every month up to and including the current month.
     */
    selectableMonths() {
      if (this.selectedYear < (new Date()).getFullYear()) {
        return monthNames;
      } else {
        return monthNames.slice(0, (new Date()).getMonth() + 1);
      }
    },
    /**
     * Returns validation state for the end date picker. Return false (i.e. invalid value) if the end date is before
     * the start date, otherwise returns NULL (i.e. no validation state).
     */
    endDateValidityState() {
      return (this.startDay.getDate() <= this.endDay.getDate() ? null : false);
    }
  }
}
</script>

<style >
  .eric-custom-week-picker .b-calendar-grid [role='button']:hover {
    background-color: #dddddd;
  }
  .eric-custom-week-picker .b-calendar-grid-body [aria-selected='true'] {
    background-color: #a1ccff;
  }
  .eric-custom-week-picker .b-calendar-grid-body [aria-selected='true']:after {
    content: '';
    position: absolute;
    background-color: #a1ccff;
    opacity: 0.3;
    width: 15rem;
    right: -15rem;
    height: 100%;
    z-index: 999;
  }
</style>