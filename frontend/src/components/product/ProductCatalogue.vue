<!--
Page that stores table show the business products
Author: Gongzai Li，Arish Abalos
Date: 15/4/2021
-->
<template>
  <div>
    <b-card v-if="canEditCatalogue">
      <b-card-title v-if="!tableLoading">Product Catalogue: {{businessName}}</b-card-title>
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
      <b-table
        striped hovers
        responsive
        no-border-collapse
        bordered
        show-empty
        @row-clicked="tableRowClick"
        class="catalogue-table"
        :fields="fields"
        :items="items"
        :per-page="perPage"
        :current-page="currentPage"
        :busy="tableLoading"
        ref="productCatalogueTable"
      >

        <template v-slot:cell(actions)="products">
          <b-button id="edit-button" @click="openEditProductCard(products.item)" size="sm">
            Edit
          </b-button>
        </template>

        <template #cell(recommendedRetailPrice)="data">
          {{ currency.symbol }}{{ data.item.recommendedRetailPrice }}
        </template>

        <template #empty>
          <div class="no-results-overlay">
            <h4>No Product to display</h4>
          </div>
        </template>
        <template #table-busy>
          <div class="no-results-overlay">
            <h4>Loading...</h4>
          </div>
        </template>
      </b-table>
      <pagination v-if="items.length>0" :per-page="perPage" :total-items="totalItems" v-model="currentPage"/>

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
.no-results-overlay {
  margin-top: 7em;
  margin-bottom: 7em;
  text-align: center;
}

h2 {
  text-align: center;
}

.catalogue-table td {
  cursor: pointer;
}
</style>

<script>
import api from "../../Api";
import productDetailCard from './ProductDetailCard';
import pagination from '../model/Pagination';


export default {
  components: {
    productDetailCard,
    pagination
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
      tableLoading: true,
      oldProductId: 0,
      currentUser: {},
    }
  },
  mounted() {
    const businessId = this.$route.params.id;
    this.getProducts(businessId);//this.getProducts(this.$route.params.id);
  },
  methods: {
    /**
     * this is a get api which can take Specific business product data keep in the items and then show in the table
     * The function id means business's id, if the serve find the business's id will response the data and call set ResponseData function
     * @param businessId
     */
    getProducts: async function (businessId) {
      // We need to make 3 asynchronous requests: Get all business products,
      // get the current business's address, and get the currency using that address.
      this.tableLoading = true;
      const getProductsPromise = api.getProducts(businessId);  // Promise for getting products
      const getCurrencyPromise = // Promise for getting the company address, and then the currency data
          api.getBusiness(businessId)
              .then((resp) => {
                this.businessName = resp.data.name;
                return api.getUserCurrency(resp.data.address.country);
              })

      try {
        const [productsResponse, currency] = await Promise.all([getProductsPromise, getCurrencyPromise]) // Run promises in parallel for lower latency

        this.items = productsResponse.data;
        this.tableLoading = false;
        if(currency != null){
          this.currency = currency;
        }
      } catch(error) {
        this.$log.debug(error);
      }
    },

    /**
     * modify the ID so that it doesn't display the "{businessId}-" at the start
     * @return string
     */
    setId: function (id) {
      return id.split(/-(.+)/)[1];
    },

    /**
     * modify the description only keep 20 characters and then add ...
     * @param description
     * @return string
     */
    setDescription: function (description) {
      if (description === "" || description.length <= 10) {
        const trimmedDescription = description.trim();
        return trimmedDescription;
      }
      const showTenWordDescription = description.slice(0, 10).trim();
      return `${showTenWordDescription}${showTenWordDescription.endsWith('.') ? '..' : '...'}`;
    },

    /**
     * modify the date to day/month/year.
     * @param created
     * @return string
     */
    setCreated: function (created) {
      const date = new Date(created);
      return `${date.getUTCDate() > 9 ? '' : '0'}${date.getUTCDate()}/` +
          `${date.getUTCMonth() + 1 > 9 ? '' : '0'}${date.getUTCMonth() + 1}/` +
          `${date.getUTCFullYear()}`;
    },

    /**
     * when click the table will show a new model card.
     * @param product object
     */
    tableRowClick(product) {
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
      this.getProducts(this.$route.params.id);
    },
  },

  computed: {

    /**
     * set table parameter
     * @returns object
     */
    fields: function () {
      return [
        {
          key: 'id',
          label: 'ID',
          formatter: (value) => {
            return this.setId(value);
          },
          sortable: true
        },
        {
          key: 'name',
          label: 'Name',
          sortable: true
        },
        {
          key: 'description',
          label: 'Description',
          formatter: (value) => {
            return this.setDescription(value);
          },
          sortable: true
        },
        {
          key: 'manufacturer',
          label: 'Manufacturer',
          sortable: true
        },
        {
          key: 'recommendedRetailPrice',
          label: `RRP (${this.currency.code})`,
          sortable: true
        },
        {
          key: 'created',
          label: 'Created',
          formatter: (value) => {
            return this.setCreated(value);
          },
          sortable: true
        },
        {
          key: 'actions',
          label: 'Action'
        }];
    },

    /**
     * The totalResults function just computed how many pages in the search table.
     * @returns number
     */
    totalItems: function () {
      return this.items.length;
    },

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
          this.getProducts(this.$route.params.id);
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
        this.getProducts(id);
      }
    },
  }
}
</script>
