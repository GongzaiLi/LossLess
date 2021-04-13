<!--
Individual User profile Page. Currently displays all user data
to all logged in users, regardless of permissions.
Author: Gongzai Li && Eric Song
Date: 5/3/2021
-->
<template>
  <div>
    <b-card border-variant="secondary" header-border-variant="secondary"
            class="profile-card shadow" no-body
            v-if="userFound"
    >

      <template #header>

        <b-row>
          <b-col>
            <h4 class="mb-1">{{ userData.firstName + " " + userData.lastName }}</h4>
            <member-since :date="userData.created"/>
          </b-col>
          <b-col cols="2" sm="auto"
                 v-if="($getCurrentUser().role==='defaultGlobalApplicationAdmin'||$getCurrentUser().role==='globalApplicationAdmin')">
            <h4>{{ userRoleDisplayString }}</h4>
            <b-button v-bind:variant="adminButtonToggle"
                      v-if="(userData.role!=='defaultGlobalApplicationAdmin'&&userData.id!==$getCurrentUser().id)"
                      @click="toggleAdmin">{{ adminButtonText }}
            </b-button>
          </b-col>

        </b-row>

        <b-row>

          <b-col cols="">

          </b-col>
        </b-row>
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
              <b-col cols="0">
                <b-icon-person-fill></b-icon-person-fill>
              </b-col>
              <b-col cols="4"><b>Full Name:</b></b-col>
              <b-col>{{ userData.firstName + " " + userData.middleName + " " + userData.lastName }}</b-col>
            </b-row>
          </h6>
          <h6>
            <b-row v-show="userData.nickname">
              <b-col cols="0">
                <b-icon-emoji-smile-fill></b-icon-emoji-smile-fill>
              </b-col>
              <b-col cols="4"><b>Nickname:</b></b-col>
              <b-col>{{ userData.nickname }}</b-col>
            </b-row>
          </h6>
          <h6>
            <b-row>
              <b-col cols="0">
                <b-icon-calendar-event-fill></b-icon-calendar-event-fill>
              </b-col>
              <b-col cols="4"><b>Date Of Birth:</b></b-col>
              <b-col>{{ userData.dateOfBirth }}</b-col>
            </b-row>
          </h6>
          <h6>
            <b-row>
              <b-col cols="0">
                <b-icon-envelope-fill></b-icon-envelope-fill>
              </b-col>
              <b-col cols="4"><b>Email:</b></b-col>
              <b-col>{{ userData.email }}</b-col>
            </b-row>
          </h6>
          <h6 v-if="userData.phoneNumber">
            <b-row>
              <b-col cols="0">
                <b-icon-telephone-fill></b-icon-telephone-fill>
              </b-col>
              <b-col cols="4"><b>Phone Number:</b></b-col>
              <b-col>{{ userData.phoneNumber }}</b-col>
            </b-row>
          </h6>
          <h6>
            <b-row>
              <b-col cols="0">
                <b-icon-house-fill></b-icon-house-fill>
              </b-col>
              <b-col cols="4"><b>Home Address:</b></b-col>
              <b-col> {{ userData.homeAddress }}</b-col>
            </b-row>
          </h6>
          <h6><b-row>
            <b-col cols="0">
              <b-icon-building></b-icon-building>
            </b-col>
            <b-col cols="4"><b>Businesses Created:</b></b-col>
            <b-col>
            <router-link v-for="card in this.userData.businessesPrimarilyAdministered"   :to="'/businesses/'+card.id.toString()" v-bind:key="card.id">
                {{ card.name }}<br></router-link></b-col>
          </b-row>

          </h6>

        </b-container>
      </b-card-body>
    </b-card>

    <b-card border-variant="secondary" header-border-variant="secondary"
            style="max-width: 45rem" no-body
            v-if="!userFound"
    >
      <template #header>
        <h4 class="mb-1">User not found</h4>
      </template>
      <b-card-body>
        <h6>
          The user you are looking for does not exist. The account may have been deleted, or you may have typed an
          invalid URL into the address bar.<br><br>
          Try
          <router-link to="/userSearch">searching for a user here.</router-link>
        </h6>
      </b-card-body>
    </b-card>
  </div>
</template>

<style scoped>
.profile-card {
  max-width: 45rem;
  margin-left: auto;
  margin-right: auto;
}

h6 {
  line-height: 1.4;
}
</style>

<script>
import api from "../Api";
import memberSince from "./MemberSince";

export default {
  components: {
    memberSince
  },

  data: function () {
    return {
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
        homeAddress: "",
        created: "",
        businessesPrimarilyAdministered:[],
      },
      userFound: true,
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
        .getUser(id)
        .then((response) => {
          this.$log.debug("Data loaded: ", response.data);
          this.userData = response.data;
          this.userFound = true;
        })
        .catch((error) => {
          this.$log.debug(error);
          this.userFound = false;
        })
    },
    //return user to login screen
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
    getMonthsAndYearsBetween(start, end) {
      const timeElapsed = end - start;
      const daysElapsed = Math.floor(timeElapsed / (1000 * 60 * 60 * 24));
      const yearsElapsed = Math.floor(daysElapsed / 365);
      const monthsElapsed = Math.floor(((daysElapsed / 365) - yearsElapsed) * 12);
      return {
        months: monthsElapsed,
        years: yearsElapsed
      }
    },

    /**
     * Revoke or give the current user the 'globalApplicationAdmin' role,
     * depending on whether the current user already has that role.
     */
    toggleAdmin: function () {
      if (this.userData.role === 'defaultGlobalApplicationAdmin' || this.userData.role === 'globalApplicationAdmin') {
        this.revokeAdmin();
      } else {
        this.giveAdmin();
      }
    },
    /**
     *  Attempts to grant the displayed user the 'globalApplicationAdmin' role
     *  by sending a request to the API. Will show an error alert if unsuccessful.
     */
    giveAdmin: function () {
      api
        .makeUserAdmin(this.userData.id)
        .then(() => {
          this.$log.debug(`Made user ${this.userData.id} admin`);
          this.userData.role = 'globalApplicationAdmin';
        })
        .catch((error) => {
          this.$log.debug(error);
          alert(error);
        });
    },
    /**
     *  Attempts to revoke the 'globalApplicationAdmin' role from the displayed user
     *  by sending a request to the API. Will show an error alert if unsuccessful.
     */
    revokeAdmin: function () {
      api
          .revokeUserAdmin(this.userData.id)
          .then(() => {
            this.$log.debug(`Revoked admin for user ${this.userData.id}`);
            this.userData.role = 'user';
          })
          .catch((error) => {
            this.$log.debug(error);
            alert(error);
          });
    }
  },
  computed: {
    adminButtonToggle() {
      return this.userData.role === 'user' ? 'success' : 'danger';
    },
    /**
     * User friendly display string for the user role. Converts the user role
     * string given by the api (eg. 'user', 'globalApplicationAdmin') to
     * a more user-friendly string to be displayed (eg. 'User', 'Site Admin')
     */
    userRoleDisplayString: function () {
      switch (this.userData.role) {
        case 'globalApplicationAdmin':
          return "Site Admin";
        case 'defaultGlobalApplicationAdmin':
          return "Default Site Admin";
        default:
          return "User";
      }
    },
    /**
     * Toggles the button text to add/remove admin privileges on a profile based on the user's role
     */
    adminButtonText: function () {
      switch (this.userData.role) {
        case 'globalApplicationAdmin':
          return "Remove Admin";
        default:  // Button won't even appear if they are default global admin so this is fine
          return "Make Admin";
      }
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
      const userId = to.params.id;
      this.getUserInfo(userId);
    },
  },

}
</script>
