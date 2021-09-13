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
          <b-col cols="6">
            <h3>Notifications</h3>
          </b-col>
          <b-col cols="4">
            <b-dropdown text="Filter By Tags" class="tag-filter-dropdown">
              <b-dropdown-form v-for="[tagColor, selected] in Object.entries(tagColors)" :key="tagColor" @click="toggleTagColorSelected(tagColor)" :class="[selected ? 'selected' : '']">
                <NotificationTag :tag-color=tagColor class="tag" :tag-style-prop="{height: '1.5rem', width: '100%'}"></NotificationTag>
              </b-dropdown-form>
            </b-dropdown>
          </b-col>
        </b-row>
      </div>
      <div class="notification-holder">
        <b-card v-if="filteredNotifications.length === 0" class="notification-cards shadow">
          <h6> You have no notifications </h6>
        </b-card>
        <b-card v-for="notification in filteredNotifications" v-bind:key="notification.id" class="notification-cards shadow" @click="notificationClicked(notification)">
          <notification :notification="notification" :in-navbar="false" @deleteNotification="createDeleteToast"> </notification>
        </b-card>
      </div>
    </b-card>
    </b-col>
    </b-row>
    <div class="undoToastClass">
    <b-toast id="undoToast" variant="danger" toaster="b-toaster-bottom-right" toast-class="undoToastClass"
             auto-hide-delay="10000"
             @hide="deleteNotification"
    >
      <template #toast-title>
        Deleted notification
      </template> <b-button size="sm" @click="undoDelete">Undo</b-button>
    </b-toast>
    </div>
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

.b-toaster-bottom-right .b-toaster-slot{
  float: right;
  max-width: 250px !important;
}

</style>

<script>
import Api from "../../Api";
import MarketplaceSection from "../marketplace/MarketplaceSection";
import NotificationTag from "../model/NotificationTag";
import Notification from "../model/Notification";
import EventBus from "../../util/event-bus"

const beforeUnloadListener = (event) => {
  event.preventDefault();
  return event.returnValue = 'Are you sure you want to exit?';
};


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
      tagColors: { //Tracks color to selected boolean
        RED: false,
        ORANGE: false,
        YELLOW: false,
        GREEN: false,
        BLUE: false,
        PURPLE: false,
        BLACK: false,
      },
      pendingDeletedNotification:false,
      undo:false,
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
      this.notifications = (await Api.getNotifications()).data;
      this.pendingDeletedNotification = false;
    },
    /**
     * Creates the toast notification to allow user to undo delete within 10 seconds
     * Sets pendingDeletedNotification so this notification is removed from the list
     * If there is already a notification waiting to be delete it executes api request to
     * delete old notification
     * Adds listener to prevent user from accidentally leaving the page before deletion is confirmed
     * @param id Id of the notification to delete in 10 seconds
     */
    createDeleteToast(id){
      if (this.pendingDeletedNotification){
        this.deleteNotification()
      }
      addEventListener('beforeunload', beforeUnloadListener, {capture: true});
      this.undo=false
      this.pendingDeletedNotification=id
      this.$bvToast.show('undoToast')
      EventBus.$emit('notificationTemporarilyRemoved',id)
    },

    /**
     * Sends api request to delete the notification
     * removes listener that prevents user from navigating away
     * emits to event bus to tell all components to update notifications
     */
    async deleteNotification() {
      removeEventListener('beforeunload', beforeUnloadListener, {capture: true});
      if (!this.undo&&this.pendingDeletedNotification) {
        await Api.deleteNotification(this.pendingDeletedNotification)
        EventBus.$emit("notificationUpdate")
      }
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
     * Hides the toast notification and prevents deleteNotification from sending api request
     * Adds pendingDeletedNotification back into displayed list
     * removes listener that prevents user from navigating away
     */
    undoDelete(){
      this.undo=true;
      this.$bvToast.hide('undoToast')
      this.pendingDeletedNotification=false
      EventBus.$emit('notificationTemporarilyRemoved',false)
      removeEventListener('beforeunload', beforeUnloadListener, {capture: true});
    },

    /**
     * Creates confirmation window to make sure user wants to leave
     */
    confirmLeave(){
      if (this.pendingDeletedNotification) {
        return window.confirm('Are you sure you would like to change the page? You will not be unable to undo the pending notification deletion')
      }
      return true
    },

  },

  computed: {
    /**
     * Filters notifications by removing notification pending deletion from the list
     * @returns notifications with pendingDeletedNotification removed
     */
    filteredNotifications: function(){
      let removedNotification=this.pendingDeletedNotification
      let filtered =[]
      filtered = this.notifications.filter(function(value){
        return value.id !== removedNotification
      });
      return filtered
    }
  },

  created() {
    this.updateNotifications();
  },

  beforeRouteLeave (to, from, next) {
    if (this.confirmLeave()) {
      this.deleteNotification()
      next()
    } else {
      next(false)
    } }
}
</script>

