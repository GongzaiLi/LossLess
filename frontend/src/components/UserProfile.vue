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
      <p  v-if="loggedInAsAdmin">
        <label v-if="userData.role==='user'">User:</label>
        <label v-else>Admin:</label>

      <button  @click="toggleAdmin">{{adminButtonToggle ? 'Remove Admin' : 'Make Admin'}} </button>
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
  /*Gets the inputted cookie from the cookie document string
  -Splits the string by ; character and turns it into an array then searches the array for inputted cookie name*/
    let name = cName + "=";
    let decodedCookie = decodeURIComponent(document.cookie);
    let cookieArray = decodedCookie.split(';');
    for(let i = 0; i <cookieArray.length; i++) {
      let c = cookieArray[i];
      while (c.charAt(0) === ' ') {
        c = c.substring(1);
      }
      if (c.indexOf(name) === 0) {
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
        id: "",
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
      adminButtonToggle: false,
      loggedInAsAdmin: false,
      demoMode: true
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
        .getUser(id)
        .then((response) => {
          this.$log.debug("Data loaded: ", response.data);
          this.userData = response.data;
        })
        .catch((error) => {
          this.$log.debug(error);
          this.error = "Failed to Load User Data"
        })
      // fake the Api data from the response data.
      // TESTING PURPOSES ONLY, REMOVE THIS WHEN THE BACKEND IS IMPLEMENTED
      this.userData = usersInfo.users[id];
      if (this.userData.role==='defaultGlobalApplicationAdmin' || this.userData.role==='globalApplicationAdmin') {
        this.adminButtonToggle = true;
      }
    },
    logOut: function () {
      this.$router.push({path: '/login'})
    },
    displayAdmin: function() {
      //Changes userIsAdmin variable to true if user is logged in as admin (has GAA cookie)
      if (getCookie('globalApplicationAdmin')==='1'){
        this.loggedInAsAdmin = true;
      }
    },
    /**
     * Revoke or give the current user the 'globalApplicationAdmin' role,
     * depending on whether the current user already has that role.
     */
    toggleAdmin: function() {
      if (this.userData.role==='defaultGlobalApplicationAdmin' || this.userData.role==='globalApplicationAdmin') {
        this.adminButtonToggle=false;
        this.revokeAdmin();
      } else {
        this.adminButtonToggle=true;
        this.giveAdmin();
      }
    },
    /**
     *  Attempts to grant the displayed user the 'globalApplicationAdmin' role
     *  by sending a request to the API. Will show an error alert if unsuccessful.
     */
    giveAdmin: function() {
      api
        .makeUserAdmin(this.userData.id)
        .then(() => {
          this.$log.debug(`Made user ${this.userData.id} admin`);
          this.userData.role = 'globalApplicationAdmin';
        })
        .catch((error) => {
          if (this.demoMode) {
            // DEMO PURPOSES ONLY. Remove this once we have an implementation of the API
            this.userData.role = 'globalApplicationAdmin';
            return;
            //////////////////////////
          }

          this.$log.debug(error);
          alert(error);
        });
    },
    /**
     *  Attempts to revoke the 'globalApplicationAdmin' role from the displayed user
     *  by sending a request to the API. Will show an error alert if unsuccessful.
     */
    revokeAdmin: function() {
      api
        .revokeUserAdmin(this.userData.id)
        .then(() => {
          this.$log.debug(`Revoked admin for user ${this.userData.id}`);
          this.userData.role = 'user';
        })
        .catch((error) => {
          if (this.demoMode) {
            // DEMO PURPOSES ONLY. Remove this once we have an implementation of the API
            this.userData.role = 'user';
            return;
            //////////////////////////
          }

          this.$log.debug(error);
          alert(error);
        });
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