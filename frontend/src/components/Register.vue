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
            <b-form-input v-bind:type="passwordType" required
                          v-model=password
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
          <b>Confirm Password *</b>
          <div class="input-group mb-2 mr-sm-2">
            <b-form-input v-bind:type="confirmPasswordType" required
                          v-model=confirmPassword
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
          <b>Home Address *</b>
          <address-input v-model="homeAddress"/>
        </b-form-group>

        <b-form-group
        >
          <b>Date of Birth *</b>
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
          <b>Phone Number</b>
          <b-form-input v-model="phoneNumber"
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
import api from "../Api";
import AddressInput from "./AddressInput";

const MIN_AGE_YEARS = 13;
const MAX_AGE_YEARS = 130;
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
    register(event) {
      console.log("CLICKED");
      event.preventDefault(); // HTML forms will by default reload the page, so prevent that from happening

      let registerData = this.getRegisterData();
      console.log(registerData);

      api
        .register(registerData)
        .then((loginResponse) => {
          this.$log.debug("Registered");
          return api.getUser(loginResponse.data.id);
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
      } else if (yearsSinceBirthDay > MAX_AGE_YEARS) {
        return "Please enter a valid birthdate"
      }
      return "";
    }
  }
}
</script>