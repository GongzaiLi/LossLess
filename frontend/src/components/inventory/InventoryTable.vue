<template>
<div>
  <div style="margin-left: 15%; margin-right: 15%">
    <div>
      <b-form @submit.prevent="searchClicked">
        <b-input-group prepend="Filter by product code:">
          <b-form-input v-model="searchQuery"></b-form-input>
          <b-input-group-append>
            <b-button type="submit"> Filter </b-button>
          </b-input-group-append>
        </b-input-group>
      </b-form>
    </div>

  </div>
  <br>
  <b-table
      striped hovers
      responsive="lg"
      no-border-collapse
      bordered
      show-empty
      no-local-sorting
      :sort-by.sync="sortBy"
      :sort-desc.sync="sortDesc"
      class="inventory-table"
      :fields="fields"
      :items="items"
      :per-page="perPage"
      :busy="tableLoading"
      ref="inventoryTable"
      @row-clicked="tableRowClick"
  >
    <template v-slot:cell(productThumbnail)="data" class="thumbnail-row">
      <b-img v-if="!data.item.product.primaryImage" center class="product-image-thumbnail" :src="require(`/public/product_default.png`)" alt="Product has no image" />
      <b-img v-if="data.item.product.primaryImage" center class="product-image-thumbnail" :src=getThumbnail(data.item.product) />
    </template>

    <template v-slot:cell(actions)="product">
      <b-button id="edit-button" @click="editButtonClicked(product.item)" size="sm">
        Edit
      </b-button>
    </template>

    <template #cell(pricePerItem)="data">
      {{ currency.symbol }}{{ data.item.pricePerItem }}
    </template>
    <template #cell(totalPrice)="data">
      {{ currency.symbol }}{{ data.item.totalPrice }}
    </template>

    <template #empty>
      <div class="no-results-overlay">
        <h4>No Inventory to display</h4>
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
.no-results-overlay {
  margin-top: 7em;
  margin-bottom: 7em;
  text-align: center;
}

.product-image-thumbnail {
  height: 60px !important;
  object-fit: cover;
  max-width: 80px;
  padding: 0.25rem;
  border-radius: 0.5rem;
}

.thumbnail-row {
  padding: 0 !important;
}
</style>

<script>
import pagination from "../model/Pagination";
import api from "../../Api";

export default {
  components: {
    pagination,
  },
  props: ['editable'],
  data: function () {
    return {
      searchQuery:'',
      items: [],
      business: {},
      perPage: 10,
      currentPage: 1,
      sortDesc: false,
      sortBy: "",
      totalItems: 0,
      tableLoading: true,
      currency: {
        symbol: '$',
        code: 'USD',
        name: 'US Dollar',
      },
      mainProps: {blank: true, width: 50, height: 50},
      isInventoryCardReadOnly: true,
      inventoryDisplayedInCard: {},
      editInventoryItem: false,
    }
  },

  methods: {
    /**
     *  Gets the inventory items based on search query
     */
    searchClicked() {
      this.getInventoryInfo(this.$route.params.id)
    },

    /**
     * this is a get api to get the name of the business
     * NOTE!! Best to add currency stuff here as well similar to the inventory
     * @param businessId
     */
    getInventoryInfo: async function (businessId) {
      let sortDirectionString = "ASC"
      if (this.sortDesc) {
        sortDirectionString = "DESC"
      }

      let sortByParam = this.sortBy;
      if (sortByParam === "product") {
        sortByParam = "product.id";
      }

      const getInventoryPromise = api.getInventory(businessId, this.perPage, this.currentPage-1, sortByParam, sortDirectionString, this.searchQuery);
      const currencyPromise = api.getBusiness(businessId)
          .then((resp) => {
            this.business = resp.data;
            return api.getUserCurrency(resp.data.address.country);
          })
      try {
        const [currency, inventoryResponse] = await Promise.all([currencyPromise, getInventoryPromise])

        this.items = inventoryResponse.data.inventory;

        this.totalItems = inventoryResponse.data.totalItems;

        if (currency !== null) {
          this.currency = currency;
        }
        this.$refs.inventoryTable.refresh();
      } catch (error) {
        this.$log.debug(error);
      }
    },

    tableRowClick(product) {
      this.$emit('inventoryItemClicked', product)
    },

    editButtonClicked(product) {
      this.$emit('editInventoryItemClicked', product)
    },

    /**
     * modify the ID so that it doesn't display the "{businessId}-" at the start
     * @return string
     */
    setId: function (product) {
      return product.id.split(/-(.+)/)[1];
    },

    /**
     * modify the date to day/month/year.
     * @param oldDate
     * @return string
     */
    setDate: function (oldDate) {
      if (oldDate != null) {
        const date = new Date(oldDate);
        return `${date.getUTCDate() > 9 ? '' : '0'}${date.getUTCDate()}/` +
            `${date.getUTCMonth() + 1 > 9 ? '' : '0'}${date.getUTCMonth() + 1}/` +
            `${date.getUTCFullYear()}`;
      }
    },

    /**
     * Uses the product of the inventory and returns the thumbnail of the primary image for that product.
     * @param  product a product that's image is being requested
     * @return string
     **/
    getThumbnail: function (product) {
      if (product.primaryImage) {
        const thumbnailFilename = product.primaryImage.thumbnailFilename;
        return api.getImage(thumbnailFilename);
      }
    }
  },

  async mounted() {
    const businessId = this.$route.params.id;
    await this.getInventoryInfo(businessId);
    this.tableLoading = false;
  },

  computed: {
    /**
     * set table parameter
     * @returns object
     */
    fields: function () {
      let fieldsList = [
        {
          key: 'productThumbnail',
          tdClass: 'thumbnail-row', // Class to make the padding around the thumbnail smaller
          label: 'Thumbnail',
          thStyle: 'width: 80px;',
        },
        {
          key: 'product',
          label: 'Product Code',
          formatter: (value) => {
            return this.setId(value);
          },
          sortable: true
        },
        {
          key: 'quantity',
          label: 'Quantity',
          sortable: true
        },
        {
          key: 'quantityRemaining',
          label: 'Quantity Remaining',
          sortable: true
        },
        {
          key: 'pricePerItem',
          label: `Price (${this.currency.code})`,
          sortable: true
        },
        {
          key: 'totalPrice',
          label: `Total (${this.currency.code})`,
          sortable: true
        },
        {
          key: 'manufactured',
          label: 'Manufacture Date',
          formatter: (value) => {
            return this.setDate(value);
          },
          thStyle: 'width: 15%',
          sortable: true
        },
        {
          key: 'sellBy',
          label: 'Sell by Date',
          formatter: (value) => {
            return this.setDate(value);
          },
          thStyle: 'width: 15%',
          sortable: true
        },
        {
          key: 'bestBefore',
          label: 'Best Before Date',
          formatter: (value) => {
            return this.setDate(value);
          },
          thStyle: 'width: 15%',
          sortable: true
        },
        {
          key: 'expires',
          label: 'Expiry Date',
          formatter: (value) => {
            return this.setDate(value);
          },
          thStyle: 'width: 15%',
          sortable: true
        }];
      if (this.editable) {
        fieldsList.push({
          key: 'actions',
          label: 'Action',
          sortable: false
        })
      }
      return fieldsList
    },
  },
  watch: {
    /**
     * Watch for current page change, refresh inventory when it happens
     */
    '$data.currentPage': {
      handler: function () {
        this.getInventoryInfo(this.business.id);
      },
      deep: true
    },
    /**
     * Watch for sortBy change, refresh inventory when it happens.
     */
    '$data.sortBy': {
      handler: function () {
        this.getInventoryInfo(this.business.id);
      },
      deep: true
    },
    /**
     * Watch for sortDesc change, refresh inventory when it happens.
     */
    '$data.sortDesc': {
      handler: function () {
        this.getInventoryInfo(this.business.id);
      },
      deep: true
    },
  }
}
</script>

