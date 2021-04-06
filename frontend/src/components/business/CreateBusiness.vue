<!--
Page for users to input their information for registration
Authors: Nitish Singh, Eric Song
Date: 3/3/2021
-->


<template>
  <div>
    <b-card class="shadow">
      <h1> Create a Business </h1>
      <br>
      <b-form
          @submit="createBusiness"
          @input="setCustomValidities"
      >
        <b-form-group
        >
          <b>Business name *</b>
          <b-form-input v-model="name" required placeholder="Business Name" autofocus></b-form-input>
        </b-form-group>

        <b-form-group
        >
          <b>Description</b>
          <b-form-textarea v-model="description" placeholder="Description"></b-form-textarea>
        </b-form-group>

        <b-form-group >
          <b>Business Address *</b>
          <address-input v-model="address" required/>
        </b-form-group>

        <b-form-group >
          <b>Business Type
          </b>
          <div class="input-group mb-xl-5">

            <select v-model="businessType" required>
              <option disabled value=""> Choose ... </option>
              <option> Accommodation and Food Services </option>
              <option> Retail Trade</option>
              <option> Charitable organisation </option>
              <option> Non-profit organisation </option>

            </select>

          </div>

        </b-form-group>

        <br>

        <b-button variant="primary" type="submit" style="margin-top:0.7em" id="create-btn">Create</b-button>
      </b-form>
      <br>
      <div v-if="errors.length">
        <b-alert variant="danger" v-for="error in errors" v-bind:key="error" dismissible :show="true">{{ error }} </b-alert>
      </div>
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
//import api from "@/Api";
import AddressInput from "../AddressInput";
import usersInfo from "../data/usersDate.json";

export default {
  components: {
    AddressInput
  },
  data: function () {
    return {
      "name": "",
      "description": "",
      "address": "",
      "businessType": "",
      isDemoMode: true,
      errors: [],
    }
  },
  methods: {
    toggle: function () {
      this.isDemoMode = !this.isDemoMode;
    },

    getBusinessData() {
      return {
        name: this.name,
        description: this.description,
        address: this.address,
        businessType: this.businessType
      };
    },

    /**
     * Uses HTML constraint validation to set custom validity rules (so far, only checks that the 'password'
     * and 'confirm password' fields match). See below for more info:
     * https://stackoverflow.com/questions/49943610/can-i-check-password-confirmation-in-bootstrap-4-with-default-validation-options
     */
    setCustomValidities() {
    },

    /**
     * Makes a request to the API to register a user with the form input.
     * Then, will redirect to the login page if successful.
     * Performs no input validation. Validation is performed by the HTML form.
     * Thus, this method should only ever be used as the @submit property of a form.
     * The parameter event is passed
     */
    createBusiness(event) {
      event.preventDefault(); // HTML forms will by default reload the page, so prevent that from happening

      let businessData = this.getBusinessData();
      console.log(businessData);

      //To do
      // api
      //   .register(businessData)
      //   .then((loginResponse) => {
      //     this.$log.debug("Registered");
      //     return api.getUser(loginResponse.userId);
      //   })
      //   .then((userResponse) => {
      //     this.$currentUser = userResponse.data;
      //     this.$router.push({path: `/users/${userResponse.data.id}`});
      //   })
      //   .catch((error) => {
      //     this.errors = [];
      //     this.$log.debug(error);
      //     if ((error.response && error.response.status === 400)) {
      //       this.errors.push("Registration failed.");
      //     } else {
      //       this.errors.push(error.message);
      //     }
      //   });
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