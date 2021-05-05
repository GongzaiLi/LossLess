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
              <strong>Email</strong>
              <b-form-input type="email" v-model="email" required
                     autofocus
                     autocomplete="off"
              ></b-form-input>
            </b-form-group>

            <b-form-group>
              <strong>Password</strong>
              <div class="input-group mb-2 mr-sm-2">
                <b-form-input v-bind:type="passwordType" required
                              v-model=password
                              class="form-control"
                              autocomplete="off"/>
                <div class="input-group-prepend">
                  <div class="input-group-text" @click="showPassword">
                    <b-icon-eye-fill v-if="!visiblePassword"/>
                    <b-icon-eye-slash-fill v-if="visiblePassword"/>
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
    <br><br>
  </b-container>

</template>

<script>
import api from "../../Api";


export default {
  data: function () {
    return {
      errors: [],
      email: null,
      password: "",
      visiblePassword: false
    }
  },
  methods: {
    //Password can hidden or shown by clicking button
    showPassword: function () {
      this.visiblePassword = !this.visiblePassword;
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
    makeLoginRequest: async function () {
      let loginData = {
        email: this.email,
        password: this.password
      };
      this.errors = [];
      await api
        .login(loginData)
        .then((response) => {
          this.$log.debug("Logged in");
          return api.getUser(response.data.id);
        })
        .then((userResponse) => {
          this.$currentUser = userResponse.data;
          this.goToUserHomePage();
        })
        .catch((error) => {
          this.$log.debug(error);

          if (error.response && error.response.status === 400) {
            this.errors.push("The given username or password is incorrect.");
          } else {
            this.errors.push(error.message);
          }
        });
    },
    /**
     * Redirects to the home page of the user
     */
    goToUserHomePage : function () {
      this.$router.push({path:`/homePage`});
    }
  },
  computed: {
    //if the password can visible to be text else be password
    passwordType() {
      return this.visiblePassword ? "text" : "password";
    }
  }
}
</script>

