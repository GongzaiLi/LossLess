<!--
Login page for existing users
Author: Eric Song, Caleb Sim
Date: 3/3/2021
-->
<template>
  <div>
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

      <b-button type="submit" variant="outline-primary" style="margin-top:10px">Login</b-button>
      <br><br>
    </b-form>

    <p> Don't have an account?
      <router-link to="/register" >Register Here</router-link>
    </p>


    <br><br>

    <b-form-group
      label-cols="auto"
      label="Demo Mode"
      label-for="input-horizontal">
      <b-button v-bind:variant="demoVariant" @click="toggle" >{{isActive ? 'ON' : 'OFF'}} </b-button>
    </b-form-group>

  </div>

</template>

<script>
import api from "../Api";

export default {
  data: function () {
    return {
      makeLoginSucceed: true,
      loginFailed: false,
      errors: [],
      email: null,
      password: "",
      isActive: false,
    }
  },
  computed: {
    demoVariant() {
      return this.isActive ? 'outline-success' : 'outline-danger';
    }
  },
  methods: {
    toggle: function() {
      this.isActive = !this.isActive;
    },
    /**
     * Makes a POST request to the API to send a login request.
     * Sends the values entered into the email and password fields.
     * Login errors (eg. incorrect password) are displayed
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
      console.log(loginData);
      api
          .login(loginData)
          .then((response) => {
            this.$log.debug("Logged in");
            // Go to profile page
            this.goToUserProfilePage(response.userId);
          })
          .catch((error) => {
            // THIS if BLOCK IS FOR TESTING PURPOSES ONLY, DELETE ONCE WE HAVE A BACKEND
            if (this.makeLoginSucceed) {
              this.loginFailed = false;
              this.goToUserProfilePage(0);
              return;
            }

            this.loginFailed = true;
            this.$log.debug(error);
            if ((error.response && error.response.status === 400) || !this.makeLoginSucceed) {
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

