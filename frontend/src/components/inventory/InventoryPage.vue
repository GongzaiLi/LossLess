<!--
Page that stores table to show the business inventory
Date: 11/5/2021
-->
<template>
  <div class="overflow-auto">
    <b-card v-if="canEditInventory" style="max-width: 1260px;">
      <template #header>
        <b-row>
          <b-col md="8">
            <h4>Inventory: {{ businessName }}</h4>
            <h6>Click on a row to view more details</h6>
          </b-col>
          <b-col md="4" class="my-auto">
            <b-form-group class="float-right">
            <b-button @click="openCreateInventoryModal">
              <b-icon-plus-square-fill/>
              Create
            </b-button>
          </b-form-group>
          </b-col>
        </b-row>
      </template>
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
        @row-clicked="openInventoryDetailModal"
      >
        <template #cell(productThumbnail) class="thumbnail-row">
          <b-img v-bind="mainProps" thumbnail fluid style="border-radius: 10px" blank-color="#777"
                 alt="Default Image"></b-img>
        </template>
        <template v-slot:cell(actions)="product">
          <b-button id="edit-button" @click="openEditInventoryModal(product.item)" size="sm">
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
    </b-card>

    <b-card id="inventory-locked-card" v-if="!canEditInventory">
      <b-card-title>
        <b-icon-lock/>
        Can't edit inventory page
      </b-card-title>
      <h6 v-if="businessNameIfAdminOfThisBusiness"><strong>You're an administrator of this business. To edit this
        inventory, you must be acting as this business.</strong>
        <br><br>To do so, click your profile picture on top-right of the screen. Then, select the name of this business
        ('{{ businessNameIfAdminOfThisBusiness }}') from the drop-down menu.</h6>
      <h6 v-else> You are not an administrator of this business. If you need to edit this inventory, contact the
        administrators of the business. <br>
        Return to the business profile page
        <router-link :to="'/businesses/' + $route.params.id">here.</router-link>
      </h6>
    </b-card>

    <b-modal
      id="inventory-card" hide-header hide-footer
      :no-close-on-backdrop="!isInventoryCardReadOnly"
      :no-close-on-esc="!isInventoryCardReadOnly">
      <inventory-detail-card :disabled="isInventoryCardReadOnly" :currency="currency"
                             :inventory="inventoryDisplayedInCard" :edit-modal="editInventoryItem"
                             :set-up-inventory-page="setUpInventoryPage"
                             :current-business="business"/>
    </b-modal>

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
import api from "../../Api";
import pagination from '../model/Pagination';
import InventoryDetailCard from "./InventoryDetailCard";

export default {
  components: {
    InventoryDetailCard,
    pagination
  },
  data: function () {
    return {
      business: {},
      currentUser: {},
      items: [],
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
  mounted() {
    this.setUpInventoryPage();
  },
  methods: {
    /**
     * init the data which is from the inventory table
     **/
    setUpInventoryPage() {
      const businessId = this.$route.params.id
      this.getBusinessInfo(businessId);
      this.tableLoading = false;
    },
    /**
     * this is a get api to get the name of the business
     * NOTE!! Best to add currency stuff here as well similar to the products
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
     * when click the table will show a detail inventory model card.
     * @param inventory object
     **/
    openInventoryDetailModal: function (inventory) {
      this.isInventoryCardReadOnly = true;
      this.editInventoryItem = false;
      this.inventoryDisplayedInCard = Object.assign({}, inventory);
      this.$bvModal.show('inventory-card');
    },

    /**
     * when click Create button will show a create inventory model card.
     **/
    openCreateInventoryModal: function () {
      this.isInventoryCardReadOnly = false;
      this.editInventoryItem = false;
      this.inventoryDisplayedInCard = {
        productId: '',
        quantity: 0,
        pricePerItem: 0,
        totalPrice: 0,
        manufactured: null,
        sellBy: null,
        bestBefore: null,
        expires: null,
      }
      this.$bvModal.show('inventory-card');
    },

    /**
     * when click Edit button in the table will show a edit inventory model card.
     **/
    openEditInventoryModal: function (inventory) {
      this.isInventoryCardReadOnly = false;
      this.editInventoryItem = true;
      this.inventoryDisplayedInCard = Object.assign({}, inventory);
      this.inventoryDisplayedInCard.productId = inventory.product.id;
      this.$bvModal.show('inventory-card');
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
        },
        {
          key: 'actions',
          label: 'Action',
          thStyle: 'width: 8%',
          sortable: false
        }
      ]

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
    canEditInventory: function () {
      return this.$currentUser.role !== 'user' ||
        (this.$currentUser.currentlyActingAs && this.$currentUser.currentlyActingAs.id === parseInt(this.$route.params.id))
    },

    /**
     * Returns the name of the business if the user is an admin of this business, otherwise returns null
     */
    businessNameIfAdminOfThisBusiness: function () {
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
     * to access the inventory, then a modal is shown informing them that they will be redirected to the business's home page.
     */
    $currentUser: {
      handler() {
        if (this.canEditInventory) {
          this.getBusinessInfo(this.$route.params.id);
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
      if (this.canEditInventory) {
        this.getBusinessInfo(id);
      }
    },
  }
}
</script>

