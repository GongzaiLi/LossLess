<template>
  <div id="userBox">
    <h2>{{ userDetail.nickname }}'s Profile Page</h2>
    <div>
      <p id="member-since">{{ memberSince }}</p>
    </div>
    <hr>
    <div>
      <p><b>Name:</b> {{ userDetail.firstName + " " + userDetail.middleName + " " + userDetail.lastName }}</p>
      <p><b>Date Of Birth:</b> {{ userDetail.dateOfBirth }}</p>
      <p><b>Email:</b> {{ userDetail.email }}</p>
      <p><b>Phone Number:</b> {{ userDetail.phoneNumber }}</p>
      <p><b>Home Address:</b> {{ userDetail.homeAddress }}</p>
      <p><b>Bio:</b> {{ userDetail.bio }}</p>
    </div>

    <hr>

    <button id="buttonLog" @click="logOut" style="margin-top:10px">Log out</button>

  </div>

</template>

<script>
export default {
  data: function () {
    return {
      userDetail: {
        firstName: "Firstname",
        lastName: "Lastname",
        middleName: "Middlename",
        nickname: "Someone",
        bio: "Likes long walks on the beach",
        email: "johnsmith99@gmail.com",
        dateOfBirth: "1999-04-27",
        phoneNumber: "+64 3 555 0129",
        homeAddress: "4 Rountree Street, Upper Riccarton",
        created: "2021-03-11T00:32:00Z",
      }

    }
  },
  methods: {
    logOut: function () {

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
  computed: {
    memberSince: function() {
      let registeredDate = new Date(this.userDetail.created);
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

<style scoped>

#userBox {
  border: 2px solid #a1a1a1;
  padding: 10px 40px;
  background: floralwhite;
  width: 400px;
  border-radius: 10px;
}

#member-since {
  margin-top: -10px;
}

</style>