<!--
Login page for existing users
Author: Eric Song, Caleb Sim
Date: 3/3/2021
-->
<template>
  <b-container>
    <b-row class="justify-content-md-center">
      <b-col class="col-md-7">
        <b-card class="shadow">
        <h2>Login to Wasteless</h2>
          <b-form @submit="login">
            <b-form-group>
              <b>Email</b>
              <b-form-input type="email" v-model="email" required
                     autofocus
                     autocomplete="off"
              ></b-form-input>
            </b-form-group>

            <b-form-group>
              <b>Password</b>
              <div class="input-group mb-2 mr-sm-2">
                <b-form-input v-if="visiblePassword"
                              type="text" required
                              v-model=password
                              class="form-control"
                              autocomplete="off"/>
                <b-form-input v-else-if="!visiblePassword"
                              type="password" required
                              v-model=password
                              class="form-control"
                              autocomplete="off"/>
                <div class="input-group-prepend">
                  <div class="input-group-text" v-if="!visiblePassword">
                    <b-icon-eye-fill @click="showPassword"/>
                  </div>
                  <div class="input-group-text" v-else-if="visiblePassword">
                    <b-icon-eye-slash-fill @click="showPassword('show')"/>
                  </div>
                </div>
              </div>

            </b-form-group>
            <b-form-group
            >
            <b-button block type="submit" variant="primary" style="margin-top:0.7em">Login</b-button>
            </b-form-group>
          </b-form>

          <b-alert variant="danger" v-for="error in errors" v-bind:key="error" dismissible :show="true">{{ error }}</b-alert>
          <h6> Don't have an account?
            <router-link to="/register" >Register Here</router-link>
          </h6>
        </b-card>
      </b-col>
    </b-row>

    <b-form-group
        class="fixed-bottom"
        label-cols="auto"
        label="Demo Mode"
        label-for="input-horizontal">
      <b-button v-bind:variant="demoVariant" @click="toggle" >{{ isDemoMode ? 'ON' : 'OFF' }}</b-button>
    </b-form-group>

  </b-container>

</template>

<script>
import api from "../Api";
import usersInfo from './data/usersDate.json';

export default {
  data: function () {
    return {
      errors: [],
      email: null,
      password: "",
      isDemoMode: true,
      visiblePassword: false
    }
  },
  computed: {
    //if in demo mode or not change style of the button
    demoVariant() {
      return this.isDemoMode ? 'outline-success' : 'outline-danger';
    }
  },
  methods: {
    // toggle the demo mode variable when button clicked
    toggle: function() {
      this.isDemoMode = !this.isDemoMode;
    },
    //Password can hold or show
    showPassword: function (value) {
      this.visiblePassword = !(value === 'show');
    },
    /**
     * only called if form page passes submit criteria
     * when user clicks login stop page deleting everything and refreshing
     * then make login request
     */
    login(event) {
      event.preventDefault()
      this.makeLoginRequest();
    },

    /**
     * Makes a POST request to the API to send a login request.
     * Sends the values entered into the email and password fields.
     * Login errors (eg. incorrect password) are displayed
     */
    makeLoginRequest: function () {
      let loginData = {
        email: this.email,
        password: this.password
      };
      this.errors = [];
      console.log(loginData);
      api
          .login(loginData)
          .then((response) => {
            this.$log.debug("Logged in");
            //set global variable of logged in user
            this.$currentUser.set(response.userId);
            // Go to profile page
            this.goToUserProfilePage(response.userId);
          })
          .catch((error) => {
            this.$log.debug(error);

            // Currently we will always get a network error as we have no backend.
            // In demos, don't show the network error. Delete this once we have a backend
            if (this.isDemoMode) return;

            if (error.response && error.response.status === 400) {
              this.errors.push("The given username or password is incorrect.");
            } else {
              this.errors.push(error.message);
            }
          });
      //if in demo mode set current user to default user 0 and go to user page
      if (this.isDemoMode) {
        this.demoModeLogin();
      }
    },
    /**
     * 'Logs' in the user by comparing the email to a bunch of dummy accounts
     * Password's aren't checked, only the emails admin@sengmail.com, user@sengmail.com
     * and defaultadmin@sengmail.com will give the dummy accounts
     * FOR DEMO PURPOSES ONLY!!!!!
     */
    demoModeLogin() {
      if (this.email === "admin@sengmail.com") {
        this.$currentUser = usersInfo.users[0];
      } else if (this.email === "user@sengmail.com") {
        this.$currentUser = usersInfo.users[1];
      } else if (this.email === "defaultadmin@sengmail.com") {
        this.$currentUser = usersInfo.users[2];
      } else {
        this.errors.push("The given username or password is incorrect.");
        return;
      }
      this.goToUserProfilePage(this.$currentUser.id);
    },
    /**
     * Redirects to the profile page of the user with the specified userId.
     * This will switch components immediately to the UserProfile component
     * so no loading spinner needs to be implemented here.
     */
    goToUserProfilePage: function (userId) {
      this.$router.push({path: `/user/${userId}`});
    }
  }
}
</script>

