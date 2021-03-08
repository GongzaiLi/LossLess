<!--
Individual User profile Page
Author: Gongzai Li && Eric Song
Date: 5/3/2021
-->
<template>
  <div id="userBox">
    <h2>{{ userData.nickname }}'s Profile Page</h2>
    <div>
      <p id="member-since">Member since:
        {{ dateR.day + " " + dateR.month[0] + " " + dateR.year + " (" + registrationTime + ") " }}</p>
    </div>
    <hr>
    <div>
      <p><b>Name:</b> {{ userData.firstName + " " + userData.middleName + " " + userData.lastName }}</p>
      <p><b>Date Of Birth:</b> {{ userData.dateOfBirth }}</p>
      <p><b>Email:</b> {{ userData.email }}</p>
      <p><b>Phone Number:</b> {{ userData.phoneNumber }}</p>
      <p><b>Home Address:</b> {{ userData.homeAddress }}</p>
      <p><b>Bio:</b> {{ userData.bio }}</p>
    </div>

    <hr>

    <button id="buttonLog" @click="logOut" style="margin-top:10px">Log out</button>


  </div>

</template>

<script>
import api from "../Api";
import usersInfo from './usersDate.json';

export default {
  data: function () {
    return {
      dateR: {
        year: 0,
        month: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
        day: 0
      },
      registrationTime: 0,
      userData: {
        firstName: "",
        lastName: "",
        middleName: "",
        nickname: "",
        bio: "",
        email: "",
        dateOfBirth: "",
        phoneNumber: "",
        homeAddress: ""
      }
    }
  },

  mounted() {
    this.getUserInfo(1);
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
      // fake the Api data from the response data
      this.userData = usersInfo.users[0];
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