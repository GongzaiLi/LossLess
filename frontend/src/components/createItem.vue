<!--
Page for users to input their information for registration
Authors: Phil Taylor
Date: 13/4/2021
-->

<template>
  <div>
    <b-card class="shadow">
      <h1>New Item</h1>
      <br>
      <b-form
        @submit="register"
      >
        <b-form-group
        >
          <b>Full Name *</b>
          <b-form-input v-model="fullName" required placeholder="Full Name" autofocus></b-form-input>
        </b-form-group>

        <b-form-group
        >
          <b>Description</b>
          <b-form-input v-model="description" placeholder="Description"></b-form-input>
        </b-form-group>

        <b-form-group
        >
          <b>Manufacturer</b>
          <b-form-input v-model="manufacturer" placeholder="Manufacturer Name"></b-form-input>
        </b-form-group>

        <b-form-group
        >
          <b>Recommended Retail Price</b>
          <b-form-input v-model="recommendedRetailPrice" placeholder="0.00"></b-form-input>
        </b-form-group>

        <b-button variant="primary" type="submit" style="margin-top:0.7em" id="register-btn">Register</b-button>
      </b-form>
      <br>
      <div v-if="errors.length">
        <b-alert variant="danger" v-for="error in errors" v-bind:key="error" dismissible :show="true">{{ error }} </b-alert>
      </div>

    </b-card>
    <br>

  </div>
</template>

<script>
import api from "@/Api";

export default {

  data: function () {
    return {
      "fullName": "",
      "description": "",
      "manufacturer": "",
      "recommendedRetailPrice": "",
      errors: []
    }
  },
  methods: {

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
     * Makes a request to the API to register a user with the form input.
     * Then, will redirect to the login page if successful.
     * Performs no input validation. Validation is performed by the HTML form.
     * Thus, this method should only ever be used as the @submit property of a form.
     * The parameter event is passed
     */
    register(event) {
      event.preventDefault(); // HTML forms will by default reload the page, so prevent that from happening

      let registerData = this.getRegisterData();
      //console.log(registerData);

      api
        .register(registerData)
        .then((loginResponse) => {
          this.$log.debug("Registered");
          return api.getUser(loginResponse.data.id);
        })
        .then((userResponse) => {
          this.$setCurrentUser(userResponse.data);
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
  }
}
</script>