<template>
  <div>
    <div v-if="!updatedNotification.read">
      <b-row >
        <b-col>
         <span class="unreadLabel">
         <b-icon-star-fill> </b-icon-star-fill> New Notification</span>
        </b-col>
      </b-row>
      <hr class="unreadHr">
    </div>

    <div>
      <b-row>
      <b-col cols="1">
        <b-icon-exclamation-triangle v-if="updatedNotification.type==='Liked Listing Purchased'"/>
        <b-icon-heart v-else-if="updatedNotification.type==='Liked Listing'"/>
        <b-icon-x-circle v-else-if="updatedNotification.type==='Unliked Listing'" />
        <b-icon-clock-history v-else-if="updatedNotification.type==='Expired Marketplace Card'"/>
        <b-icon-cart v-else-if="updatedNotification.type==='Purchased listing'"/>
      </b-col>
      <b-col cols="6">
        <h6> {{updatedNotification.type}} </h6>
      </b-col>
      <b-col cols="3">
        <h6> {{updatedNotification.price}} </h6>
      </b-col>
      <b-col cols="1" class="float-right">
          <b-button size="sm" variant="outline-success" @click.stop=confirmArchive title="Archive this notification"><b-icon-archive></b-icon-archive></b-button>
      </b-col>
      </b-row>
      <hr class="mt-1 mb-2">
      <span>{{ updatedNotification.message }}</span>

      <b-row>
        <b-col cols="11">
          <h6 v-if="updatedNotification.location"> Location: {{updatedNotification.location}} </h6>
        </b-col>
        <b-col v-if="updatedNotification.read"  cols="1">
          <span class="readLabel">
           <b-icon-check2-all> </b-icon-check2-all> </span>
        </b-col>
      </b-row>
    </div>

    <b-modal ref="confirmArchiveModal" size="sm"
             title="Archive Notification"
             ok-variant="success"
             ok-title="Archive"
             @ok="archiveNotification">
      <h6>
        Are you sure you want to <strong>archive</strong> this notification?
      </h6>
    </b-modal>

  </div>
</template>


<style>

.readLabel {
  float: right;
  color: green;

}

hr.unreadHr {
  margin-top: 0.3rem;
  margin-bottom: 0.5rem;
  border-top: 1px solid orangered;
}

span.unreadLabel {
  float: left;
  color: orangered;
}

</style>

<script>
import Api from "../../Api";
import EventBus from "../../util/event-bus";

export default {
  name: "Notification",
  props: ['notification'],
  data() {
    return {
      updatedNotification: {message:"", type:"", read: false},
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
    },

    /**
     * Shows a dialog to confirm archiving the notification.
     * USES REFS NOT ID TO PREVENT DUPLICATION!
     */
    async confirmArchive() {
      this.$refs.confirmArchiveModal.show();
    },

    /**
     * Calls API archiveNotification patch request
     * and using an EventBus that emits notificationUpdate so that
     * other components are refreshed.
     */
    async archiveNotification() {
      await Api.archiveNotification(this.notification.id)
      EventBus.$emit("notificationUpdate")
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
