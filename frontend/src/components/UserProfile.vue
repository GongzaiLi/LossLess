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
        created: "2020-07-14T14:32:00Z",
      }

    }
  },
  methods: {
    logOut: function () {

    }
  },
  computed: {
    memberSince: function() {
      let registeredDate = new Date(this.userDetail.created);
      let registeredMonths = Math.round((Date.now() - registeredDate) / (1000 * 60 * 60 * 24 * 30));
      let registeredYears = Math.round(registeredMonths / 12);

      const months = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
      let message = "Member since: " + registeredDate.getDate() + " " + months[registeredDate.getMonth()] + " " + registeredDate.getFullYear() + " (";
      if (registeredYears > 0) {
        message += registeredYears + ((registeredYears > 1) ? " Years" : " Year");
        if (registeredMonths > 0) {
          message += " and ";
        }
      }
      if (registeredMonths > 0) {
        message += registeredMonths + ((registeredMonths > 1) ? " Months" : " Month");
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