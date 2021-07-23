<template>
  <div>
    <b-table
        striped hovers
        responsive="true"
        no-border-collapse
        bordered
        show-empty
        no-local-sorting
        :sort-by.sync="sortBy"
        :sort-desc.sync="sortDesc"
        @row-clicked="tableRowClick"
        class="catalogue-table"
        :fields="fields"
        :items="items"
        :per-page="perPage"
        ref="productCatalogueTable"
    >
      <template #cell(thumbnail)="products">
        <div v-if="!products.item.images.length">
          <b-img  class="product-image-thumbnail" center :src="require(`/public/product_default_thumbnail.png`)" alt="Product has no image"/>
        </div>
        <div v-if="products.item.images.length">
          <img class="product-image-thumbnail" center :src="getThumbnail(products.item)" alt="Failed to load image"/>
        </div>
      </template>

      <template v-slot:cell(actions)="products">
        <b-button id="edit-button" @click="editButtonClicked(products.item)" size="sm">
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
    <pagination v-if="totalItems>0" :per-page="perPage" :total-items="totalItems" v-model="currentPage"/>
  </div>
</template>


<style>

</style>

<script>
import pagination from "../model/Pagination";
import api from "../../Api";

export default {
  name: "ProductsTable",
  props: ['editable'],
  components: {
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
      sortDesc: false,
      sortBy: "",
      items: [],
      totalItems: 0,
      perPage: 10,
      currentPage: 1,
      currentUser: {},
      business: null
    }
  },
  methods: {
    /**
     * Given a business's id, fills/updates the product table with the products of the business's catalogue.
     * This will also make network requests to a currency api in order to get the currency of the business.
     *
     * Uses perPage and currentPage to determine the correct subset of products to retrieve for the table.
     *
     * This component will not load items when mounted so you have to call this manually.
     * This saves us having to pass in a business id prop.
     */
    getProducts: async function (business) {
      this.business = business;

      let sortDirectionString = "ASC"
      if (this.sortDesc) {
        sortDirectionString = "DESC"
      }

      let sortByParam;
      switch (this.sortBy) {
        case "id":
          sortByParam = "ID";
          break;

        case "name":
          sortByParam = "NAME";
          break;

        case "description":
          sortByParam = "DESCRIPTION";
          break;

        case "manufacturer":
          sortByParam = "MANUFACTURER";
          break;

        case "recommendedRetailPrice":
          sortByParam = "RRP";
          break;

        case "created":
          sortByParam = "CREATED";
          break;

        default:
          sortByParam = "ID";
      }

      const getProductsPromise = api.getProducts(business.id, this.perPage, this.currentPage - 1, sortByParam, sortDirectionString);  // Promise for getting products
      const getCurrencyPromise = api.getUserCurrency(business.address.country); // Promise for getting the currency data

      try {
        const [productsResponse, currency] = await Promise.all([getProductsPromise, getCurrencyPromise]) // Run promises in parallel for lower latency

        this.items = productsResponse.data.products;
        this.totalItems = productsResponse.data.totalItems;

        this.$refs.productCatalogueTable.refresh();


        if(currency != null){
          this.currency = currency;
        }
      } catch(error) {
        this.$log.debug(error);
      }
    },

    tableRowClick(product) {
      this.$emit('productClicked', product)
    },

    editButtonClicked(product) {
      this.$emit('editProductClicked', product)
    },

    /**
     * modify the description only keep 20 characters and then add ...
     * @param description
     * @return string
     */
    setDescription(description) {
      if (description === "" || description.length <= 10) {
        return description.trim();
      }
      const showTenWordDescription = description.slice(0, 10).trim();
      return `${showTenWordDescription}${showTenWordDescription.endsWith('.') ? '..' : '...'}`;
    },

    /**
     * Takes a product as input and returns the primary image thumbnail for that product
     * @param product   The product that's thumbnail is being requested
     * @return image    The primary image thumbnail
     **/
    getThumbnail: function (product) {
      const primaryImageFileName = product.primaryImage.thumbnailFilename;
      return api.getImage(primaryImageFileName);
    }
  },

  computed: {

    /**
     * Fields passed into b-table, defines which columns the table should have.
     * Will not have the 'action' column if the 'editable' prop is false
     */
    fields: function () {
      let fieldsList = [
        {
          key: 'thumbnail',
          label: 'Image',
          tdClass: 'thumbnail-row', // Class to make the padding around the thumbnail smaller
        },

        {
          key: 'id',
          label: 'ID',
          formatter: (value) => {
            return value.split(/-(.+)/)[1];
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
          formatter: 'setDescription',
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
          sortable: true
        }];
      if (this.editable) {
        fieldsList.push({
          key: 'actions',
          label: 'Action',
          sortable: false
        })
      }
      return fieldsList;
    },

  },


  watch: {
    /**
     * Watch for current page change, refresh products when it happens.
     */
    '$data.currentPage': {
      handler: function() {
        this.getProducts(this.business);
      },
      deep: true
    },
    /**
     * Watch for sortBy change, refresh products when it happens.
     */
    '$data.sortBy': {
      handler: function() {
        this.getProducts(this.business);
      },
      deep: true
    },
    /**
     * Watch for sortDesc change, refresh products when it happens.
     */
    '$data.sortDesc': {
      handler: function() {
        this.getProducts(this.business);
      },
      deep: true
    }
  }
}
</script>

<style>

.no-results-overlay {
  margin-top: 7em;
  margin-bottom: 7em;
  text-align: center;
}

.catalogue-table td {
  cursor: pointer;
}
</style>
