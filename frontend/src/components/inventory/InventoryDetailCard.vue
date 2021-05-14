<!--
Page show inventory detail card
Date: 13/5/2021
-->
<template>
  <b-card
    class="profile-card shadow"
    style="max-width: 550px"
  >
    <b-form @submit="createInventory">
      <b-img center v-bind="mainProps" rounded="circle" alt="Default Image"></b-img>
      <b-card-body>
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

        <h6><strong>Quantity *:</strong></h6>
        <b-input-group class="mb-2">
          <b-form-input type="number" maxlength="50" :disabled="disabled" v-model="inventoryInfo.quantity"
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
            v-model="inventoryInfo.pricePerItem"
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
            v-model="inventoryInfo.totalPrice"
            />
          <template #append>
            <b-input-group-text>{{ currency.code }}</b-input-group-text>
          </template>
        </b-input-group>

        <b-input-group>
          <h6><strong>Manufactured:</strong></h6>
        </b-input-group>
        <b-input-group class="mb-2">
          <b-form-input :type="(disabled)?'text':'date'" :disabled="disabled" v-model="inventoryInfo.manufactured"/>
        </b-input-group>

        <b-input-group>
          <h6><strong>Sell By:</strong></h6>
        </b-input-group>
        <b-input-group class="mb-2">
          <b-form-input :type="(disabled)?'text':'date'" :disabled="disabled" v-model="inventoryInfo.sellBy"/>
        </b-input-group>

        <b-input-group>
          <h6><strong>Best Before:</strong></h6>
        </b-input-group>
        <b-input-group class="mb-2">
          <b-form-input :type="(disabled)?'text':'date'" :disabled="disabled" v-model="inventoryInfo.bestBefore"/>
        </b-input-group>

        <b-input-group>
          <h6><strong>Expires *:</strong></h6>
        </b-input-group>
        <b-input-group class="mb-2">
          <b-form-input :type="(disabled)?'text':'date'" :disabled="disabled" v-model="inventoryInfo.expires" required/>
        </b-input-group>
        <hr style="width:100%">
        <b-button v-show="!disabled" style="float: right" variant="primary" type="submit">OK</b-button>
        <b-button style="float: right; margin-right: 1rem" variant="secondary" @click="cancelAction">Cancel</b-button>
      </b-card-body>
    </b-form>
    <b-alert :show="inventoryCardError ? 120 : 0" variant="danger">{{ inventoryCardError }}</b-alert>
  </b-card>
</template>

<script>
import api from "../../Api";

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
    editModal: {
      type: Boolean,
      default: false
    }
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
      inventoryCardError: ''
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
      if (!this.disabled) {
        this.inventoryInfo.manufactured = this.setDate(this.inventoryInfo.manufactured);
        this.inventoryInfo.sellBy = this.setDate(this.inventoryInfo.sellBy);
        this.inventoryInfo.bestBefore = this.setDate(this.inventoryInfo.bestBefore);
        this.inventoryInfo.expires = this.setDate(this.inventoryInfo.expires);
      }
    },

    /**
     * set date to a string form.
     **/
    setDate(date) {
      return new Date(date).toUTCString().split(' ').slice(0, 4).join(' ');
    },

    /**
     * calculate the Total price when the price prt item or quantity changed
     */
    calculateTotalPrice() {
      this.inventoryInfo.totalPrice =
        parseFloat((this.inventoryInfo.pricePerItem * this.inventoryInfo.quantity).toFixed(2));
    },

    async createInventory(event) {
      event.preventDefault();
      console.log(this.inventoryInfo)
      await api
          .createInventory(this.$route.params.id, this.inventoryInfo)
          .then((createInventoryResponse) => {
            this.$log.debug("Inventory Created", createInventoryResponse);
            this.$bvModal.hide('inventory-card');
          })
          .catch((error) => {
            this.inventoryCardError = error;
            this.$log.debug(error);
          });
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
