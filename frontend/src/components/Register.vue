<!--
Page for users to input their information for registration
Authors: Nitish Singh, Eric Song
Date: 3/3/2021
-->


<template>
  <div>
    <b-card class="shadow">
      <h1> Sign Up to Wasteless </h1>
      <br>
      <b-form
        @submit="register"
        @input="setCustomValidities"
      >
        <b-form-group
        >
          <b>First Name *</b>
          <b-form-input v-model="firstName" required placeholder="First Name" autofocus></b-form-input>
        </b-form-group>

        <b-form-group
        >
          <b>Last Name *</b>
          <b-form-input v-model="lastName" required placeholder="Last Name"></b-form-input>
        </b-form-group>

        <b-form-group
        >
          <b>Middle Name</b>
          <b-form-input v-model="middleName" placeholder="Middle Name"></b-form-input>
        </b-form-group>

        <b-form-group
        >
          <b>Nickname</b>
          <b-form-input v-model="nickname" placeholder="Nick Name"></b-form-input>
        </b-form-group>

        <b-form-group
        >
          <b>Bio</b>
          <b-form-textarea v-model="bio" placeholder="Enter your Bio"></b-form-textarea>
        </b-form-group>

        <b-form-group
        >
          <b>Email *</b>
          <b-form-input required type="email" v-model="email" placeholder="Email"></b-form-input>
        </b-form-group>

        <b-form-group>
          <b>Password *</b>
          <div class="input-group mb-2 mr-sm-2">
            <b-form-input v-if="visiblePassword"
                          type="text" required
                          v-model=password
                          class="form-control"
                          placeholder="Password"></b-form-input>
            <b-form-input v-else-if="!visiblePassword"
                          type="password" required
                          v-model=password
                          class="form-control"
                          placeholder="Password"></b-form-input>
            <div class="input-group-prepend">
              <div class="input-group-text" v-if="!visiblePassword">
                <b-icon-eye-fill @click="showPassword"></b-icon-eye-fill>
              </div>
              <div class="input-group-text" v-else-if="visiblePassword">
                <b-icon-eye-slash-fill @click="showPassword('show')"></b-icon-eye-slash-fill>
              </div>
            </div>
          </div>
        </b-form-group>

        <b-form-group>
          <b>Confirm Password *</b>
          <div class="input-group mb-2 mr-sm-2">
            <b-form-input v-if="visibleConfirmPassword"
                          type="text" required
                          v-model=confirmPassword
                          class="form-control"
                          id="confirmPasswordInput"
                          placeholder="Confirm Password"
                          autocomplete="off"/>
            <b-form-input v-else-if="!visibleConfirmPassword"
                          type="password" required
                          v-model=confirmPassword
                          class="form-control"
                          id="confirmPasswordInput"
                          placeholder="Confirm Password"
                          autocomplete="off"/>
            <div class="input-group-prepend">
              <div class="input-group-text" v-if="!visibleConfirmPassword">
                <b-icon-eye-fill @click="showConfirmPassword"/>
              </div>
              <div class="input-group-text" v-else-if="visibleConfirmPassword">
                <b-icon-eye-slash-fill @click="showConfirmPassword('show')"/>
              </div>
            </div>
          </div>
        </b-form-group>

        <b-form-group
        >
          <b>Home Address *</b>
          <address-input v-model="homeAddress"/>
        </b-form-group>

        <b-form-group
        >
          <b>Date of Birth *</b>
          <b-form-input type="date" v-model="dateOfBirth" required
           placeholder="Date of Birth"
           autocomplete="off"
           size=30;
          />
        </b-form-group>

        <b-form-group
        >
          <b>Phone Number</b>
          <b-form-input v-model="phoneNumber"
           placeholder="Phone Number"
           autocomplete="off"
           size=30;
          />
          <div class="invalid-feedback">Please enter a phone # like 123-456-7890. This field is required.</div>
        </b-form-group>
        <b-button variant="primary" type="submit" style="margin-top:0.7em" id="register-btn">Register</b-button>
      </b-form>
      <br>
      <div v-if="errors.length">
        <b-alert variant="danger" v-for="error in errors" v-bind:key="error" dismissible :show="true">{{ error }} </b-alert>
      </div>
      <h6>
        Already have an account? <router-link to="/login">Login here</router-link>
      </h6>
    </b-card>
    <br>

    <b-form-group
        label-cols="auto"
        label="Demo Mode"
        label-for="input-horizontal">
      <b-button v-bind:variant="demoVariant" @click="toggle" >{{isDemoMode ? 'ON' : 'OFF'}} </b-button>
    </b-form-group>
  </div>
</template>

<script>
import api from "@/Api";
import AddressInput from "@/components/AddressInput";
import usersInfo from "@/components/data/usersDate.json";

export default {
  components: {
    AddressInput
  },
  data: function () {
    return {
      "firstName": "",
      "lastName": "",
      "middleName": "",
      "nickname": "",
      "bio": "",
      "email": "",
      "dateOfBirth": "",
      "phoneNumber": "",
      "homeAddress": "",
      "password": "",
      "confirmPassword": "",
      isDemoMode: true,
      errors: [],
      visiblePassword: false,
      visibleConfirmPassword: false
    }
  },
  methods: {
    toggle: function () {
      this.isDemoMode = !this.isDemoMode;
    },
    //Password can hidden or shown by clicking button
    showPassword: function (value) {
      this.visiblePassword = !(value === 'show');
    },
    //ConfirmPassword can hidden or shown by clicking button
    showConfirmPassword: function (value) {
      this.visibleConfirmPassword = !(value === 'show');
    },

    getRegisterData() {
      return {
        firstName: this.firstName,
        lastName: this.lastName,
        middleName: this.middleName,
        nickname: this.nickname,
        bio: this.bio,
        email: this.email,
        dateOfBirth: this.dateOfBirth,
        phoneNumber: this.phoneNumber,
        homeAddress: this.homeAddress,
        password: this.password
      };
    },

    /**
     * Author: Eric Song
     * Uses HTML constraint validation to set custom validity rules (so far, only checks that the 'password'
     * and 'confirm password' fields match). See below for more info:
     * https://stackoverflow.com/questions/49943610/can-i-check-password-confirmation-in-bootstrap-4-with-default-validation-options
     */
    setCustomValidities() {
      const confirmPasswordInput = document.getElementById('confirmPasswordInput');
      confirmPasswordInput.setCustomValidity(this.password !== this.confirmPassword ? "Passwords do not match." : "");
    },

    /**
     * Makes a request to the API to register a user with the form input.
     * Then, will redirect to the login page if successful.
     * Performs no input validation. Validation is performed by the HTML form.
     * Thus, this method should only ever be used as the @submit property of a form.
     * The parameter event is passed
     */
    register(event) {
      console.log("CLICKED");
      event.preventDefault(); // HTML forms will by default reload the page, so prevent that from happening

      let registerData = this.getRegisterData();
      console.log(registerData);

      api
        .register(registerData)
        .then((loginResponse) => {
          this.$log.debug("Registered");
          return api.getUser(loginResponse.userId);
        })
        .then((userResponse) => {
          this.$currentUser = userResponse.data;
          this.$router.push({path: `/users/${userResponse.data.id}`});
        })
        .catch((error) => {
          this.errors = [];
          this.$log.debug(error);
          if ((error.response && error.response.status === 400)) {
            this.errors.push("Registration failed.");
          } else {
            this.errors.push(error.message);
          }
        });
      if (this.isDemoMode) {
        this.$currentUser = usersInfo.users[1];
        this.$router.push({path: '/user/1'});
      }
    },
  },
  computed: {
    //if in demo mode or not change style of the button
    demoVariant() {
      return this.isDemoMode ? 'outline-success' : 'outline-danger';
    }
  }
}
</script>