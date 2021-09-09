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
          <notification :notification="notification"> </notification>
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
import Notification from "../model/Notification";
import {markNotificationRead} from '../../util/index'
import EventBus from "../../util/event-bus";

export default {
  components: {MarketplaceSection, Notification},
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
      notificationUpdate: {
        read: false
      }
    }
  },

  mounted() {
    const userId = this.$currentUser.id;
    this.getUserInfo(userId);

    /**
     * This mount listens to the notificationUpdate event
     */
    EventBus.$on('notificationClicked', this.notificationClickedFromNavBar)
    EventBus.$on('notificationUpdate', this.updateNotifications)
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

    /**
     * Performs an action based on the notification that has been clicked.
     * When a liked or unliked listing is clicked it routes you to that listing
     * @param notification the notification that has been clicked
     */
    async notificationClicked(notification) {
      const response = await markNotificationRead(notification, this.notificationUpdate);
      this.$log.debug(response)
      EventBus.$emit('notificationClickedFromHomeFeed', notification);
      if (notification.type === 'Liked Listing' || notification.type === 'Unliked Listing') {
        if (this.$route.fullPath !== '/listings/' + notification.subjectId) {
          await this.$router.push('/listings/' + notification.subjectId);
        }
      }
    },

    /**
     * Updates the status of the notification in home feed when a nav bar notification status is updated.
     * @param notification The notification that the user updated from nav bar.
     */
    notificationClickedFromNavBar(notification) {
      const updated = this.notifications.find(item => item.id === notification.id);
      this.notificationClicked(updated);
    }

  },

  created() {
    this.updateNotifications();
  },
}
</script>

