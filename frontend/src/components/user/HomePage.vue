<template>
  <div>
  <b-card class="shadow" :style="{ 'max-width': $currentUser.currentlyActingAs ? '850px' : 'initial' }">
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
    <b-col md="6" v-if="!$currentUser.currentlyActingAs">
    <b-card class="expired-cards mt-3 shadow">
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
    <b-col :md="notificationWidth">
    <b-card class="shadow mt-3 w-100">
      <div>
        <b-row>
          <b-col cols="1">
            <h3><b-icon-bell/></h3>
          </b-col>
          <b-col cols="5">
            <h3>Notifications</h3>
          </b-col>
          <b-col cols="4">
            <b-dropdown :variant="numberOfSelectedTags === 0 ? 'secondary' : 'primary'" v-if="!isArchivedSelected" :text="numberOfSelectedTags === 0 ? 'Filter By Tag' : `${numberOfSelectedTags} Tags Selected`">
              <b-dropdown-form v-for="[tagColor, selected] in Object.entries(tagColors)" :key="tagColor" @click="toggleTagColorSelected(tagColor)" :class="[selected ? 'selected' : '']">
                <NotificationTag :tag-color=tagColor class="tag" :tag-style-prop="{height: '1.5rem', width: '100%'}"></NotificationTag>
              </b-dropdown-form>
              <b-dropdown-item v-if="anyTagSelected" @click="removeAllTagsFromFilter()">
                <P><b-icon-x-circle-fill/> Remove Filters </p>
              </b-dropdown-item>
            </b-dropdown>
            <h3 v-else>(Archived)</h3>
          </b-col>
          <b-col cols="2">
            <b-icon-archive v-if="!isArchivedSelected" font-scale="2"  title="View Archived Notifications"
                            class="view-archived-button pt-2"  @click="toggleArchived" v-b-tooltip.hover></b-icon-archive>
            <b-icon-archive-fill v-else font-scale="2" variant="success"  title="View Notifications"
                                 class="view-archived-button pt-2"  @click="toggleArchived" v-b-tooltip.hover></b-icon-archive-fill>
          </b-col>
        </b-row>
      </div>
      <b-overlay class="notification-holder" v-model="loadingNotifications">
        <b-card v-if="filteredNotifications.length === 0" class="notification-cards shadow">
          <h6 v-if="isArchivedSelected"> You have no archived notifications </h6>
          <h6 v-if="!isArchivedSelected && numberOfSelectedTags === 0"> You have no notifications </h6>
          <h6 v-if="!isArchivedSelected && numberOfSelectedTags !== 0"> You have no notifications that match the selected tag filters </h6>
        </b-card>
        <div v-for="notification in filteredNotifications" v-bind:key="notification.id" class="notification-cards shadow" @click="notificationClicked(notification)">
          <notification @tagColorChanged="filterNotificationsByTag" :archived-selected="isArchivedSelected" :notification="notification"
                        :in-navbar="false" @deleteNotification="createDeleteToast">
          </notification>
        </div>
      </b-overlay>
    </b-card>
    </b-col>
    </b-row>
    <div class="undoToastClass" >
    <b-toast v-for="toast in pendingDeletedNotifications" v-bind:key="toast" :id="'undoToast'+toast" variant="danger" toaster="b-toaster-bottom-right" toast-class="undoToastClass"
             no-auto-hide
             visible
             @hide="deleteNotification(toast)"
             @show="toastCountdown(toast)"
    >
      <template #toast-title>
        Deleted notification
      </template> <b-button size="sm" @click="undoDelete(toast)">Undo({{countdowns[toast].count}})</b-button>
      {{countdowns[toast].text}}
    </b-toast>
    </div>
  </div>
</template>

<style scoped>

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

.b-toaster-bottom-right .b-toaster-slot{
  float: right;
  max-width: 250px !important;
}

</style>

<script>
import Api from "../../Api";
import NotificationTag from "../model/NotificationTag";
import Notification from "../model/Notification";
import MarketplaceSection from "../marketplace/MarketplaceSection";
import EventBus from "../../util/event-bus";
import Vue from 'vue';

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
      isArchivedSelected: false,
      loadingNotifications: false,
      tagColors: { //Tracks color to selected boolean
        RED: false,
        ORANGE: false,
        YELLOW: false,
        GREEN: false,
        BLUE: false,
        PURPLE: false,
        BLACK: false,
      },
      pendingDeletedNotifications:[],
      countdowns:{}
    }
  },

  mounted() {
    const userId = this.$currentUser.id;
    this.getUserInfo(userId);
    EventBus.$on('notificationUpdate', this.updateNotifications);
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
     * Toggle all tags to be un-selected
     */
    removeAllTagsFromFilter: function () {
      Object.entries(this.tagColors).forEach(([tag,]) => this.tagColors[tag] = false);
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
      this.loadingNotifications = true;
      const expiredCards = (await Api.getExpiredCards(this.$currentUser.id)).data;
      if (expiredCards.length > 0) {
        this.hasExpiredCards = true;
      }
      this.notifications = (await Api.getNotifications(null, this.isArchivedSelected)).data;
      this.loadingNotifications = false;
    },
    /**
     * Creates the toast notification to allow user to undo delete within 10 seconds
     * adds notification id to pendingDeletedNotifications so this notification is removed from the list of displayed notifications
     * Adds listener to prevent user from accidentally leaving the page before deletion is confirmed
     * @param id Id of the notification to delete in 10 seconds
     * @param text the text to add to the countdown
     */
    createDeleteToast(id,text){
      addEventListener('beforeunload', beforeUnloadListener, {capture: true});
      Vue.set(this.countdowns,id,{})
      Vue.set(this.countdowns[id],'count',10)
      Vue.set(this.countdowns[id],'text',text)
      this.pendingDeletedNotifications.push(id)
      EventBus.$emit('notificationTemporarilyRemoved',this.pendingDeletedNotifications)
    },

    /**
     * Sends api request to delete the notification
     * removes listener that prevents user from navigating away
     * emits to event bus to tell all components to update notifications
     */
    async deleteNotification(id) {
      removeEventListener('beforeunload', beforeUnloadListener, {capture: true});
      if (this.pendingDeletedNotifications.includes(id)) {
        await Api.deleteNotification(id)
        await this.updateNotifications().then( () => {
        this.pendingDeletedNotifications = this.pendingDeletedNotifications.filter(function(item) {
          return item !== id
        })})
        EventBus.$emit("notificationUpdate")
      }
    },

    /**
     * Performs an action based on the notification that has been clicked.
     * @param notification the notification that has been clicked
     */
    async notificationClicked(notification) {
      if (!notification.read) {
        notification.read = true;
        await Api.patchNotification(notification.id, {read: true});
        EventBus.$emit('notificationClicked', notification);
      }

    },

    /**
     * Updates the notifications to show either the archived or non-archived notifications.
     * This is toggle by a button.
     *
     */
    async toggleArchived() {
      this.isArchivedSelected = !this.isArchivedSelected;
      await this.updateNotifications();
      if (!this.isArchivedSelected) {
        await this.filterNotificationsByTag();
      }
    },
    /**
     * Hides the toast notification
     * Adds pendingDeletedNotification back into displayed list
     * removes listener that prevents user from navigating away
     */
    undoDelete(id){
      this.pendingDeletedNotifications = this.pendingDeletedNotifications.filter(function(item) {
        return item !== id
      })
      this.$bvToast.hide('undoToast'+id)
      EventBus.$emit('notificationTemporarilyRemoved',this.pendingDeletedNotifications)
      removeEventListener('beforeunload', beforeUnloadListener, {capture: true});
    },

    /**
     * Creates confirmation window to make sure user wants to leave
     */
    confirmLeave(){

      if (this.pendingDeletedNotifications.length>0) {
        return window.confirm('Are you sure you would like to change the page? You will not be able to undo the pending notification deletion')
      }
      return true
    },

    /**
     * Countdown timer that deletes the notification after 10 seconds
     */
    toastCountdown(id){
      if(this.countdowns[id].count > 0&&this.pendingDeletedNotifications.includes(id)) {
        setTimeout(() => {
          this.countdowns[id].count-=1
          this.toastCountdown(id)
        }, 1000)
    }
      else {
        this.$bvToast.hide('undoToast'+id)
      }
    }
  },

  computed: {
    /**
     * Count the number of currently selected tags
     */
    numberOfSelectedTags: function () {
      return Object.entries(this.tagColors).filter(([, value]) => value).length;
    },
    /**
     * Filters notifications by removing notifications that shouldn't be displayed.
     * This includes notifications pending deletion, and business notifications (if acting as user) or
     * user notifications (if acting as business)
     * @returns All notifications that should be displayed
     */
    filteredNotifications: function(){
      return this.notifications.filter((notification) =>
        !this.pendingDeletedNotifications.includes(notification.id) && (this.$currentUser.currentlyActingAs && notification.type==='Business Currency Changed'
          || !this.$currentUser.currentlyActingAs && notification.type!=='Business Currency Changed')
      );
    },
    notificationWidth() {
      return this.$currentUser.currentlyActingAs ? 12 : 6;
    },
    /**
     * return true if any tag selected
     */
    anyTagSelected() {
      return Object.entries(this.tagColors).some(([, value]) => value);
    }
  },

  created() {
    this.updateNotifications();
  },

  beforeRouteLeave (to, from, next) {
    if (this.confirmLeave()) {
      this.pendingDeletedNotifications.forEach(notif =>
      this.deleteNotification(notif)
      )
      next()
    } else {
      next(false)
    }
  }
}
</script>

