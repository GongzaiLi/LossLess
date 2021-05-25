<!--
Page Show create listing card, validate data, send api request to create listing.
Date: 23/5/2021
-->

<template>
  <div>
    <b-card
        class="profile-card shadow"
    >
      <b-form @submit.prevent="createListing">
        <div :hidden="!disabled">
          <b-img center v-bind="mainProps" rounded="circle" alt="Default Image"></b-img>
        </div>
        <b-card-body>
          <h6 class="mb-2"><strong>Inventory Item ID *:</strong></h6>
          <p :hidden="disabled" style="margin:0">
            Enter the ID of a product in your inventory, or select a product using the 'Select Product' button.<br></p>
          <b-input-group class="mb-2">
            <b-form-input
                type="text" maxlength="50"
                pattern="[a-zA-Z0-9\d\-_\s]{0,100}"
                :disabled="disabled"
                placeholder="Inventory-Item-ID"
                v-model="listingData.inventoryItemId"
                autofocus required/>
            <b-input-group-append v-if="!disabled">
              <b-button variant="outline-primary" @click="openSelectInventoryItemModal">Select Inventory Item</b-button>
            </b-input-group-append>
          </b-input-group>

          <h6><strong>Quantity *:</strong></h6>
          <b-input-group class="mb-2">
            <b-form-input type="number" maxlength="50" :min="1" :disabled="disabled" v-model="listingData.quantity"
                          @input="calculateTotalPrice" required/>
          </b-input-group>

          <b-input-group>
            <h6><strong>Total Price:</strong></h6>
          </b-input-group>
          <b-input-group class="mb-2">
            <template #prepend>
              <b-input-group-text>{{ currency.symbol }}</b-input-group-text>
            </template>
            <b-form-input
                type="number" maxlength="15"
                step=".01" min=0 placeholder=0
                :disabled="disabled"
                v-model="listingData.price"
            />
            <template #append>
              <b-input-group-text>{{ currency.code }}</b-input-group-text>
            </template>
          </b-input-group>

          <b-input-group>
            <h6><strong>More Info:</strong></h6>
          </b-input-group>
          <b-input-group class="mb-2">
            <b-form-input type="text"
                          :disabled="disabled"
                          autocomplete="off"
                          v-model="listingData.moreInfo"/>
          </b-input-group>

          <b-input-group>
            <h6><strong>Closes:</strong></h6>
          </b-input-group>
          <b-input-group class="mb-2">
            <b-form-input :type="(disabled)?'text':'date'"
                          :disabled="disabled"
                          autocomplete="off"
                          :min="getToday()"
                          v-model="listingData.closes"/>
          </b-input-group>

          <b-alert :show="listingCardError ? 120 : 0" variant="danger">{{ listingCardError }}</b-alert>
          <hr style="width:100%">
          <b-button v-show="!disabled" style="float: right" variant="primary" type="submit">OK</b-button>
          <b-button style="float: right; margin-right: 1rem" variant="secondary" @click="cancelAction">Cancel</b-button>
        </b-card-body>
      </b-form>
    </b-card>

    <b-modal
        id="select-inventory-item" hide-footer title="Select an inventory item"
        static> <!--We make the modal static so that it is always rendered immediately on page load. If we remove this, the modal will
       only render when opened. This causes issues as the products-table component will only be rendered after the modal opens,
       so we wouldn't be able to call it with $refs-->
      <div style="text-align: center" class="mb-3">
        <h5>Click on the inventory item you want to create a listing for</h5>
      </div>
      <inventory-table ref="inventoryTable" :editable="false"
                      v-on:inventoryItemClicked="selectInventoryItem"
      />
    </b-modal>
  </div>
</template>


<style>


#select-inventory-item > .modal-dialog {
  max-width: 1250px;
}

</style>

<script>

import api from '../../Api';

import InventoryTable from "../inventory/InventoryTable";

export default {
name: "add-listing-card",
  components: {InventoryTable},
  props: {
    currentBusiness: {
      type: Object,
    },
    disabled: {
      type: Boolean,
      default: true
    },
    currency: {
      type: Object,
      default: () => ({
        symbol: '$',
        code: 'USD',
        name: 'US Dollar'
      })
    },
    editModal: {
      type: Boolean,
      default: false
    },
  },
  data() {
    return {
      mainProps: {blank: true, blankColor: '#777', width: 150, height: 150, class: 'm1'},
      listingData: {
        inventoryItemId: '',
        quantity: 1,
        price: 0,
        moreInfo: "",
        closes: null
      },
      selectedInventoryItem: null,
      listingCardError: "",
      listingId: null
    }
  },

  mounted() {
    this.setUpListingCard();
  },

  methods: {

    /**
     * Makes a request to the API to create a Listing with the form input.
     * Then, will hide the create Listing popup
     */
    async createListing() {

      if (this.listingData.quantity == null || this.listingData.quantity <= 0) {
        this.listingCardError = "Quantity must be more than zero"
        return
      }

      if (this.listingData.price == null || this.listingData.price < 0) {
        this.listingCardError = "price must be greater than or equal to zero"
        return
      }


      let listingRequest = {
        inventoryItemId: this.listingData.inventoryItemId,
        quantity: this.listingData.quantity,
        price: this.listingData.price,
        moreInfo: this.listingData.moreInfo,
        closes: this.listingData.closes
      };
      // this.listingData = listingRequest;
      await api.createListing(this.$route.params.id, listingRequest)
          .then((listingResponse) => {
            this.$log.debug("Listing Created", listingResponse);
            this.listingId = listingResponse.data.listingId;
            this.$bvModal.hide('add-listing-card');
            this.$emit('itemCreated');
          })
          .catch((error) => {
            this.$log.debug(error);
            this.listingCardError = this.getErrorMessageFromApiError(error);
          })

    },
    /**
     * Given an error thrown by a rejected axios (api) request, returns a user-friendly string
     * describing that error. Only applies to POST and PUT requests for Listing
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
    setUpListingCard() {
    },

    setDate(date) {
      if (date != null) {
        return new Date(date).toUTCString().split(' ').slice(0, 4).join(' ')
      }
    },

    /**
     * get today's date without the time
     * need to add one to get correct date
     * @return today's date in format yyyy-mm-dd
     **/
    getToday() {
      let date = new Date();
      return date.getFullYear() + "-" + (date.getMonth() + 1).toString().padStart(2, '0') + '-' + date.getDate().toString().padStart(2, '0');
    },


    calculateTotalPrice() {
      if (this.selectedInventoryItem != null) {
        this.listingData.price =
            parseFloat((this.selectedInventoryItem.pricePerItem * this.listingData.quantity).toFixed(2));
      } else {
        this.listingData.price = 0.00;
      }
    },

    cancelAction() {
      this.listingCardError = "";
      this.$bvModal.hide('add-listing-card');
    },

    openSelectInventoryItemModal() {
      this.$bvModal.show('select-inventory-item');
      // this.$refs.inventoryTable.getInventory(this.currentBusiness);
    },

    selectInventoryItem(inventory) {
      this.selectedInventoryItem = inventory;
      this.listingData.inventoryItemId = inventory.product.id;
      this.calculateTotalPrice();
      this.$bvModal.hide('select-inventory-item');
    },
  },
}


</script>