<!--
Page that stores table show the business products
Author: Gongzai Li，Arish Abalos
Date: 15/4/2021
-->
<template>

  <div>
    <h2>Product Catalogue: {{businessName}}</h2>
    <div>
      <b-form-group>
        <b-button @click="openCreateProductModal" class="float-right">
          <b-icon-plus-square-fill animation="fade"/>
          Create
        </b-button>
      </b-form-group>
      <b-table
        striped hovers
        responsive="lg"
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
      > <!--stacked="sm" table-class="text-nowrap"-->

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
        <b-alert :show="productCardError.length > 0 ? 120 : 0" variant="danger">{{ productCardError }}</b-alert>
      </b-modal>

      <b-modal id="changed-acting-as-modal" title="Account Changed"
               ok-only canno-close-on-backdrop no-close-on-esc hide-header-close
               @ok="goToHomePage"
      >
        <h6> <b>You have selected another account to act as.</b> <br> As you can only edit this page while you are acting as this business, you will be redirected to this business's home page.</h6>
      </b-modal>
    </div>
  </div>


</template>

<style>
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
        this.currency = currency;
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
      const showTenWordDescription = description.slice(0, 20).trim();
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
      this.createProductError = "";
      this.modifyProductError = "";
      this.getProducts(this.$route.params.id);
    },
    /**
     * Redirects to the home page of the current business (used when the user switches who they are acting as)
     */
    goToHomePage: function () {
      // There is a route guard in the router module that does this redirect when they are in this route,
      // so we only need to refresh the page to trigger the guard
      this.$router.go();
    }
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
  },

  watch: {
    /**
     * Watches the current user data (specifically, who the user is acting as). If this changes to someone without permission
     * to access the catalogue, then a modal is shown informing them that they will be redirected to the business's home page.
     */
    $currentUser: {
      handler(val){
        if (val.role === 'user' && (!val.currentlyActingAs || val.currentlyActingAs.id !== parseInt(this.$route.params.id))) {
          this.$bvModal.show('changed-acting-as-modal');
        }
      },
      deep: true, // So we can watch all the subproperties (eg. currentlyActingAs)
    },
  }
}
</script>
