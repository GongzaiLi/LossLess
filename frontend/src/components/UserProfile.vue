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
        {{ dateR.day + " " + dateR.month[0] + " " + dateR.year + " (" + registrationTime + ") " }}
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
          this.userData = response.data;
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
    }
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
    }
  }
}
</script>
