<template>
  <div>
    <b-card
        class="profile-card shadow"
    >
      <b-form @submit="createListing">
        <div :hidden="!disabled">
          <b-img center v-bind="mainProps" rounded="circle" alt="Default Image"></b-img>
        </div>
        <b-card-body>
          <h6 class="mb-2"><strong>Product Id *:</strong></h6>
          <p :hidden="disabled" style="margin:0">
            Enter the ID of a product in your inventory, or select a product using the 'Select Product' button.<br></p>
          <b-input-group class="mb-2">
            <b-form-input
                type="text" maxlength="50"
                pattern="[a-zA-Z0-9\d\-_\s]{0,100}"
                :disabled="disabled"
                placeholder="PRODUCT-ID"
                v-model="listingInfo.productId"
                autofocus required/>
            <b-input-group-append v-if="!disabled">
              <b-button variant="outline-primary" @click="openSelectInventoryItemModal">Select Inventory Item</b-button>
            </b-input-group-append>
          </b-input-group>

          <h6><strong>Quantity *:</strong></h6>
          <b-input-group class="mb-2">
            <b-form-input type="number" maxlength="50" :disabled="disabled" v-model="listingInfo.quantity"
                          @input="calculateTotalPrice" required/>
          </b-input-group>

          <b-input-group>
            <h6><strong>Price Per Item:</strong></h6>
          </b-input-group>
          <b-input-group class="mb-2">
            <template #prepend>
              <b-input-group-text>{{ currency.symbol }}</b-input-group-text>
            </template>
            <b-form-input
                type="number" maxlength="15"
                step=".01" min=0 placeholder=0
                :disabled="disabled"
                @input="calculateTotalPrice"
                v-model="listingInfo.pricePerItem"
            />
            <template #append>
              <b-input-group-text>{{ currency.code }}</b-input-group-text>
            </template>
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
                v-model="listingInfo.totalPrice"
            />
            <template #append>
              <b-input-group-text>{{ currency.code }}</b-input-group-text>
            </template>
          </b-input-group>

          <b-input-group>
            <h6><strong>Manufactured:</strong></h6>
          </b-input-group>
          <b-input-group class="mb-2">
            <b-form-input :type="(disabled)?'text':'date'"
                          :disabled="disabled"
                          autocomplete="off"
                          v-model="listingInfo.manufactured"/>
          </b-input-group>

          <b-input-group>
            <h6><strong>Sell By:</strong></h6>
          </b-input-group>
          <b-input-group class="mb-2">
            <b-form-input :type="(disabled)?'text':'date'"
                          :disabled="disabled"
                          autocomplete="off"
                          v-model="listingInfo.sellBy"/>
          </b-input-group>

          <b-input-group>
            <h6><strong>Best Before:</strong></h6>
          </b-input-group>
          <b-input-group class="mb-2">
            <b-form-input :type="(disabled)?'text':'date'"
                          :disabled="disabled"
                          autocomplete="off"
                          v-model="listingInfo.bestBefore"/>
          </b-input-group>

          <b-input-group>
            <h6><strong>Expires *:</strong></h6>
          </b-input-group>
          <b-input-group class="mb-2">
            <b-form-input :type="(disabled)?'text':'date'"
                          :disabled="disabled"
                          autocomplete="off"
                          v-model="listingInfo.expires" required/>
          </b-input-group>
          <b-alert :show="listingCardError ? 120 : 0" variant="danger">{{ listingCardError }}</b-alert>
          <hr style="width:100%">
          <b-button v-show="!disabled" style="float: right" variant="primary" type="submit">OK</b-button>
          <b-button style="float: right; margin-right: 1rem" variant="secondary" @click="cancelAction">Cancel</b-button>
        </b-card-body>
      </b-form>
    </b-card>

    <b-modal
        id="select-products" hide-footer title="Select a product"
        static> <!--We make the modal static so that it is always rendered immediately on page load. If we remove this, the modal will
       only render when opened. This causes issues as the products-table component will only be rendered after the modal opens,
       so we wouldn't be able to call it with $refs-->
      <div style="text-align: center" class="mb-3">
        <h5>Click on the inventory item you want to create an listing item for</h5>
      </div>
      <products-table ref="productTable" :editable="false"
                      v-on:productClicked="selectInventoryItem"
      ></products-table>
    </b-modal>
  </div>
</template>

<script>


export default {
name: "add-listing-card",
  //components: {InventoryTable}, (Needs to be moved into it's own component?)
  props: {
    currentBusiness: {
      type: Object,
    },
    disabled: {
      type: Boolean,
      default: true
    },
    inventory: {
      type: Object,
      default: () => ({})
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
    setUpInventoryPage: {
      type: Function,
    }

  },
  data() {
    return {
      mainProps: { blank: true, blankColor: '#777', width: 150, height: 150, class: 'm1' },
      listingInfo: {},
      listingCardError: ""
    }
  },

  mounted() {
    this.setUpListingCard();
  },

  methods: {
    async createListing() {},
    async getErrorMessageFromApiError() {},
    setUpListingCard() {},

    setDate(date) {
      if (date != null)  {return new Date(date).toUTCString().split(' ').slice(0, 4).join(' ')}
    },

    calculateTotalPrice() {
      this.listingInfo.totalPrice =
          parseFloat((this.listingInfo.pricePerItem * this.listingInfo.quantity).toFixed(2));
    },

    cancelAction() {
      this.listingCardError = "";
      this.$bvModal.hide('add-listing-card');
    },

    openSelectInventoryItemModal() {},

    selectInventoryItem() {},
  },
}


</script>

<style scoped>

</style>