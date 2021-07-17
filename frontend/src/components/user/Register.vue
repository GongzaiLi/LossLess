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
          <strong>First Name *</strong>
          <b-form-input v-model="firstName" maxLength=50 required placeholder="First Name" autofocus></b-form-input>
        </b-form-group>

        <b-form-group
        >
          <strong>Last Name *</strong>
          <b-form-input v-model="lastName" maxLength=50 required placeholder="Last Name"></b-form-input>
        </b-form-group>

        <b-form-group
        >
          <strong>Middle Name</strong>
          <b-form-input v-model="middleName" maxLength=50 placeholder="Middle Name"></b-form-input>
        </b-form-group>

        <b-form-group
        >
          <strong>Nickname</strong>
          <b-form-input v-model="nickname" maxLength=50 placeholder="Nick Name"></b-form-input>
        </b-form-group>

        <b-form-group
        >
          <strong>Bio</strong>
          <b-form-textarea v-model="bio" maxLength=250 placeholder="Enter your Bio"></b-form-textarea>
        </b-form-group>

        <b-form-group
        >
          <strong>Email *</strong>
          <b-form-input required type="email" maxLength=50 v-model="email" placeholder="Email"></b-form-input>
        </b-form-group>

        <b-form-group>
          <strong>Password *</strong>
          <div class="input-group mb-2 mr-sm-2">
            <b-form-input v-bind:type="passwordType" required
                          v-model=password
                          maxLength=50
                          class="form-control"
                          placeholder="Password"
                          autocomplete="off"/>
            <div class="input-group-prepend">
              <div class="input-group-text" @click="showPassword">
                <b-icon-eye-fill v-if="!visiblePassword"/>
                <b-icon-eye-slash-fill v-else-if="visiblePassword"/>
              </div>
            </div>
          </div>
        </b-form-group>

        <b-form-group>
          <strong>Confirm Password *</strong>
          <div class="input-group mb-2 mr-sm-2">
            <b-form-input v-bind:type="confirmPasswordType" required
                          v-model=confirmPassword
                          maxLength=50
                          class="form-control"
                          id="confirmPasswordInput"
                          placeholder="Confirm Password"
                          autocomplete="off"/>
            <div class="input-group-prepend">
              <div class="input-group-text" @click="showConfirmPassword">
                <b-icon-eye-fill v-if="!visibleConfirmPassword"/>
                <b-icon-eye-slash-fill v-else-if="visibleConfirmPassword"/>
              </div>
            </div>
          </div>
        </b-form-group>

        <b-form-group
        >
          <strong>Home Address *</strong>
          <address-input v-model="homeAddress"/>
        </b-form-group>

        <b-form-group
        >
          <strong>Date of Birth *</strong>
          <div>Note: you must be at least 13 years old to register</div>
          <b-form-input type="date" v-model="dateOfBirth" required
                        id="dateOfBirthInput"
                        placeholder="Date of Birth"
                        autocomplete="off"
                        size=30;
          />
        </b-form-group>

        <b-form-group
        >
          <strong>Phone Number</strong>
          <b-form-input v-model="phoneNumber"
                        maxLength=50
                        placeholder="Phone Number"
                        autocomplete="off"
                        size=30;
          />
        </b-form-group>
        <b-button variant="primary" type="submit" style="margin-top:0.7em" id="register-btn">Register</b-button>
      </b-form>
      <br>
      <div v-if="errors.length">
        <b-alert variant="danger" v-for="error in errors" v-bind:key="error" dismissible :show="true">{{
            error
          }}
        </b-alert>
      </div>
      <h6>
        Already have an account?
        <router-link to="/login">Login here</router-link>
      </h6>
    </b-card>
    <br>
  </div>
</template>

<script>
import api from "../../Api";
import AddressInput from "../model/AddressInput";

const MIN_AGE_YEARS = 13;
const MAX_AGE_YEARS = 120;
const UNIX_EPOCH_YEAR = 1970;

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
      "homeAddress": {
        "streetNumber": "",
        "streetName": "",
        "suburb": "",
        "city": "",
        "region": "",
        "country": "",
        "postcode": ""
      },
      "password": "",
      "confirmPassword": "",
      errors: [],
      visiblePassword: false,
      visibleConfirmPassword: false
    }
  },
  methods: {
    //Password can hidden or shown by clicking button
    showPassword: function () {
      this.visiblePassword = !this.visiblePassword;
    },
    //ConfirmPassword can hidden or shown by clicking button
    showConfirmPassword: function () {
      this.visibleConfirmPassword = !this.visibleConfirmPassword;
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
     * and 'confirm password' fields match, and the date of birth is valid). See below for more info:
     * https://stackoverflow.com/questions/49943610/can-i-check-password-confirmation-in-bootstrap-4-with-default-validation-options
     */
    setCustomValidities() {

      const confirmPasswordInput = document.getElementById('confirmPasswordInput');
      confirmPasswordInput.setCustomValidity(this.password !== this.confirmPassword ? "Passwords do not match." : "");

      const dateOfBirthInput = document.getElementById('dateOfBirthInput');
      dateOfBirthInput.setCustomValidity(this.dateOfBirthCustomValidity);
    },

    /**
     * Makes a request to the API to register a user with the form input.
     * Then, will redirect to the login page if successful.
     * Performs no input validation. Validation is performed by the HTML form.
     * Thus, this method should only ever be used as the @submit property of a form.
     * The parameter event is passed
     */
    async register(event) {
      event.preventDefault(); // HTML forms will by default reload the page, so prevent that from happening

      let registerData = this.getRegisterData();


      await api
        .register(registerData)
        .then((loginResponse) => {
          this.$log.debug("Registered");
          return api.getUser(loginResponse.data.id);
        })
        .then((userResponse) => {
          this.$currentUser = userResponse.data;
          this.$router.push({path: `/homePage`});
        })
        .catch((error) => {
          this.errors = [];
          this.$log.debug(error);
          if (error.response) {
            this.errors.push(`Registration failed: ${error.response.data.message}`);
          } else {
            this.errors.push("Sorry, we couldn't reach the server. Check your internet connection");
          }
        });
    },
  },
  computed: {
    passwordsMatch() {
      return this.password === this.confirmPassword;
    },
    //if the confirm password can visible to be text else be password
    confirmPasswordType() {
      return this.visibleConfirmPassword ? "text" : "password";
    },
    //if the password can visible to be text else be password
    passwordType() {
      return this.visiblePassword ? "text" : "password";
    },
    /**
     * Returns the HTML5 validity string based on the value of the birth date input. Birth dates must be at least
     * 13 years old and cannot be more tha 130 years old.
     * See below for more details:
     * https://stackoverflow.com/questions/49943610/can-i-check-password-confirmation-in-bootstrap-4-with-default-validation-options
     * @returns {string} A HTML5 validity string. Equal to "" (empty string) if the birthdate is valid,
     * otherwise "You must be at least 13 years old" or "Please enter a valid birthdate"
     */
    dateOfBirthCustomValidity() {
      const dateOfBirthJSDate = Date.parse(this.dateOfBirth);
      if (isNaN(dateOfBirthJSDate)) {
        return "Please enter a valid birthdate";
      }

      // Taken from https://stackoverflow.com/questions/8152426/how-can-i-calculate-the-number-of-years-between-two-dates
      const dateDiffMs = Date.now() - dateOfBirthJSDate;
      const diffDate = new Date(dateDiffMs); // milliseconds from epoch
      const yearsSinceBirthDay = diffDate.getUTCFullYear() - UNIX_EPOCH_YEAR;

      if (yearsSinceBirthDay < MIN_AGE_YEARS) {
        return "You must be at least 13 years old";
      } else if (yearsSinceBirthDay >= MAX_AGE_YEARS) {
        return "You cannot be older than 120 years"
      }
      return "";
    }
  }
}
</script>
