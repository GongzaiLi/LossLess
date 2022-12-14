<template>
  <b-card class="b_card_listing shadow-sm">
    <b-card-title>{{ listing.quantity }} x {{ listing.inventoryItem.product.name }}</b-card-title>

    <hr>
    <b-card-sub-title>
      Product Expires: {{ listing.inventoryItem.expires }}
    </b-card-sub-title>
    <div v-if="listing.inventoryItem.product.images.length">
      <img class="product-image" :src="getPrimaryImage(listing)" alt="Failed to load image">
    </div>
    <div v-if="!listing.inventoryItem.product.images.length">
      <img class="product-image" src="product_default.png" alt="Product has no image">
    </div>
    <hr>
    <h5><strong>Seller: {{ listing.business.name }}</strong></h5>
    <span>Location: {{getAddress}}</span><br>
    <span>Closes: {{ getCloses }}</span>
    <template #footer>
      <h5 class="listing_price" v-if="currency">
        {{ currency.symbol }}{{ listing.price }} {{ currency.code }}
      </h5>
    </template>
  </b-card>
</template>

<style>
.b_card_listing {
  min-width: 17rem;
  height: 100%;
}

.b_card_listing:hover {
  -webkit-box-shadow: 0 1rem 3rem rgba(0, 0, 0, 0.18) !important;
  box-shadow: 0 1rem 3rem rgba(0, 0, 0, 0.18) !important;
  cursor: pointer;
}

.listing_price {
  float: right;
}
</style>

<script>
import Api from "../../Api";
import {formatAddress, formatDateTime} from "../../util";

export default {
  name: "PartialListingCard",
  props: ["listing", "searchQuery"],
  data() {
    return {
      currency: null,
    }
  },
  async mounted() {
    await this.setCurrency();
  },

  methods: {

    /**
     * Takes a listing as input and returns the primary image for that listing
     * @return image      The image of the listing
     * @param listing     The listing to get the image of
     **/
    getPrimaryImage: function (listing) {
      const primaryImageFileName = listing.inventoryItem.product.primaryImage.fileName;
      return Api.getImage(primaryImageFileName);
    },

    /**
     * Queries the currencies API to get currency info for the business
     */
    async setCurrency() {
      this.currency = await Api.getUserCurrency(this.listing.business.address.country);
    }
  },

  computed: {
    /**
     * Formats the address using util function and appropriate privacy level.
     *
     *
     * @return address formatted
     */
    getAddress: function () {
      return formatAddress(this.listing.business.address, 4);
    },

    getCloses: function () {
      return formatDateTime(this.listing.closes)
    }
  },

  watch: {
    async listing() {
      await this.setCurrency();
    },
  }
}
</script>
