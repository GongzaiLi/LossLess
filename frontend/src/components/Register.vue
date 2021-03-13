<!--
Page for users to input their information for registration
Author: Nitish Singh
Date: 3/3/2021
-->


<template>
  <div class="register">

    <h2> Sign Up to Wasteless </h2>
    <b-form
      @submit="register"
      @input="setCustomValidities"
      :novalidate="isDemoMode"
    >
      <b-form-group
        label="First Name *"
      >
        <b-form-input v-model="firstName" required placeholder="First Name"></b-form-input>
      </b-form-group>

      <b-form-group
        label="Last Name *"
      >
        <b-form-input v-model="lastName" required placeholder="Last Name"></b-form-input>
      </b-form-group>

      <b-form-group
        label="Middle Name"
      >
        <b-form-input v-model="middleName" placeholder="Middle Name"></b-form-input>
      </b-form-group>

      <b-form-group
        label="Nickname"
      >
        <b-form-input v-model="nickname" placeholder="Nick Name" autofocus></b-form-input>
      </b-form-group>

      <b-form-group
        label="Bio"
      >
        <b-form-textarea v-model="bio" placeholder="Enter your Bio"></b-form-textarea>
      </b-form-group>

      <b-form-group
        label="Email *"
      >
        <b-form-input required type="email" v-model="email" placeholder="Email"></b-form-input>
      </b-form-group>

      <b-form-group
        label="Password *"
      >
        <b-form-input type="password" required v-model="password" placeholder="Password"></b-form-input>
      </b-form-group>

      <b-form-group
        label="Confirm Password *"
      >
        <b-form-input type="password" required v-model="confirmPassword" placeholder="Confirm Password" id="confirmPasswordInput" autocomplete="off"></b-form-input>
      </b-form-group>

      <b-form-group
        label="Date of Birth *"
      >
        <b-form-input type="date" v-model="dateOfBirth" required
         placeholder="Date of Birth"
         autofocus
         autocomplete="off"
         size=30;
        />
      </b-form-group>

      <b-form-group
        label="Phone Number"
      >
        <b-form-input v-model="phoneNumber"
         placeholder="Phone Number"
         autofocus
         autocomplete="off"
         size=30;
        />
        <div class="invalid-feedback">Please enter a phone # like 123-456-7890. This field is required.</div>
      </b-form-group>

      <p> Home Address * </p>

      <b-form-group
          class="address-input"
          label="Home Address *"
      >
        <b-form-textarea type="search" v-model="homeAddress" onkeypress=""
          @input="onAddressChange"
          placeholder="Enter Address"
        />

        <div class="address-options-list"
          v-if="addressFind.length > 0">
          <div class="address-option"
            v-for="address in addressFind"
            v-bind:key="address"
            @click="selectAddressOption(address)"
            >{{ address }}</div>
          <div class="address-option address-close"
            @click="addressFind=[]">&#10060; Close Suggestions</div>
        </div>
      </b-form-group>

      <b-button type="submit" style="margin-top:10px" id="register-btn">Register</b-button>
      </b-form>

    <br>
    <div v-if="errors.length">
      <h5 style="color:#ff0000" v-for="error in errors" v-bind:key="error" id="error-txt">{{ error }} </h5>
    </div>
    <br>
    <h6>
      Already have an account? <router-link to="/login">Login here.</router-link>
    </h6>
    <br><br><br>
    <span>Demo Mode</span>

    <button v-bind:class="{ 'green': isDemoMode, 'blue': !isDemoMode}" @click="toggle">{{ isDemoMode ? 'ON' : 'OFF' }}
    </button>

  </div>

</template>

<style>
form.errors :invalid {
  outline: 2px solid red;
}

.address-input {
  position: relative;
  display: inline-block;
}

.address-option {
  border-top: 2px solid gray;
}

.address-option:hover {
  background-color: lightgray;
}

.address-close {
  text-align: center;
}

.address-options-list {
  border-left: 2px solid gray;
  border-right: 2px solid gray;
  border-bottom: 2px solid gray;
  background-color: #FCFCFC;
  width: 100%;
  position: absolute;
  z-index: 9999;
  max-height: 20ex;
  overflow-y: scroll;
}
</style>

<script>
import api from "@/Api";

export default {

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
      isDemoMode: false,
      errors: [],
      addressSearchQuery: "",
      addressFind: [],
    }
  },
  methods: {
    toggle: function () {
      this.isDemoMode = !this.isDemoMode;
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
      event.preventDefault(); // HTML forms will by default reload the page, so prevent that from happening

      let registerData = this.getRegisterData();
      console.log(registerData);

      api
        .login(registerData)
        .then(() => {
          this.$log.debug("Registered");
          this.$router.push({path: '/login'});
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
        this.$router.push({path: '/login'});
      }
    },

    //gets JSON from photon.komoot.io api with inputted string and returns an array of the results from the search
    async getJson(input) {
      const url = 'https://photon.komoot.io/api/?q=' + input + '&limit=10';
      let returned = await (await fetch(url)).json();
      return returned.features;
    },

    /**
     * Authors: Eric Song, Caleb Sim
     * Given the address properties of an object returned by Photon Komoot, attempts to convert
     * it into an address string. Returns null if the address is not valid.
     * Rules for a valid address are:
     * - Address must have a country field
     * - Address must have either a county, district, or city field
     * - Address must have either a house number and street, or a name field
     */
    getStringFromPhotonAddress (address) {
      let addressString = "";
      let addressValid = false;

      if (address.country) {
        addressString = address.country;
        if (address.county || address.district || address.city) {
          addressString = (address.county || address.district || address.city) + ', ' + addressString;
          if (address.housenumber && address.street) {
            addressString = address.housenumber + ' ' + address.street + ', ' + addressString;
            addressValid = true;
          } else if (address.name) {
            addressString = address.name + ', ' + addressString;
            addressValid = true;
          }
        }
      }
      if (addressValid) {
        return addressString;
      } else {
        return null;
      }
    },
    /**
     * Author: Eric Song
     * Sets the address input field to the selected address
     * and closes the autocomplete drop down
     */
    selectAddressOption (address) {
      this.homeAddress = address;
      this.addressFind = [];
    },

    /**
     * Authors: Phil Taylor, Gongzai Li, Eric Song
     * Queries the Photon Komoot API with the home address and puts the
     * results into addressFind.
     * This method is called whenever the address input is changed
     * by the user's typing (as it is bound to @input).
     * This is not and should not be called when an autocomplete option is
     * clicked, otherwise the suggestions will pop up again when it is clicked.
     */
    async onAddressChange() {
      const addressQueryString = this.homeAddress.replace(/\s/gm," ");  // Replace newlines and tabs with spaces, otherwise Photon gets confused
      let returnQuery = await this.getJson(addressQueryString);
      this.addressFind = [];

      for (const result of returnQuery) {
        const addressString = this.getStringFromPhotonAddress(result.properties);

        if (addressString != null && !this.addressFind.includes(addressString)) {
          this.addressFind.push(addressString);
        }
      }
    }
  },
}
</script>