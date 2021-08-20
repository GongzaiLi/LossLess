<template>
  <div>
  <b-card class="shadow" style="max-width: 80rem">
      <h1 v-if="$currentUser.currentlyActingAs">{{$currentUser.currentlyActingAs.name + "'s Home Page"}}</h1>
      <h1 v-else>{{userData.firstName + "'s Home Page"}}</h1>
      <router-link v-if="$currentUser.currentlyActingAs" :to="{ name: 'business-profile', params: { id: $currentUser.currentlyActingAs.id }}">
      <h4>Profile page</h4>
    </router-link>
    <router-link v-else :to="{ name: 'user-profile', params: { id: $currentUser.id }}">
      <h4>Profile page</h4>
    </router-link>
  </b-card>
    <b-row>
    <b-col md="7">
    <b-card v-if="!$currentUser.currentlyActingAs" class="expired-cards mt-3 shadow">
      <h3><b-icon-clock/> Your recently closed cards </h3>
      <h6>These cards will be deleted within 24 hours of their closing date. You can either extend their display period or delete cards you no longer need.</h6>
      <b-input-group v-if="hasExpiredCards">
        <b-form-text style="margin-right: 7px">
          Table View
        </b-form-text>
        <b-form-checkbox v-model="isCardFormat" switch/>
        <b-form-text>
          Card View
        </b-form-text>
      </b-input-group>
      <h2 style="text-align: center; margin-top: 2rem" v-if="!hasExpiredCards"> You have no cards that recently expired </h2>
      <marketplace-section
          v-else
          :is-card-format="isCardFormat"
          :cardsPerRow="2"
          :perPage="2"
          section="homepage"
          v-on:cardCountChanged="checkExpiredCardsExist"
      />
    </b-card>
    </b-col>
    <b-col md="5">
    <b-card v-if="!$currentUser.currentlyActingAs" class="shadow mt-3">
      <h3><b-icon-bell/> Notifications </h3>
      <div class="notification-holder">
        <b-card v-if="notifications.length === 0" class="notification-cards shadow">
          <h6> You have no notifications </h6>
        </b-card>
        <b-card v-for="notification in notifications" v-bind:key="notification.id" class="notification-cards shadow"  @click="notificationClicked(notification)">
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
            <hr>
            <span>{{ notification.message }}</span>
            <h6 v-if="notification.location"> Location: {{notification.location}} </h6>
        </b-card>
      </div>
    </b-card>
    </b-col>
    </b-row>
  </div>
</template>

<style>

.expired-cards {
  overflow-y: auto;
  height: 42rem;
  max-height: 42rem;
  float:left

}

.notification-holder {
  height: 37rem;
  max-height: 37rem;
  overflow-y: auto;
  margin-top: -3px;
}


.notification-cards {
  margin-top: 20px;
  cursor: pointer;
}

</style>

<script>
import Api from "../../Api";
import MarketplaceSection from "../marketplace/MarketplaceSection";

export default {
  components: {MarketplaceSection},
  data: function () {
    return {
      userData: {
        id: "",
        firstName: "",
        lastName: "",
        middleName: "",
        nickname: "",
        bio: "",
        email: "",
        dateOfBirth: "",
        phoneNumber: "",
        homeAddress: "",
      },
      errors: [],
      activeTabIndex: 0,
      isCardFormat: true,
      hasExpiredCards: false,
      notifications: [],
      purchasedListing: {}
    }
  },

  mounted() {
    const userId = this.$currentUser.id;
    this.getUserInfo(userId);
  },

  methods: {
    /**
     * this is a get api which can take Specific user to display on the page
     * The function id means user's id, if the serve find -
     * -the user's id will response the data and keep the data into this.userData
     */
    getUserInfo: function (id) {
      Api
          .getUser(id)
          .then((response) => {
            this.$log.debug("Data loaded: ", response.data);
            this.userData = response.data;
          })
          .catch((error) => {
            this.$log.debug(error);
          })
    },


    checkExpiredCardsExist(cards) {
      this.hasExpiredCards = cards.length !== 0;
    },

    /**
     * Updates the notifications using the notification api requests and updates whether the user has
     * cards that have expired.
     */
    async updateNotifications() {
      const expiredCards = (await Api.getExpiredCards(this.$currentUser.id)).data;
      if (expiredCards.length > 0) {
        this.hasExpiredCards = true;
      }
      this.notifications = (await Api.getNotifications()).data;

      for (const notification of this.notifications) {
        if (notification.type === "Purchased listing") {
          await this.updatePurchasedNotifications(notification)
        }
      }
    },

    /**
     * Performs an action based on the notification that has been clicked.
     * When a liked or unliked listing is clicked it routes you to that listing
     * @param notification the notification that has been clicked
     */
    notificationClicked(notification) {
      if (notification.type === 'Liked Listing' || notification.type === 'Unliked Listing') {
        if (this.$route.fullPath !== '/listings/' + notification.subjectId) {
          this.$router.push('/listings/' + notification.subjectId);
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
    }
  },

  created() {
    this.updateNotifications();
    this.interval = setInterval(() => this.updateNotifications(), 60000);
  },
}
</script>

