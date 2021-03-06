<!--
Individual User profile Page
Author: Gongzai Li && Eric Song
Date: 5/3/2021
-->
<template>
  <div id="userBox">
    <h2>{{ userDetail.nickname }}'s Profile Page</h2>
    <div>
      <p id="member-since">Member since:
        {{ dateR.day + " " + dateR.month[0] + " " + dateR.year + " (" + registrationTime + ") " }}</p>
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
      userDetail: {
        firstName: "",
        lastName: "",
        middleName: "",
        nickname: "",
        bio: "",
        email: "",
        dateOfBirth: "",
        phoneNumber: "",
        homeAddress: ""
      },
      userData: {}

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
      this.userData = usersInfo.users[0]; // fake the Api data from the response data

      this.userDetail.firstName = this.userData.firstName;
      this.userDetail.lastName = this.userData.lastName;
      this.userDetail.middleName = this.userData.middleName;
      this.userDetail.nickname = this.userData.nickname;
      this.userDetail.bio = this.userData.bio;
      this.userDetail.email = this.userData.email;
      this.userDetail.dateOfBirth = this.userData.dateOfBirth;
      this.userDetail.phoneNumber = this.userData.phoneNumber;
      this.userDetail.homeAddress = this.userData.homeAddress;
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