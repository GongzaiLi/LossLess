<!--
Page show inventory detail card
Date: 13/5/2021
-->
<template>
  <b-card
    class="profile-card shadow"
    style="max-width: 550px"
  >
    <b-form @submit="okAction">
      <b-card-body>
        <b-img class="mb-2" center v-bind="mainProps" rounded="circle" alt="Default Image"></b-img>
        <h6 class="mb-2"><strong>Product Id *:</strong></h6>
        <p :hidden="disabled" style="margin:0">
          Ensure there are no special characters (e.g. "/","?").
          <br>
          This will be automatically changed into the correct format.</p>

        <b-input-group class="mb-2">
          <b-form-input
            type="text" maxlength="50"
            pattern="[a-zA-Z0-9\d\-_\s]{0,100}"
            :disabled="disabled"
            placeholder="PRODUCT-ID"
            v-model="inventoryInfo.productId"
            autofocus required/>
        </b-input-group>

        <h6><strong>Quantity: </strong></h6>
        <b-input-group class="mb-2">
          <b-form-input type="number" maxlength="50" disabled v-model="inventoryInfo.quantity"
                        @input="calculateTotalPrice"/>
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
            step=".01" min=0
            :disabled="disabled"
            v-model="inventoryInfo.pricePerItem"
            required/>
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
            step=".01" min=0
            :disabled="disabled"
            v-model="inventoryInfo.totalPrice"
            required/>
          <template #append>
            <b-input-group-text>{{ currency.code }}</b-input-group-text>
          </template>
        </b-input-group>

        <b-input-group>
          <h6><strong>Manufactured:</strong></h6>
        </b-input-group>
        <b-input-group class="mb-2">
          <b-form-input type="text" disabled v-model="inventoryInfo.manufactured"/>
        </b-input-group>

        <b-input-group>
          <h6><strong>Sell By:</strong></h6>
        </b-input-group>
        <b-input-group class="mb-2">
          <b-form-input type="text" disabled v-model="inventoryInfo.sellBy"/>
        </b-input-group>

        <b-input-group>
          <h6><strong>Best Before:</strong></h6>
        </b-input-group>
        <b-input-group class="mb-2">
          <b-form-input type="text" disabled v-model="inventoryInfo.bestBefore"/>
        </b-input-group>

        <b-input-group>
          <h6><strong>Expires:</strong></h6>
        </b-input-group>
        <b-input-group class="mb-2">
          <b-form-input type="text" disabled v-model="inventoryInfo.expires"/>
        </b-input-group>

      </b-card-body>

      <hr style="width:100%">
      <div>
        <b-button v-show="!disabled" style="float: right" variant="primary" type="submit">OK</b-button>
        <b-button style="float: right; margin-right: 1rem" variant="secondary" @click="cancelAction">Cancel</b-button>
      </div>
    </b-form>
  </b-card>
</template>

<script>
export default {
  name: "inventory-detail-card",
  props: {
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
  },
  data() {
    return {
      mainProps: { blank: true, blankColor: '#777', width: 150, height: 150, class: 'm1' },
      inventoryInfo: {
        productId: "WATT-420-BEANS",//
        quantity: 3,
        pricePerItem: 2.00,
        totalPrice: 6,
        manufactured: "2021-05-13",
        sellBy: "2021-05-13",
        bestBefore: "2021-05-13",
        expires: "2021-05-13"
      },
    }
  },
  mounted() {
    this.setUpInventoryCard();
  },
  methods: {
    /**
     * init the data which is from the inventory table
     */
    setUpInventoryCard() {
      this.inventoryInfo = this.inventory;
      this.inventoryInfo.manufactured = new Date(this.inventoryInfo.manufactured).toUTCString();
      this.inventoryInfo.sellBy = new Date(this.inventoryInfo.sellBy).toUTCString();
      this.inventoryInfo.bestBefore = new Date(this.inventoryInfo.bestBefore).toUTCString();
      this.inventoryInfo.expires = new Date(this.inventoryInfo.expires).toUTCString();
    },
    /**
     * calculate the Total price when the price prt item or quantity changed
     */
    calculateTotalPrice() {
      this.inventoryInfo.totalPrice = (this.inventoryInfo.pricePerItem * this.inventoryInfo.quantity).toFixed(2);
    },
    /**
     * when click Ok button the modal will hide.
     */
    okAction() {
      this.$bvModal.hide('inventory-card');
    },

    /**
     * when click Cancel button the modal will hide.
     */
    cancelAction() {
      this.$bvModal.hide('inventory-card');
    }
  }
}
</script>

<style scoped>

</style>
