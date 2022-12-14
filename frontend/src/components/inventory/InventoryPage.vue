<!--
Page that stores table to show the business inventory
Date: 11/5/2021
-->
<template>
  <div>
    <b-card v-if="canEditInventory" style="max-width: initial;" class="shadow">
      <b-card-title>Inventory: {{ business.name }}</b-card-title>
      <hr class='m-0'>
      <b-row align-v="center">
        <b-col md="8"><h6 class="ml-2">Click on an inventory to view more details</h6></b-col>
        <b-col md="4">
          <b-form-group>
            <b-button @click="openCreateInventoryModal" class="float-right">
              <b-icon-plus-square-fill/>
              Create
            </b-button>
          </b-form-group>
        </b-col>
      </b-row>
      <inventory-table ref="inventoryTable" :editable="true"
                       v-on:inventoryItemClicked="openInventoryDetailModal"
                       v-on:editInventoryItemClicked="openEditInventoryModal"></inventory-table>
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
      <inventory-detail-card :disabled="isInventoryCardReadOnly"
                             :currency="currency"
                             :inventory="inventoryDisplayedInCard"
                             :edit-modal="editInventoryItem"
                             :current-business="business"
                             :set-up-inventory-page="refreshTable"/>
    </b-modal>

  </div>
</template>

<script>
import InventoryTable from './InventoryTable';
import InventoryDetailCard from "./InventoryDetailCard";
import api from "../../Api";

export default {
  components: {
    InventoryDetailCard,
    InventoryTable
  },
  data: function () {
    return {
      business: {},
      currentUser: {},
      currency: {
        symbol: '$',
        code: 'USD',
        name: 'US Dollar',
      },
      isInventoryCardReadOnly: true,
      inventoryDisplayedInCard: {},
      editInventoryItem: false,
    }
  },
  mounted() {
    this.getBusinessInfo();
  },
  methods: {
    /**
     * refresh the inventory table
     **/
    refreshTable: async function() {
      const businessId = this.$route.params.id;
      await this.$refs.inventoryTable.getInventoryInfo(businessId);
    },
    /**
     * call api read the business information
     **/
    async getBusinessInfo() {
      const businessId = this.$route.params.id;
      api.getBusiness(businessId)
          .then((resp) => {
            this.business = resp.data;
            return api.getUserCurrency(resp.data.address.country);
          })
          .then(currencyData => this.currency = currencyData)
          .catch((error) => {
            this.$log.debug(error);
          })
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
     * when click Edit button in the table will show an edit inventory model card.
     **/
    openEditInventoryModal: function (inventory) {
      this.isInventoryCardReadOnly = false;
      this.editInventoryItem = true;
      this.inventoryDisplayedInCard = Object.assign({}, inventory);
      this.inventoryDisplayedInCard.productId = inventory.product.id;
      this.$bvModal.show('inventory-card');
    },

  },

  computed: {

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
          this.getBusinessInfo();
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
        this.setUpInventoryPage(id);
      }
    },
  }
}
</script>

