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
    this.getUserInfo();
  },

  methods: {
    logOut: function () {
    },
    getUserInfo: function () {

      api
          .readUserInf()
          .then((response) => {
            this.$log.debug("Data loaded: ", response.data);
            //this.userData = response.data;
          })
          .catch((error) => {
            this.$log.debug(error);
            this.error = "Failed to Load User Date"
          })

      // fake the Api data from the response data
      this.userData.firstName = usersInfo.users[0].firstName;
      this.userData.lastName = usersInfo.users[0].lastName;
      this.userData.middleName = usersInfo.users[0].middleName;
      this.userData.nickname = usersInfo.users[0].nickname;
      this.userData.bio = usersInfo.users[0].bio;
      this.userData.email = usersInfo.users[0].email;
      this.userData.dateOfBirth = usersInfo.users[0].dateOfBirth;
      this.userData.phoneNumber = usersInfo.users[0].phoneNumber;
      this.userData.homeAddress = usersInfo.users[0].homeAddress;
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