<!--
Individual User profile Page. Currently displays all user data
to all logged in users, regardless of permissions.
Author: Gongzai Li && Eric Song
Date: 5/3/2021
-->
<template>
  <b-card border-variant="secondary" header-border-variant="secondary" style="max-width: 45rem" no-body>
    <template #header>
      <h4 class="mb-1">{{ userData.firstName + " " + userData.lastName }}</h4>
      <p class="mb-1">Member since:
        {{ memberSince }}
      </p>
    </template>

    <b-list-group border-variant="secondary">
      <b-list-group-item>
        <b-card-text style="text-align: justify">
          {{ userData.bio }}
        </b-card-text>
      </b-list-group-item>
    </b-list-group>

    <b-card-body>
      <b-container>
        <h6>
          <b-row>
            <b-col cols="4"><b>Name:</b></b-col>
            <b-col>{{ userData.firstName + " " + userData.middleName + " " + userData.lastName }}</b-col>
          </b-row>
        </h6>
        <h6>
          <b-row v-show="userData.nickname.length">
            <b-col cols="4"><b>Nickname:</b></b-col>
            <b-col>{{ userData.nickname}}</b-col>
          </b-row>
        </h6>
        <h6>
          <b-row>
            <b-col cols="4"><b>Date Of Birth:</b></b-col>
            <b-col>{{ userData.dateOfBirth }}</b-col>
          </b-row>
        </h6>
        <h6>
          <b-row>
            <b-col cols="4"><b>Email:</b></b-col>
            <b-col>{{ userData.email }}</b-col>
          </b-row>
        </h6>
        <h6>
          <b-row>
            <b-col cols="4"><b>Phone Number:</b></b-col>
            <b-col>{{ userData.phoneNumber }}</b-col>
          </b-row>
        </h6>
        <h6>
          <b-row>
            <b-col cols="4"><b>Home Address:</b></b-col>
            <b-col> {{ userData.homeAddress }}</b-col>
          </b-row>
        </h6>
      </b-container>
    </b-card-body>


  </b-card>

</template>

<script>
import api from "../Api";
import usersInfo from './usersDate.json';

export default {
  data: function () {
    return {
      userData: {
        firstName: "",
        lastName: "",
        middleName: "",
        nickname: "",
        bio: "",
        email: "",
        dateOfBirth: "",
        phoneNumber: "",
        homeAddress: "",
      }
    }
  },

  mounted() {
    const userId = this.$route.params.id;
    this.getUserInfo(userId);
  },

  methods: {
    /**
     * this is a get api which can take Specific user to display on the page
     * The function id means user's id, if the serve find -
     * -the user's id will response the data and keep the data into this.userData
     */
    getUserInfo: function (id) {
      api
          .getUser(id) //
          .then((response) => {
            this.$log.debug("Data loaded: ", response.data);
            //this.userData = response.data;
          })
          .catch((error) => {
            this.$log.debug(error);
            this.error = "Failed to Load User Date"
          })
      // fake the Api data from the response data.
      // TESTING PURPOSES ONLY, REMOVE THIS WHEN THE BACKEND IS IMPLEMENTED
      this.userData = usersInfo.users[id];
    },
    logOut: function () {
      this.$router.push({path: '/login'})
    },
    /**
     * Takes a starting and ending date, then calculates the integer number of years and months elapsed since that date.
     * The months elapsed is not the total number of months elapsed, but the number months elapsed in
     * addition to the years also elapsed. For example, 1 year and 2 months instead of 1 year, 14 months.
     * Assumes that a year is 365 days, and every month is exactly 1/12 of a year.
     * Returns data in the format {months: months_elapsed, years: years_elapsed}
     */
    getMonthsAndYearsBetween (start, end) {
      const timeElapsed = end - start;
      const daysElapsed = Math.floor(timeElapsed / (1000 * 60 * 60 * 24));
      const yearsElapsed = Math.floor(daysElapsed / 365);
      console.log(daysElapsed / 365);
      const monthsElapsed = Math.floor(((daysElapsed / 365) - yearsElapsed) * 12);
      return {
        months: monthsElapsed,
        years: yearsElapsed
      }
    },
  },
  watch: {
    /**
     * When the user navigates from /user/foo to /user/bar, this component is re-used. This watches for those routing
     * changes, and will update the user profile with the data of the user specified by the new route.
     * See https://router.vuejs.org/guide/essentials/dynamic-matching.html#reacting-to-params-changes for more info
     */
    /* The argument _from is not needed, so this is to stop eslint complaining:*/
    /* eslint no-unused-vars: ["error", { "argsIgnorePattern": "^_" }] */
    $route(to, _from) {
      console.log(to);
      const userId = to.params.id;
      this.getUserInfo(userId);
    },
  },
  computed: {
    memberSince: function() {
      let registeredDate = new Date(this.userData.created);
      const timeElapsed = this.getMonthsAndYearsBetween(registeredDate, Date.now());
      const registeredYears = timeElapsed.years;
      const registeredMonths = timeElapsed.months;

      const months = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
      let message = "Member since: " + registeredDate.getDate() + " " + months[registeredDate.getMonth()] + " " + registeredDate.getFullYear() + " (";
      if (registeredYears > 0) {
        message += registeredYears + ((registeredYears > 1) ? " Year" : " Years");
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
