<template>
  <b-nav-item-dropdown right class="notifications-tray" v-if="isActingAsUser">
    <template #button-content>
      <div class="icon mr-1">
        <b-icon v-if="numberOfNotifications"  icon="bell" class="iconBell" variant="danger" style="font-size:  1.8rem"></b-icon>
        <b-icon v-else icon="bell" class="iconBell" variant="light" style="font-size:  1.8rem"></b-icon>
        <span v-if="numberOfNotifications" style="position: absolute; transform: translateY(5px); color: red">
                {{numberOfNotifications}}
              </span>
      </div>
    </template>
    <b-dropdown-item disabled>
      <h4 style="color: black">Notifications: <span> {{numberOfNotifications}}</span></h4>
    </b-dropdown-item>
    <b-dropdown-item class="notifications-item expiring-notifications-item" v-for="card in expiringCards" v-bind:key="card.id + card.title" @click="goToHomePage">
      <h6> Marketplace Card: {{card.title}}</h6>
      <strong>expires within 24 hours</strong>
    </b-dropdown-item>

    <b-dropdown-item v-for="notification in notifications" v-bind:key="notification.id" class="notifications-item" @click="notificationClicked(notification)">
      <b-row>
          <b-col cols="1">
            <b-icon-exclamation-triangle v-if="notification.type==='Liked Listing Purchased'"/>
            <b-icon-heart v-else-if="notification.type==='Liked Listing'"/>
            <b-icon-x-circle v-else-if="notification.type==='Unliked Listing'" />
            <b-icon-clock-history v-else-if="notification.type==='Expired Marketplace Card'"/>
            <b-icon-cart v-else-if="notification.type==='Purchased listing'"/>
          </b-col>
          <b-col cols="7">
            <h6> {{notification.type}} </h6>
          </b-col>
          <b-col cols="3">
            <h6> {{notification.price}} </h6>
          </b-col>
        </b-row>
        <hr class="mt-1 mb-1">
        <span>{{ notification.message }}</span>
        <h6 v-if="notification.location"> Location: {{notification.location}} </h6>
    </b-dropdown-item>
  </b-nav-item-dropdown>
</template>

<script>
import api from "../../Api";
import EventBus from "../../util/event-bus";
import Api from "../../Api";

export default {
  name: "NotificationDropdown",
  data() {
    return {
      notifications: [],
      expiringCards: [],
    }
  },
  computed: {
    isActingAsUser: function() {
      return this.$currentUser.currentlyActingAs == null;
    },
    /**
     * Checks if there are notifications about expiring or expired cards.
     * @return The number of total notifications
     */
    numberOfNotifications: function () {
      return this.expiringCards.length + this.notifications.length;
    },
  },
  methods: {

    /**
     * Changes page to homepage of user
     */
    goToHomePage() {
      if (this.$route.fullPath !== '/homepage') {
        this.$router.push('/homepage');
      }
    },
    /**
     *  Adds a notification about a card that expires within next 24 hours.
     *  This is done by adding the expiring card to the list of notifications.
     */
    async updateNotifications() {
      this.expiringCards = (await api.getExpiredCards(this.$currentUser.id)).data;
      this.notifications = (await api.getNotifications(this.$currentUser.id)).data;

      for (const notification of this.notifications) {
        if (notification.type === "Purchased listing") {
          await this.updatePurchasedNotifications(notification)
        }
      }

    },

    /**
     * Updates the purchase listing notification with the product data
     *
     */
    async updatePurchasedNotifications(notification) {
      this.purchasedListing = (await Api.getPurchaseListing(notification.subjectId)).data
      const address = this.purchasedListing.business.address;
      notification.location = (address.suburb ? address.suburb + ", " : "") + `${address.city}, ${address.region}, ${address.country}`;
      const currency = await Api.getUserCurrency(address.country);
      notification.price = currency.symbol + this.purchasedListing.price + " " + currency.code
      notification.message = `${this.purchasedListing.quantity} x ${this.purchasedListing.product.name}`
    },


    /**
     * Performs an action based on the notification that has been clicked.
     * When a liked or unliked listing is clicked it routes you to that listing
     * @param notification the notification that has been clicked
     */
    notificationClicked(notification) {
      if (notification.type==='Liked Listing' || notification.type==='Unliked Listing'){
        if (!(this.$route.name === 'listings-full' &&  this.$route.params.id === notification.subjectId)) {
          this.$router.push('/listings/' + notification.subjectId);
        }
      }
    }
  },

  created() {
    this.updateNotifications();
    this.interval = setInterval(() => this.updateNotifications(), 60000);
  },

  mounted() {
    /**
     * This mount listens to the notificationUpdate event
     */
    EventBus.$on('notificationUpdate', this.updateNotifications)
  }
}

</script>

<style>
.notifications-item {
  border-top: 1px solid #eee;
  width: 26rem;
}

.notifications-item h6 {
  margin-top: 3px;
}

.notifications-item * {
  white-space:normal;
  word-wrap:break-word;
}

.notifications-item .dropdown-item:active {
  color: initial;
  background-color: #cccccc;
}
.expiring-notifications-item * {
  color: orangered;
}
.notifications-tray .dropdown-menu {
  max-height: 80vh;
  overflow-y: auto;
}
</style>
