<template>
  <div>
    <b-row>
      <b-col lg="2">
        <b-form-group label="Filter results by">
          <b-form-select v-model="dateType" :options="dateTypeOptions"></b-form-select>
        </b-form-group>
      </b-col>

      <b-col lg="2" v-if="dateType === 'year' || dateType === 'month'">
        <b-form-group label="Year">
          <b-form-input v-model="selectedYear" type="number" min="2000" :max="currentYear()"></b-form-input>
        </b-form-group>
      </b-col>

      <b-col lg="2" v-if="dateType === 'month'">
        <b-form-group label="Month">
          <b-form-select v-model="month" :options="monthNames"></b-form-select>
        </b-form-group>
      </b-col>

      <b-col lg="4" v-if="dateType === 'week'">
        <b-form-group label="Week starting:">
          <b-form-datepicker :date-disabled-fn="dateDisabled" value-as-date v-model="selectedWeek" class="eric-custom-week-picker"/>
        </b-form-group>
      </b-col>

      <b-col lg="4" v-if="dateType === 'day'">
        <b-form-group label="Select day">
          <b-form-datepicker v-model="selectedDay"/>
        </b-form-group>
      </b-col>

      <b-col lg="4" v-if="dateType === 'customRange'">
        <b-form-group label="Start day">
          <b-form-datepicker value-as-date v-model="startDay"/>
        </b-form-group>
      </b-col>
      <b-col lg="4" v-if="dateType === 'customRange'">
        <b-form-group label="End day">
          <b-form-datepicker value-as-date v-model="endDay"/>
        </b-form-group>
      </b-col>
    </b-row>
  </div>
</template>

<script>
const monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
export default {
  name: "DateRangeInput",
  data() {
    return {
      dateType: "any",
      selectedYear: this.currentYear(),
      month: this.currentMonth(),
      monthNames: monthNames,
      dateTypeOptions: {
        any: "No Filter",
        year: "Year",
        month: "Month",
        week: "Week",
        day: "Day",
        customRange: "Custom Range"
      },
      selectedWeek: this.currentWeek(),
      selectedDay: new Date(),
      startDay: new Date(),
      endDay: new Date(),
    }
  },
  methods: {
    dateDisabled(ymd, date) {
      return date.getDay() !== 0;
    },
    currentWeek() {
      const today = new Date();
      today.setDate(today.getDate() - today.getDay());
      return today;
    },
    currentMonth() {
      return monthNames[(new Date()).getMonth()];
    },
    currentYear() {
      return (new Date()).getFullYear();
    }
  },
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