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
      <div>
        <b-row>
          <b-col cols="1">
            <h3><b-icon-bell/></h3>
          </b-col>
          <b-col cols="5">
            <h3>Notifications</h3>
          </b-col>
          <b-col cols="4">
            <b-dropdown v-if="!isArchivedSelected" text="Filter By Tags" class="tag-filter-dropdown">
              <b-dropdown-form v-for="[tagColor, selected] in Object.entries(tagColors)" :key="tagColor" @click="toggleTagColorSelected(tagColor)" :class="[selected ? 'selected' : '']">
                <NotificationTag :tag-color=tagColor class="tag" :tag-style-prop="{height: '1.5rem', width: '100%'}"></NotificationTag>
              </b-dropdown-form>
            </b-dropdown>
          </b-col>
          <b-col cols="2">
            <b-icon-archive font-scale="2" v-b-tooltip.hover title="View Archived Notifications"
                            class="view-archived-button pt-2" v-if="!isArchivedSelected" @click="toggleArchived"></b-icon-archive>
            <b-icon-archive-fill font-scale="2" variant="success" v-b-tooltip.hover title="View Notifications"
                                 class="view-archived-button pt-2" v-else @click="toggleArchived"></b-icon-archive-fill>
          </b-col>
        </b-row>
      </div>
      <div class="notification-holder">
        <b-card v-if="notifications.length === 0" class="notification-cards shadow">
          <h6 v-if="!isArchivedSelected"> You have no notifications </h6>
          <h6 v-else> You have no archived notifications </h6>
        </b-card>
        <b-card v-for="notification in notifications" v-bind:key="notification.id" class="notification-cards shadow" @click="notificationClicked(notification)">
          <notification :notification="notification" :in-navbar="false" :archived-selected="isArchivedSelected"> </notification>
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

.selected {
  background-color: lightgray;
}

.notification-cards {
  margin-top: 20px;
  /*cursor: pointer;*/
}

.view-archived-button {
  cursor: pointer;
  color: green;
}

</style>

<script>
import Api from "../../Api";
import MarketplaceSection from "../marketplace/MarketplaceSection";
import NotificationTag from "../model/NotificationTag";
import Notification from "../model/Notification";
import EventBus from "../../util/event-bus"

export default {
  components: {MarketplaceSection, Notification, NotificationTag},
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
      isArchivedSelected: false,
      tagColors: { //Tracks color to selected boolean
        RED: false,
        ORANGE: false,
        YELLOW: false,
        GREEN: false,
        BLUE: false,
        PURPLE: false,
        BLACK: false,
      },
    }
  },

  mounted() {
    const userId = this.$currentUser.id;
    this.getUserInfo(userId);
    EventBus.$on('notificationUpdate', this.updateNotifications)
  },

  methods: {
    /**
     * Toggles a color tags boolean value
     */
    toggleTagColorSelected: function (tagColor) {
      this.tagColors[tagColor] = !this.tagColors[tagColor];
      this.filterNotificationsByTag();
    },
    /**
     * Requests the notifications using the tags param to select only those that match the selected tags
     */
    filterNotificationsByTag: async function () {
      let tags = [];
      Object.entries(this.tagColors).filter(([, value]) => value).forEach(([tag,]) => tags.push(tag));
      this.notifications = (await Api.getNotifications(tags.join(","))).data;
    },
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

      this.notifications = (await Api.getNotifications(this.isArchivedSelected)).data;
    },

    /**
     * Performs an action based on the notification that has been clicked.
     * @param notification the notification that has been clicked
     */
    async notificationClicked(notification) {
      notification.read = true
      EventBus.$emit('notificationClicked', notification);
    },

    /**
     * Updates the notifications to show either the archived or non-archived notifications.
     * This is toggle by a button.
     *
     */
    async toggleArchived() {
      this.isArchivedSelected = !this.isArchivedSelected;
      await this.updateNotifications();
    }

  },

  created() {
    this.updateNotifications();
  },
}
</script>

