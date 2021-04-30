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
        @submit="CreateProduct"
      >
        <b-form-group
        >
          <b>ID *</b>
          -This will be changed automatically into the correct format for you.
          <b-form-input v-model="id" required placeholder="PRODUCT-ID" autofocus></b-form-input>

        </b-form-group>
        <b-form-group
        >
          <b>Full Name *</b>
          <b-form-input v-model="name" required placeholder="Full Name" autofocus></b-form-input>
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
        <router-link :to="'/businesses/'+this.businessId+'/products'" custom v-slot="{ navigate }">
          <b-button variant="danger" id="cancel-btn" @click="navigate">Cancel</b-button>
        </router-link>

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
<style scoped>
.profile-card {
  max-width: 45rem;
  margin-left: auto;
  margin-right: auto;
}


</style>
<script>
import api from "../../Api";

export default {

  data: function () {
    return {
      "id": "",
      "name": "",
      "description": "",
      "manufacturer": "",
      "recommendedRetailPrice": "0.00",
      errors: [],
      businessId: null,
    }
  },
  mounted() {
    this.businessId = this.$route.params.id;
  },
  methods: {

    getProductData() {
      return {
        id: this.id,
        name: this.name,
        description: this.description,
        manufacturer: this.manufacturer,
        recommendedRetailPrice: this.recommendedRetailPrice
      };
    },


    /**
     * Makes a request to the API to create a product with the form input.
     * Then, will redirect back to the product list if successful.
     */
    CreateProduct(event) {

      event.preventDefault(); // HTML forms will by default reload the page, so prevent that from happening

      let ProductData = this.getProductData();
      let apiResponse =
        api
          .createProduct(this.businessId, ProductData)
          .then((createProductResponse) => {
            this.$log.debug("Product Created", createProductResponse);
            this.$router.push({path: `/businesses/${this.businessId}/products`});
            return "success";
          })
          .catch((error) => {
            this.errors = [];
            this.$log.debug(error);
            if ((error.response && error.response.status === 400)) {
              this.errors.push("Creation failed. Please try again");
            } else if ((error.response && error.response.status === 403)) {
              this.errors.push("Forbidden. You are not an authorized administrator");
            } else {
              this.errors.push("Server error");
            }

            return this.errors[0];
          })

      return apiResponse;

    },
  }
}
</script>
