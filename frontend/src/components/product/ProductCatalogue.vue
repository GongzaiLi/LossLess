<!--
Page that stores table show the business products
Author: Gongzai Li，Arish Abalos
Date: 15/4/2021
-->
<template>
  <div>
    <b-card v-if="canEditCatalogue">
      <b-card-title>Product Catalogue: {{businessName}}</b-card-title>
      <hr class='m-0'>
      <b-row align-v="center">
        <b-col md="8"><h6 class="ml-2">Click on a product to view more details</h6></b-col>
        <b-col md="4">
          <b-form-group>
            <b-button @click="openCreateProductModal" class="float-right">
              <b-icon-plus-square-fill animation="fade"/>
              Create
            </b-button>
          </b-form-group>
        </b-col>
      </b-row>

      <products-table ref="productTable" :editable="true"
                      v-on:productClicked="showProductDetails"
                      v-on:editProductClicked="openEditProductCard">
      </products-table>

      <b-modal id="product-card" hide-header hide-footer
               :no-close-on-backdrop="!isProductCardReadOnly" :no-close-on-esc="!isProductCardReadOnly"
      >
        <product-detail-card :product="productDisplayedInCard"
                             :disabled="isProductCardReadOnly"
                             :currency="currency"
                             :okAction="productCardAction"
                             :cancelAction="closeProductCardModal"
        />
        <b-alert :show="productCardError ? 120 : 0" variant="danger">{{ productCardError }}</b-alert>
      </b-modal>
    </b-card>

    <b-card id="catalogue-locked-card" v-if="!canEditCatalogue">
      <b-card-title>
        <b-icon-lock/> Can't edit product catalogue
      </b-card-title>
      <h6 v-if="businessNameIfAdminOfThisBusiness"><strong>You're an administrator of this business. To edit this catalogue, you must be acting as this business.</strong>
        <br><br>To do so, click your profile picture on top-right of the screen. Then, select the name of this business ('{{businessNameIfAdminOfThisBusiness}}') from the drop-down menu.</h6>
      <h6 v-else> You are not an administrator of this business. If you need to edit this catalogue, contact the administrators of the business. <br>
      Return to the business profile page <router-link :to="'/businesses/' + $route.params.id">here.</router-link></h6>
    </b-card>
  </div>
</template>

<style scoped>

h2 {
  text-align: center;
}

</style>

<script>
import api from "../../Api";
import productDetailCard from './ProductDetailCard';
import ProductsTable from "./ProductsTable";
import Api from "../../Api";


export default {
  components: {
    ProductsTable,
    productDetailCard
  },
  data: function () {
    return {
      businessName: "",
      currency: {
        symbol: '$',
        code: 'USD',
        name: 'US Dollar',
      },
      productCardAction: () => {},
      productCardError: "",
      productDisplayedInCard: {},
      isProductCardReadOnly: true,
      items: [],
      perPage: 10,
      currentPage: 1,
      oldProductId: 0,
    }
  },
  mounted() {
    const businessId = this.$route.params.id;
    this.refreshTable(businessId);
  },
  methods: {
    refreshTable: async function(businessId) {
      const curBusiness = (await Api.getBusiness(businessId)).data;
      this.businessName = curBusiness.name;
      await this.$refs.productTable.getProducts(curBusiness);
    },
    /**
     * modify the ID so that it doesn't display the "{businessId}-" at the start
     * @return string
     */
    setId: function (id) {
      return id.split(/-(.+)/)[1];
    },

    /**
     * when click the table will show a new model card.
     * @param product object
     */
    showProductDetails(product) {
      this.isProductCardReadOnly = true;

      this.productDisplayedInCard = Object.assign({}, product);
      this.productDisplayedInCard.id = this.setId(this.productDisplayedInCard.id);
      this.$bvModal.show('product-card');
    },

    /**
     * route to the create product page
     */
    openCreateProductModal: function () {
      this.isProductCardReadOnly = false;
      this.productCardAction = this.createProduct;

      this.productDisplayedInCard = {
        id: '',
        name: '',
        description: '',
        manufacturer: '',
        recommendedRetailPrice: 0,
        image: '',
      }
      this.$bvModal.show('product-card');
    },

    /**
     * button function when clicked shows edit card
     * @param product edit product
     */
    openEditProductCard: function (product) {
      this.isProductCardReadOnly = false;
      this.productCardAction = this.modifyProduct;

      this.productDisplayedInCard = Object.assign({}, product);
      this.oldProductId = product.id;
      this.productDisplayedInCard.id = this.setId(this.productDisplayedInCard.id);
      this.$bvModal.show('product-card');
    },

    /**
     * Closes the modal popup for the show/edit/create product card, and clears any errors displayed.
     */
    closeProductCardModal: function () {
      this.$bvModal.hide('product-card');
      this.productCardError = '';
    },

    /**
     * Makes a request to the API to create a product with the form input.
     * Then, will hide the product popup and reload the table items if successful.
     */
    async createProduct(event) {
      event.preventDefault(); // HTML forms will by default reload the page, so prevent that from happening
      await api
          .createProduct(this.$route.params.id, this.productDisplayedInCard)
          .then((createProductResponse) => {
            this.$log.debug("Product Created", createProductResponse);
            this.$bvModal.hide('product-card');
            this.refreshProducts();
          })
          .catch((error) => {
            this.productCardError = this.getErrorMessageFromApiError(error);
            this.$log.debug(error);
          });
    },

    /**
     * button function for ok when clicked calls an API
     * place holder function for API task
     */
    modifyProduct: async function (event) {
      event.preventDefault();

      let editData = this.productDisplayedInCard;
      delete editData["created"];
      await api
          .modifyProduct(this.$route.params.id, this.oldProductId, editData)
          .then((editProductResponse) => {
            this.$log.debug("Product has been edited",editProductResponse);
            this.$bvModal.hide('product-card');
            this.refreshProducts();
          })
          .catch((error) => {
            this.productCardError = this.getErrorMessageFromApiError(error);
            this.$log.debug(error);
          })
    },

    /**
     * Given an error thrown by a rejected axios (api) request, returns a user-friendly string
     * describing that error. Only applies to POST and PUT requests for products
     */
    getErrorMessageFromApiError(error) {
      if ((error.response && error.response.status === 400)) {
        return error.response.data;
      } else if ((error.response && error.response.status === 403)) {
        return "Forbidden. You are not an authorized administrator";
      } else if (error.request) {  // The request was made but no response was received, see https://github.com/axios/axios#handling-errors
        return "No Internet Connectivity";
      } else {
        return "Server error";
      }
    },

    /**
     * function when clicked refreshes the table so that it can be reloaded with
     * new/edited data
     */
    refreshProducts: function () {
      this.productCardError = "";
      this.refreshTable(this.$route.params.id);
    },
  },

  computed: {

    /**
     * True if the user can edit this catalogue (ie they are an admin or acting as this business)
     */
    canEditCatalogue: function() {
      return this.$currentUser.role !== 'user' ||
          (this.$currentUser.currentlyActingAs && this.$currentUser.currentlyActingAs.id === parseInt(this.$route.params.id))
    },

    /**
     * Returns the name of the business if the user is an admin of this business, otherwise returns null
     */
    businessNameIfAdminOfThisBusiness: function() {
      for (const business of this.$currentUser.businessesAdministered) {
        if (business.id === parseInt(this.$route.params.id)) {
          return business.name;
        }
      }
      return null;
    }
  },

  watch: {
    /**
     * Watches the current user data (specifically, who the user is acting as). If this changes to someone without permission
     * to access the catalogue, then a modal is shown informing them that they will be redirected to the business's home page.
    */
    $currentUser: {
      handler() {
        if (this.canEditCatalogue) {
          this.refreshTable(this.$route.params.id);
        }
      },
      deep: true, // So we can watch all the subproperties (eg. currentlyActingAs)
    },
    /**
     * This watches for those routing changes, and will update the profile with the catalogue of the business specified by the new route.
     * See https://router.vuejs.org/guide/essentials/dynamic-matching.html#reacting-to-params-changes for more info
     */
    /* The argument _from is not needed, so this is to stop eslint complaining:*/
    /* eslint no-unused-vars: ["error", { "argsIgnorePattern": "^_" }] */
    $route(to, _from) {
      const id = to.params.id;
      if (this.canEditCatalogue) {
        this.refreshTable(id);
      }
    },
  }
}
</script>
