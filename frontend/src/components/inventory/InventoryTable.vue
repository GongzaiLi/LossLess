<template>
<div>
  <b-table
      striped hovers
      responsive="lg"
      no-border-collapse
      bordered
      show-empty
      class="inventory-table"
      :fields="fields"
      :items="items"
      :per-page="perPage"
      :current-page="currentPage"
      :busy="tableLoading"
      ref="inventoryTable"
      @row-clicked="tableRowClick"
  >
    <template #cell(productThumbnail) class="thumbnail-row">
      <b-img v-bind="mainProps" thumbnail fluid style="border-radius: 10px" blank-color="#777"
             alt="Default Image"></b-img>
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
  <pagination v-if="items.length>0" :per-page="perPage" :total-items="totalItems" v-model="currentPage"/>
</div>
</template>

<style>
.no-results-overlay {
  margin-top: 7em;
  margin-bottom: 7em;
  text-align: center;
}

.thumbnail-row {
  padding: 0 0 0 0.5rem !important;
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
      items: [],
      business: {},
      products: [],
      perPage: 10,
      currentPage: 1,
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
     * this is a get api to get the name of the business
     * NOTE!! Best to add currency stuff here as well similar to the inventory
     * @param businessId
     */
    getBusinessInfo: async function (businessId) {
      this.tableLoading = true;
      const getProductsPromise = api.getProducts(businessId)
      const getInventoryPromise = api.getInventory(businessId)
      const currencyPromise = api.getBusiness(businessId)
          .then((resp) => {
            this.business = resp.data;
            return api.getUserCurrency(resp.data.address.country);
          })

      try {
        const [productsResponse, currency, inventoryResponse] = await Promise.all([getProductsPromise, currencyPromise, getInventoryPromise])
        this.products = productsResponse.data;
        this.items = inventoryResponse.data;
        this.tableLoading = false;
        if (currency != null) {
          this.currency = currency;
        }
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
          thStyle: 'width: 60px',
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

    /**
     * The totalResults function just computed how many pages in the search table.
     * @returns number
     */
    totalItems: function () {
      return this.items.length;
    },

  },
}
</script>

