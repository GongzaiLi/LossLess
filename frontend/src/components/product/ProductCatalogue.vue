<!--
Page that stores table show the business products
Author: Gongzai Li，Arish Abalos
Date: 15/4/2021
-->
<template>

  <div>
    <h2>Product Catalogue</h2>
    <div>
      <b-form-group>
        <b-button @click="goToCreateProduct" class="float-right">
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
      > <!--stacked="sm" table-class="text-nowrap"-->

        <template v-slot:cell(actions)="products">
          <b-button id="edit-button" @click="editProduct(products.item)" size="sm">
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

      <b-modal id="product-card" hide-header hide-footer centered @click="this.getProducts($route.params.id)">
        <product-detail-card :product="productSelect" :disabled="true" :currency="currency" :errors="errors"/>
      </b-modal>

      <b-modal id="edit-product-card" hide-header no-close-on-backdrop @ok="modifyProductAPI">
        <product-detail-card :product="productEdit" :disabled="false" :currency="currency" :errors="errors"/>
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
      currency: {
        symbol: '$',
        code: 'USD',
        name: 'US Dollar',
      },
      errors: [],
      items: [],
      perPage: 10,
      currentPage: 1,
      productSelect: {},
      tableLoading: true,
      productEdit: {},
      oldProductId: 0,
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
              .then((resp) => api.getUserCurrency(resp.data.address.country))

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
      this.productSelect = Object.assign({}, product);
      this.productSelect.id = this.productSelect.id.split(/-(.+)/)[1];
      this.$bvModal.show('product-card');
    },

    /**
     * route to the create product page
     */
    goToCreateProduct: function () {
      this.$router.push({path: `/businesses/${this.$route.params.id}/products/createProduct`});
    },

    /**
     * button function when clicked shows edit card
     * @param product edit product
     */
    editProduct: function (product) {
      this.productEdit = Object.assign({}, product);
      this.oldProductId = product.id;
      this.productEdit.id = this.productEdit.id.split(/-(.+)/)[1];
      this.$bvModal.show('edit-product-card');
    },

    /**
     * button function for ok when clicked calls an API
     * place holder function for API task
     */
    modifyProductAPI: async function (event) {
      event.preventDefault();

      let editData = this.productEdit
      delete editData["created"];
      await api
          .modifyProduct(this.$route.params.id, this.oldProductId, editData)
          .then((editProductResponse) => {
            this.$log.debug("Product has been edited",editProductResponse);
            this.getProducts(this.$route.params.id);
            this.$bvModal.hide('edit-product-card');
          })
          .catch((error) => {
            this.errors = [];
            this.$log.debug(error);
            if ((error.response && error.response.status === 400)) {
              this.errors.push(error.response.data);
            } else if ((error.response && error.response.status === 403)) {
              this.errors.push("Forbidden. You are not an authorized administrator");
            } else {
              this.errors.push("Server error");
            }
            console.log(error.response);
          })
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
    }
  }

}
</script>
