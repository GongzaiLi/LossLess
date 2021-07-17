<!--
Page show inventory detail card
Date: 13/5/2021
-->
<template>
  <div>
    <b-card
      class="profile-card shadow"
    >
      <b-form @submit.prevent="okAction">
        <div :hidden="!disabled" v-if="!loadingData && inventoryInfo.product">
          <b-carousel
              v-if="inventoryInfo.product.images.length && !loadingData"
              id="carousel-1"
              controls
              indicators
              class="mb-2"
              :interval="0"
              ref="image_carousel"
          >
            <b-carousel-slide v-for="image in inventoryInfo.product.images" :key="image.id">
              <template #img>
                <img
                    class="product-image d-block w-100 rounded"
                    alt="Product image"
                    :src="getImage(image.fileName)"
                >
              </template>
            </b-carousel-slide>
          </b-carousel>
          <b-img class="product-image d-block w-100 rounded" v-else center :src="require(`/public/product_default.png`)" alt="Product has no image"/>
        </div>

        <b-card-body>
          <h6 class="mb-2"><strong>Product Id *:</strong></h6>
          <p :hidden="disabled" style="margin:0">
            Enter the ID of a product in your catalogue, or select a product using the 'Select Product' button.<br></p>
          <b-input-group class="mb-2">
            <b-form-input
              type="text" maxlength="50"
              pattern="[a-zA-Z0-9\d\-_\s]{0,100}"
              :disabled="disabled"
              placeholder="PRODUCT-ID"
              v-model="inventoryInfo.displayedProductId"
              autofocus required/>
            <b-input-group-append v-if="!disabled">
              <b-button variant="outline-primary" @click="openSelectProductModal">Select Product</b-button>
            </b-input-group-append>
          </b-input-group>

          <h6><strong>Quantity *:</strong></h6>
          <b-input-group class="mb-2">
            <b-form-input type="number" min='1' max='1000000000' maxlength="50" :disabled="disabled" v-model="inventoryInfo.quantity"
                          @input="calculateTotalPrice" required/>
          </b-input-group>

          <b-input-group>
            <h6><strong>Price Per Item: *</strong></h6>
          </b-input-group>
          <b-input-group class="mb-2">
            <template #prepend>
              <b-input-group-text>{{ currency.symbol }}</b-input-group-text>
            </template>
            <b-form-input
              type="number" maxlength="15" max="1000000000"
              step=".01" min=0 placeholder=0
              :disabled="disabled"
              @input="calculateTotalPrice"
              v-model="inventoryInfo.pricePerItem"
              required
            />
            <template #append>
              <b-input-group-text>{{ currency.code }}</b-input-group-text>
            </template>
          </b-input-group>

          <b-input-group>
            <h6><strong>Total Price: *</strong></h6>
          </b-input-group>
          <b-input-group class="mb-2">
            <template #prepend>
              <b-input-group-text>{{ currency.symbol }}</b-input-group-text>
            </template>
            <b-form-input
              type="number" maxlength="15" max="1000000000"
              step=".01" min=0 placeholder=0
              :disabled="disabled"
              v-model="inventoryInfo.totalPrice"
              required
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
                          :max="getToday()"
                          v-model="inventoryInfo.manufactured"/>
          </b-input-group>

          <b-input-group>
            <h6><strong>Sell By:</strong></h6>
          </b-input-group>
          <b-input-group class="mb-2">
            <b-form-input :type="(disabled)?'text':'date'"
                          :disabled="disabled"
                          autocomplete="off"
                          :min="getToday()"
                          v-model="inventoryInfo.sellBy"/>
          </b-input-group>

          <b-input-group>
            <h6><strong>Best Before:</strong></h6>
          </b-input-group>
          <b-input-group class="mb-2">
            <b-form-input :type="(disabled)?'text':'date'"
                          :disabled="disabled"
                          autocomplete="off"
                          :min="getToday()"
                          v-model="inventoryInfo.bestBefore"/>
          </b-input-group>

          <b-input-group>
            <h6><strong>Expires *:</strong></h6>
          </b-input-group>
          <b-input-group class="mb-2">
            <b-form-input :type="(disabled)?'text':'date'"
                          :disabled="disabled"
                          autocomplete="off"
                          :min="getToday()"
                          v-model="inventoryInfo.expires" required/>
          </b-input-group>
          <transition name="fade">
            <b-alert v-model="showErrorAlert" variant="danger" dismissible>{{ inventoryCardError }}</b-alert>
          </transition>
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
        <h5>Click on the product you want to create an inventory item for</h5>
      </div>
      <products-table ref="productTable" :editable="false"
                      v-on:productClicked="selectProduct"
      ></products-table>
    </b-modal>
  </div>
</template>

<style>

.fade-enter-active, .fade-leave-active {
  transition: opacity .5s;
}
.fade-enter, .fade-leave-to /* .fade-leave-active below version 2.1.8 */ {
  opacity: 0;
}

.detail_card_image {
  width: 250px;
  height: 250px;
}

#select-products > .modal-dialog {
  max-width: 1000px;
}

</style>

<script>
import api from "../../Api";
import ProductsTable from "../product/ProductsTable";

export default {
  name: "inventory-detail-card",
  components: {ProductsTable},
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
      inventoryInfo: {},
      inventoryCardError: "",
      showErrorAlert: false,
      loadingData: true, //This is necessary to stop error in render
    }
  },
  mounted() {
    this.setUpInventoryCard();
    this.loadingData = false; //This is necessary to stop error in render
  },
  methods: {
    /**
     * init the data which is from the inventory table
     */
    setUpInventoryCard() {
      let inventoryCopy = Object.assign({}, this.inventory);
      // We have to assign displayedProductId here. If we assign displayedProductId in this.inventoryinfo then it won't be reactive
      if (inventoryCopy.product) {
        inventoryCopy.displayedProductId = inventoryCopy.product.id.split(/-(.+)/)[1];
      } else {
        inventoryCopy.displayedProductId = inventoryCopy.productId.split(/-(.+)/)[1];
      }
      this.inventoryInfo = inventoryCopy;

      if (this.disabled) {
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
      let today = date.getFullYear() + "-" + (date.getMonth() + 1).toString().padStart(2, '0') + '-' + date.getDate().toString().padStart(2, '0');
      return today;
    },

    /**
     * calculate the Total price when the price prt item or quantity changed
     */
    calculateTotalPrice() {
      const calculatedPrice = (this.inventoryInfo.pricePerItem * this.inventoryInfo.quantity).toFixed(2);
      this.inventoryInfo.totalPrice = Math.max(calculatedPrice, 0);   // Make sure the total price doesn't go to negatives (eg. if the user enters a negative quantity)
    },

    /**
     * when click Cancel button the modal will hide.
     */
    cancelAction() {
      this.inventoryCardError = "";
      this.$bvModal.hide('inventory-card');
    },

    /**
     * Opens the modal to select a product for this inventory item
     */
    openSelectProductModal() {
      this.$bvModal.show('select-products');
      this.$refs.productTable.getProducts(this.currentBusiness);
    },

    /**
     * Takes a selected product to be used to create the inventory.
     * Sets the displayed product id field to the id of the selected product.
     * Hides the selected product modal.
     * @param product The selected product to be used to create the inventory.
     */
    selectProduct(product) {
      this.inventoryInfo.displayedProductId = product.id.split(/-(.+)/)[1];
      this.$bvModal.hide('select-products');
    },
    /**
     * Ok action button when edit modal is ture
     * the OkAction will modify the Inventory otherwise Create a Inventory
     * This also sets the inventory product id to be the 'actual' product id selected.
     * That is, it sets the productId field of the data to be POSTed to be the id in the
     * text field, but with the business id prepended to it.
     */
    okAction: async function () {
      this.inventoryInfo.productId = `${this.currentBusiness.id}-${this.inventoryInfo.displayedProductId}`.toUpperCase();

      if (this.editModal) {
        await this.editInventory();
      } else {
        await this.createInventory();
      }

    },

    /**
     * Makes a request to the API to create a inventory with the form input.
     * Then, will hide the inventory popup
     */
    async createInventory() {
      this.showErrorAlert = false;

      await api
          .createInventory(this.$route.params.id, this.inventoryInfo)
          .then((createInventoryResponse) => {
            this.$log.debug("Inventory Created", createInventoryResponse);
            this.$bvModal.hide('inventory-card');
            this.setUpInventoryPage();
          })
          .catch((error) => {
            this.handleApiError(error);
          });
    },

    /**
     * Sends an API request to modify the inventory data, and closes the modal and refreshes the table is successful
     */
    editInventory: async function () {
      this.showErrorAlert = false;

      await api
          .modifyInventory(this.$route.params.id, this.inventoryInfo.id, this.inventoryInfo)
          .then(() => {
            this.$bvModal.hide('inventory-card');
            this.setUpInventoryPage();// update the table
          })
          .catch((error) => {
            this.handleApiError(error);
          })
    },

    /**
     * Call this method when your API call returns an error. Takes an error sent back by an API call,
     * and displays it (in a user-friendly format) in an alert.
     */
    handleApiError(error) {
      this.inventoryCardError = this.getErrorMessageFromApiError(error);
      this.showErrorAlert = true;
      this.$log.debug(error);
    },

    /**
     * Given an error thrown by a rejected axios (api) request, returns a user-friendly string
     * describing that error. Only applies to POST and PUT requests for inventory
     */
    getErrorMessageFromApiError(error) {
      if ((error.response && error.response.status === 400)) {
        return error.response.data.message;
      } else if ((error.response && error.response.status === 403)) {
        return "Forbidden. You are not an authorized administrator";
      } else if (error.request) {  // The request was made but no response was received, see https://github.com/axios/axios#handling-errors
        return "No Internet Connectivity";
      } else {
        return "Server error";
      }
    },

    /**
     * Uses an image's filename and returns the image with the filename requested.
     * @param imageFileName is a string of the requested image's file name
     * @return string
     **/
    getImage: function (imageFileName) {
      return api.getImage(imageFileName);
    },
  }
}
</script>
