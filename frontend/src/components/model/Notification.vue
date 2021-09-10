<template>
  <div>
    <div v-if="!updatedNotification.read">
      <b-row >
        <b-col>
         <span class="unreadLabel">
         <b-icon-envelope-fill> </b-icon-envelope-fill> New Notification</span>
        </b-col>
      </b-row>
      <hr class="unreadHr">
    </div>

    <b-row>
    <b-col cols="2" class="pt-1 pr-0">
      <b-icon-star-fill class="mr-2 star-icon "  v-if="updatedNotification.starred"/>
      <b-icon-exclamation-triangle v-if="updatedNotification.type==='Liked Listing Purchased'"/>
      <b-icon-heart v-else-if="updatedNotification.type==='Liked Listing'"/>
      <b-icon-x-circle v-else-if="updatedNotification.type==='Unliked Listing'" />
      <b-icon-clock-history v-else-if="updatedNotification.type==='Expired Marketplace Card'"/>
      <b-icon-cart  v-else-if="updatedNotification.type==='Purchased listing'"/>
    </b-col>
    <b-col class="pl-0 pt-1" cols="5">
      <h6> {{updatedNotification.type}} </h6>
    </b-col>
    <b-col cols="3" class="pt-1">
      <h6> {{updatedNotification.price}} </h6>
    </b-col>
    <b-col cols="2">
        <b-dropdown right no-caret variant="link" class="float-right" v-if="!this.inNavbar">
          <template #button-content>
            <div>
              <b-icon-three-dots-vertical class="three-dot float-right " ></b-icon-three-dots-vertical>
            </div>
          </template>
          <b-dropdown-item @click="starNotification">
            <p ><b-icon-star-fill v-if="updatedNotification.starred" title="Mark this notification as Important" class="star-icon"></b-icon-star-fill>
            <b-icon-star title="Remove this notification as Important" class="star-icon"  v-else></b-icon-star>   Important</p>
          </b-dropdown-item>
          <b-dropdown-item @click="confirmArchive">
            <p><b-icon-archive class="archive-button" variant="outline-success" title="Archive this notification"></b-icon-archive>  Archive</p>
          </b-dropdown-item>
          <b-dropdown-item @click="confirmDelete">
            <p><b-icon-trash class="delete-button" title="Delete this notification"></b-icon-trash>  Delete</p>
          </b-dropdown-item>
        </b-dropdown>
    </b-col>
  </b-row>
  <hr class="mt-1 mb-2">
    <div v-if="updatedNotification.type === 'Liked Listing' || updatedNotification.type === 'Unliked Listing'">
      <span @click="goToListing" class="listing-link">{{ updatedNotification.message }}</span>
    </div>
    <div v-else>
      <span >{{ updatedNotification.message }}</span>
    </div>

      <b-row>
        <b-col cols="11">
          <h6 v-if="updatedNotification.location"> Location: {{updatedNotification.location}} </h6>
        </b-col>
        <b-col v-if="updatedNotification.read"  cols="1">
          <span class="readLabel">
           <b-icon-check2-all> </b-icon-check2-all> </span>
        </b-col>
      </b-row>

    <b-modal ref="confirmDeleteModal" size="sm"
             title="Delete Notification"
             ok-variant="danger"
             ok-title="Delete"
             @ok="deleteNotification">
      Are you sure you want to <strong>delete</strong> this notification?
    </b-modal>

    <b-modal ref="confirmArchiveModal" size="sm"
             title="Archive Notification"
             ok-variant="success"
             ok-title="Archive"
             @ok="archiveNotification">
      Are you sure you want to <strong>archive</strong> this notification?
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

.star-icon {
  color: dodgerblue;
}

.archive-button {
  color: green;
}

.delete-button {
  color: red;
}

.three-dot {
  margin-right: -10px;
  padding-right: 0;

  color: black;
}

.listing-link:hover {
  color: dodgerblue;
  text-decoration: underline;
  cursor: pointer;
}

/*this is being used ignore warning*/
.dropdown-menu {
  min-width: 6rem !important;
}

</style>

<script>
import Api from "../../Api";
import EventBus from "../../util/event-bus";
import {formatAddress} from "../../util";

export default {
  name: "Notification",
  props: ['notification', 'inNavbar'],
  data() {
    return {
      updatedNotification: {message:"", type:"", read: this.notification.read},
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
      notification.location = formatAddress(address, 2);
      const currency = await Api.getUserCurrency(address.country);
      notification.price = currency.symbol + purchasedListing.price + " " + currency.code
      notification.message = `${purchasedListing.quantity} x ${purchasedListing.product.name}`
      return notification
    },

    /**
     * when called it checks if the notification is currently liked
     * then sends a toggled api request based on this
     */
    async starNotification() {
      if(this.updatedNotification.starred) {
        this.updatedNotification.starred = false
        await Api.patchNotification(this.updatedNotification.id, {"starred": false})
      } else {
        this.updatedNotification.starred = true
        await Api.patchNotification(this.updatedNotification.id, {"starred": true})
      }
      EventBus.$emit("notificationUpdate")
    },

    /**
     * Performs an action based on the notification that has been clicked.
     * When a liked or unliked listing is clicked it routes you to that listing
     */
    goToListing() {
      if (this.updatedNotification.type === 'Liked Listing' || this.updatedNotification.type === 'Unliked Listing') {
        if (this.$route.fullPath !== '/listings/' + this.updatedNotification.subjectId) {
          this.$router.push('/listings/' + this.updatedNotification.subjectId);
        }
      }
    },

    /**
     * Calls API deleteNotification patch request
     * and using an EventBus that emits notificationUpdate so that
     * other components are refreshed.
     */
    async deleteNotification() {
      await Api.deleteNotification(this.updatedNotification.id)
      EventBus.$emit("notificationUpdate")
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
      await Api.patchNotification(this.updatedNotification.id, {"archived": true})
      EventBus.$emit("notificationUpdate")
    },

    /**
     * Shows a dialog to confirm deleting the notification.
     * USES REFS NOT ID TO PREVENT DUPLICATION!
     */
    async confirmDelete() {
      this.$refs.confirmDeleteModal.show();
    },

    /**
     * Marks a notification as read and makes th api call.
     * @param notification the notification that has been clicked
     */
    async markRead(notification) {
      if (notification.id === this.notification.id) {
        this.updatedNotification.read = true;
        await Api.patchNotification(notification.id, {"read": true});
      }
    }

  },
  async mounted() {
    EventBus.$on('notificationClicked', this.markRead);

    if (this.notification.type === "Purchased listing") {
      this.updatedNotification = await this.updatePurchasedNotifications(this.notification)
    } else {
      this.updatedNotification = this.notification
    }
  },


}
</script>
