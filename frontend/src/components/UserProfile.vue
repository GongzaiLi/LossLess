<!--
Individual User profile Page. Currently displays all user data
to all logged in users, regardless of permissions.
Author: Gongzai Li && Eric Song
Date: 5/3/2021
-->
<template>
  <div id="userBox">
    <h2>{{ userData.nickname }}'s Profile Page</h2>
    <div>
      <p id="member-since">Member since:
        {{ dateR.day + " " + dateR.month[0] + " " + dateR.year + " (" + registrationTime + ") " }}</p>
      <p  v-if="userIsAdmin">
        <label v-if="userData.globalApplicationAdmin">Admin:</label>
        <label v-else>User:</label>

      <button v-bind:class="{ 'admin': userData.globalApplicationAdmin, 'user': !userData.globalApplicationAdmin}" @click="toggleAdmin">{{userData.globalApplicationAdmin ? 'Remove Admin' : 'Make Admin'}} </button>
      </p>
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

    <button id="logout-button" @click="logOut" style="margin-top:10px">Log out</button>

  </div>

</template>

<script>
import api from "../Api";
import usersInfo from './usersDate.json';

function getCookie(cName) {
    let name = cName + "=";
    let decodedCookie = decodeURIComponent(document.cookie);
    let cookieArray = decodedCookie.split(';');
    for(let i = 0; i <cookieArray.length; i++) {
      let c = cookieArray[i];
      while (c.charAt(0) == ' ') {
        c = c.substring(1);
      }
      if (c.indexOf(name) == 0) {
        return c.substring(name.length, c.length);
      }
    }
    return "";


}

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
        homeAddress: "",
        globalApplicationAdmin: ""
      },
      userIsAdmin: ""
    }
  },

  mounted() {
    const userId = this.$route.params.id;
    this.displayAdmin();
    this.getUserInfo( userId );



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
    },
    displayAdmin: function() {
      console.log(this.userIsAdmin);
      if (getCookie('globalApplicationAdmin')==='1'){
        this.userIsAdmin='True';
      }
    },
    toggleAdmin: function() {
      this.userData.globalApplicationAdmin = !this.userData.globalApplicationAdmin;
    },

  },



  watch: {
    /**
     * When the user navigates from /user/foo to /user/bar, this component is re-used. This watches for those routing
     * changes, and will update the user profile with the data of the user specified by the new route.
     * See https://router.vuejs.org/guide/essentials/dynamic-matching.html#reacting-to-params-changes for more info
     */
    /* The orgument _from is not needed, so this is to stop eslint complaining:*/
    /* eslint no-unused-vars: ["error", { "argsIgnorePattern": "^_" }] */
    $route(to, _from) {
      console.log(to);
      const userId = to.params.id;
      this.getUserInfo( userId );
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