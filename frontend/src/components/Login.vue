<!--
Login page for existing users
Author: Eric Song, Caleb Sim
Date: 3/3/2021
-->
<template>
  <b-container>
    <b-row class="justify-content-md-center">
      <b-col class="col-md-5">
        <h2>Login to Wasteless</h2>
        <b-form @submit="login" >
          <b-form-group
            label="Email"
            >
            <b-form-input type="email" v-model="email" required
                   autofocus
                   autocomplete="off"
            ></b-form-input>
          </b-form-group>

          <b-form-group
              label="Password"
          >
            <b-form-input v-model="password" type="password" required
                 autofocus
                 autocomplete="off"
            ></b-form-input>
          </b-form-group>
          <b-form-group
          >
          <b-button block type="submit" variant="primary" v-on:click="demoLogin" style="margin-top:0.7em">Login</b-button>
          </b-form-group>
        </b-form>

        <h6> Don't have an account?
          <router-link to="/register" >Register Here</router-link>
        </h6>
        <br><br>
      </b-col>
    </b-row>

    <b-form-group
        class="fixed-bottom"
        label-cols="auto"
        label="Demo Mode"
        label-for="input-horizontal">
      <b-button v-bind:variant="demoVariant" @click="toggle" >{{isActive ? 'ON' : 'OFF'}} </b-button>
    </b-form-group>

  </b-container>

</template>

<script>
import api from "../Api";

export default {
  data: function () {
    return {
      loginFailed: false,
      errors: [],
      email: null,
      password: "",
      isActive: false,
    }
  },
  computed: {
    //if in demo mode or not change style of the button
    demoVariant() {
      return this.isActive ? 'outline-success' : 'outline-danger';
    }
  },
  methods: {
    // toggle the demo mode variable when button clicked
    toggle: function() {
      this.isActive = !this.isActive;
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

    //called on submit to check if in demo mode if so go straight to user page 0
    demoLogin : function() {
      if (this.isActive) {
        this.goToUserProfilePage(0);
      }
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
      console.log(loginData);
      api
          .login(loginData)
          .then((response) => {
            this.$log.debug("Logged in");
            // Go to profile page
            this.goToUserProfilePage(response.userId);
          })
          .catch((error) => {
            this.loginFailed = true;
            this.$log.debug(error);
            if (error.response && error.response.status === 400) {
              this.errors.push("The given username or password is incorrect.");
            } else {
              this.errors.push(error.message);
            }
          });
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

