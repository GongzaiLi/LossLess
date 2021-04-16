<!--
Page for users to input their information for registration
Authors: Nitish Singh, Arish Abalos
Date: 26/3/2021
-->


<template>
  <div>
    <b-card class="shadow">
      <h1> Create a Business </h1>
      <br>
      <b-form
          @submit="createBusiness"
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

        <b-form-group>
          <b>Business Address *</b>
          <address-input v-model="address" required/>
        </b-form-group>

        <b-form-group>
          <b>Business Type *
          </b>
          <div class="input-group mb-xl-5">

            <select v-model="businessType" required>
              <option disabled value=""> Choose ...</option>
              <option> Accommodation and Food Services</option>
              <option> Retail Trade</option>
              <option> Charitable organisation</option>
              <option> Non-profit organisation</option>

            </select>

          </div>

        </b-form-group>

        <br>

        <b-button variant="primary" type="submit" style="margin-top:0.7em" id="create-btn">Create</b-button>
      </b-form>
      <br>
      <div v-if="errors.length">
        <b-alert variant="danger" v-for="error in errors" v-bind:key="error" dismissible :show="true">{{
            error
          }}
        </b-alert>
      </div>
    </b-card>
    <br>

  </div>
</template>

<script>
import api from "../../Api";
import AddressInput from "../../components/AddressInput";

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
      errors: [],
    }
  },
  methods: {

    getBusinessData() {
      return {
        name: this.name,
        description: this.description,
        address: this.address,
        businessType: this.businessType
      };
    },

    /**
     * Makes a request to the API to register a business with the form input.
     * Then, will redirect to the business page if successful.
     * Performs no input validation. Validation is performed by the HTML form.
     * Thus, this method should only ever be used as the @submit property of a form.
     * The parameter event is passed
     */
    async createBusiness(event) {
      let result = "success";
      // let afterPost;

      event.preventDefault(); // HTML forms will by default reload the page, so prevent that from happening

      let businessData = this.getBusinessData();

      //result = api.createBusiness(businessData);
      const response = await api.postBusiness(businessData);
      await this.$router.push({path: `/users/${this.$getCurrentUser().id}`});

      this.pushErrors(response);
      return result;


    },
    pushErrors(response) {
      const status = response[0];
      const value = response[1];
      if (status == "error") {
        this.errors = [];
        //this.$log.debug(value);
        if ((value.response && value.response.status === 401)) {
          this.errors.push("Access token is missing or invalid");
        } else {
          this.errors.push(value.message);
        }

      }

    }
  },
}
</script>