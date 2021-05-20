<!--
Page for users to input business information for registration of a Business
Authors: James
Date: whenever
-->


<template>
  <div>
    <b-card class="shadow">
      <h1> Market Place </h1>

    </b-card>
  </div>
</template>

<script>
import api from "../../Api";
import getMonthsAndYearsBetween from '../../util';

const MIN_AGE_TO_CREATE_BUSINESS = 16;

export default {

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
      return monthsAndYearsBetween.years >= MIN_AGE_TO_CREATE_BUSINESS && !this.$currentUser.currentlyActingAs;
    }
  }
}
</script>