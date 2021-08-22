<template>
  <div>
  <b-row>
    <b-col cols="1">
      <b-icon-exclamation-triangle v-if="updatedNotification.type==='Liked Listing Purchased'"/>
      <b-icon-heart v-else-if="updatedNotification.type==='Liked Listing'"/>
      <b-icon-x-circle v-else-if="updatedNotification.type==='Unliked Listing'" />
      <b-icon-clock-history v-else-if="updatedNotification.type==='Expired Marketplace Card'"/>
      <b-icon-cart v-else-if="updatedNotification.type==='Purchased listing'"/>
    </b-col>
    <b-col cols="7">
      <h6> {{updatedNotification.type}} </h6>
    </b-col>
    <b-col cols="3">
      <h6> {{updatedNotification.price}} </h6>
    </b-col>
  </b-row>
  <hr class="mt-1 mb-2">
  <span>{{ updatedNotification.message }}</span>
  <h6 v-if="updatedNotification.location"> Location: {{updatedNotification.location}} </h6>
    </div>
</template>

<script>
import Api from "../../Api";

export default {
  name: "Notification",
  props: ['notification'],
  data() {
    return {
      updatedNotification: {message:"", type:""}
    }
  },
  methods: {
    /**
     * Updates the purchase listing notification with the product data
     * @param notification the purchase listing notification
     * @return notification the same notification updated
     */
    async updatePurchasedNotifications(notification) {
      const purchasedListing = (await Api.getPurchaseListing(notification.subjectId)).data
      const address = purchasedListing.business.address;
      notification.location = (address.suburb ? address.suburb + ", " : "") + `${address.city}, ${address.region}, ${address.country}`;
      const currency = await Api.getUserCurrency(address.country);
      notification.price = currency.symbol + purchasedListing.price + " " + currency.code
      notification.message = `${purchasedListing.quantity} x ${purchasedListing.product.name}`
      return notification
    }

  },
  async mounted() {
    if (this.notification.type === "Purchased listing") {
      this.updatedNotification = await this.updatePurchasedNotifications(this.notification)
    } else {
      this.updatedNotification = this.notification
    }
  }
}
</script>
