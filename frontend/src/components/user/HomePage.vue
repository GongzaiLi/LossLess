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

    <b-card v-if="hasExpiredCards && !$currentUser.currentlyActingAs" class="expired-cards shadow">
      <h3><b-icon-clock/> Your recently closed cards </h3>
      <h6>These cards will be deleted within 24 hours of their closing date. You can either extend their display period or delete cards you no longer need.</h6>
      <b-input-group>
        <b-form-text style="margin-right: 7px">
          Table View
        </b-form-text>
        <b-form-checkbox v-model="isCardFormat" switch/>
        <b-form-text>
          Card View
        </b-form-text>
      </b-input-group>
      <marketplace-section
          :is-card-format="isCardFormat"
          :cardsPerRow="3"
          :perPage="2"
          section="homepage"
          v-on:cardCountChanged="checkExpiredCardsExist"
      />
    </b-card>
    <b-card v-if="!$currentUser.currentlyActingAs" style="margin-left: 15px; margin-top: 20px; float: left" class="shadow">
      <h3><b-icon-bell/> Notifications </h3>
      <div class="notification-holder">
        <b-card v-for="notification in notifications" v-bind:key="notification.id" class="notification-cards shadow">
          <h6> {{notification.type}} </h6>
          <span>{{ notification.message }}</span>
        </b-card>
      </div>
    </b-card>
  </div>
</template>

<style>

.expired-cards {
  overflow-y: scroll;
  height: 42rem;
  max-height: 42rem;
  margin-top: 20px;
  max-width: 52rem;
  width: 52rem;
  float:left

}

.notification-holder {
  height: 37rem;
  max-height: 37rem;
  overflow-y: scroll;
  margin-top: -3px;
}


.notification-cards {
  margin-top: 20px;
  width: 22rem;
  max-width: 22rem;

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
    },
  },

  created() {
    this.updateNotifications();
    this.interval = setInterval(() => this.updateNotifications(), 60000);
  },
}
</script>

