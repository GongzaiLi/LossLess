<!--
Page for users to input business information for registration of a Business
Authors: Nitish Singh, Arish Abalos
Date: 26/3/2021
-->


<template>
  <div>
    <b-card class="shadow" v-if="canCreateBusiness">
      <h1> Create a Business </h1>
      <br>
      <b-form
        @submit="createBusiness"
      >
        <b-form-group
        >
          <strong>Business name *</strong>
          <b-form-input v-model="name" required placeholder="Business Name" maxlength="50" autofocus></b-form-input>
        </b-form-group>

        <b-form-group
        >
          <strong>Description</strong>
          <b-form-textarea v-model="description" placeholder="Description" maxlength="250"></b-form-textarea>
        </b-form-group>

        <b-form-group>
          <strong>Business Address *</strong>
          <address-input v-model="address" required/>
        </b-form-group>

        <b-form-group>
          <strong>Business Type *
          </strong>
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
    <b-card v-else>
      <b-card-title>
        Not old enough to create a business
        <b-iconstack font-scale="3" style="float:right">
          <b-icon
              stacked
              icon="calendar2-x"
              variant="danger"
              scale="0.75"
          ></b-icon>
        </b-iconstack>

        <b-iconstack font-scale="3" style="float:right">
          <b-icon
              stacked
              icon="building"
              variant="info"
              scale="0.75"
          ></b-icon>
          <b-icon
              stacked
              icon="x-circle"
              variant="danger"
          ></b-icon>
        </b-iconstack>
      </b-card-title>
      <hr>

      <h4>You must be at least <strong>16 years old</strong> to create a business.</h4>
    </b-card>
  </div>
</template>

<script>
import api from "../../Api";
import AddressInput from "../model/AddressInput";
import getMonthsAndYearsBetween from '../../util';

const MIN_AGE_TO_CREATE_BUSINESS = 16;

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
      event.preventDefault();   // HTML forms will by default reload the page, so prevent that from happening

      let businessData = this.getBusinessData();

      try {
        const businessResponse = (await api.postBusiness(businessData)).data;
        this.$currentUser = (await api.getUser(this.$currentUser.id)).data;
        await this.$router.push({path: `/businesses/${businessResponse.businessId}`});
      } catch(error) {
        //console.log(error);
        this.pushErrors(error);
      }
    },
    /**
     * Pushes errors to errors list to be displayed as response on the screen,
     * if there are any.
     */
    pushErrors(error) {
      this.errors.push(error.message);
    }
  },
  computed: {
    canCreateBusiness: function() {
      const monthsAndYearsBetween = getMonthsAndYearsBetween(new Date(this.$currentUser.dateOfBirth), Date.now());
      console.log(this.$currentUser.dateOfBirth);
      return monthsAndYearsBetween.years >= MIN_AGE_TO_CREATE_BUSINESS;
    }
  }
}
</script>