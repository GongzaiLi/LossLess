<!--
Login page for existing users
Author: Eric Song, Caleb Sim
Date: 3/3/2021
-->
<template>
  <div>
    <h2>Login to Wasteless</h2>
    <p>Email</p>

    <input type="email" v-model="email" required
           size="30"
           autofocus
           autocomplete="off"
    />
    <p>Password</p>

    <input v-model="password" type="password" required
           size="30"
           autofocus
           autocomplete="off"
    /> <br>


    <div v-if="errors.length">
      <ul>
        <li v-for="error in errors" v-bind:key="error" style="color:red">{{ error }}</li>
      </ul>
    </div>

    <span style="padding-right:10px">

        <button @click="login" style="margin-top:10px">Login</button>
      </span>

    <p> Don't have an account?
      <span>
            <button @click="goToRegisterPage" style="margin-top:10px">Register</button>
        </span>
    </p>


    <br><br><br>
    <span>Demo Mode</span>

    <button v-bind:class="{ 'green': isActive, 'blue': !isActive}" @click="toggle">{{isActive ? 'ON' : 'OFF'}} </button>

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
      isActive: false

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
    login: function () {
      this.errors = this.validateLoginFields();
      if (this.isActive) {
        this.errors.length = 0;
      }
      if (this.errors.length === 0) {
        this.makeLoginRequest();
      }
    },
    /**
     * Checks if both email and password fields are not empty, and
     * the email format is valid. Returns a list of error messages.
     * The list will be empty if there are not errors.
     */
    validateLoginFields() {
      let errors = [];

      if (!this.email) {
        errors.push("Please fill in the Email field");
      } else if (!this.email.includes("@")) {
        errors.push("The given email is not valid");
      }
      if (!this.password) {
        errors.push("Please fill in the Password field");
      }
      return errors;
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
     * Redirects to the registration page/component. To be used when
     * the user has no account and wants to sign up.
     */
    goToRegisterPage: function () {
      console.log("Redirecting to Register Page");
      this.$router.push({path: '/register'});
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

