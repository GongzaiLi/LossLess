<!--
Page for users to input their information for registration
Authors: Phil Taylor
Date: 13/4/2021
-->

<template>
  <div>
    <b-card border-variant="secondary" header-border-variant="secondary"
            class="profile-card shadow"
    >
      <template #header>
      <h1>Create Product</h1>
      </template>
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
          <b-form-input type="number" step="0.01" v-model="recommendedRetailPrice" placeholder="0.00"></b-form-input>
        </b-form-group>

        <b-button variant="primary" type="submit" style="margin-right:1em" id="register-btn">Create</b-button>
          <router-link :to="'/businesses/'+this.businessId+'/products'" tag="button"><b-button variant="danger"   id="cancel-btn">Cancel</b-button></router-link>

      </b-form>
      <br>
      <div v-if="errors.length">
        <b-alert variant="danger" v-for="error in errors" v-bind:key="error" dismissible :show="true">{{ error }} </b-alert>
      </div>

    </b-card>
    <br>

  </div>
</template>
<style scoped>
.profile-card {
  max-width: 45rem;
  margin-left: auto;
  margin-right: auto;
}


</style>
<script>
import api from "@/Api";

export default {

  data: function () {
    return {
      "fullName": "",
      "description": "",
      "manufacturer": "",
      "recommendedRetailPrice": "",
      errors: [],
      businessId : null
    }
  },
  mounted() {
    this.businessId = this.$route.params.id;
  },
  methods: {

    getProductData() {
      return {
        fullName: this.fullName,
        description: this.description,
        manufacturer: this.manufacturer,
        recommendedRetailPrice: this.recommendedRetailPrice
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

      console.log(event);
      event.preventDefault(); // HTML forms will by default reload the page, so prevent that from happening

      let ProductData = this.getProductData();
      console.log(ProductData);

      api
        .createProduct(this.businessId,ProductData)
        .then((createProductResponse) => {
          this.$log.debug("Product Created",createProductResponse);
          this.$router.push({path: `/businesses/${this.businessId}/products`});
        })
        .catch((error) => {
          this.errors = [];
          this.$log.debug(error);
          if ((error.response && error.response.status === 400)) {
            this.errors.push("Creation failed. Please try again ");
          } else if ((error.response && error.response.status === 403)) {
            this.errors.push("Forbidden. You are not an authorized administrator");
          } else {
            this.errors.push(error.message);
          }
        });

    },
  }
}
</script>