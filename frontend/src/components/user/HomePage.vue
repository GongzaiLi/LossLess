<template>
  <div>
  <b-card class="shadow">
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
    <b-card class="shadow mt-3 w-100">
      <div class="mb-2">
        <b-row>
          <b-col lg="1" cols="1">
            <h3><b-icon-bell/></h3>
          </b-col>
          <b-col lg="5" cols="11">
            <h3>Notifications</h3>
          </b-col>
          <b-col lg="4" cols="6">
            <b-dropdown :variant="numberOfSelectedTags === 0 ? 'secondary' : 'primary'" v-if="!isArchivedSelected" :text="numberOfSelectedTags === 0 ? 'Filter By Tag' : `${numberOfSelectedTags} Tags Selected`" class="tag-filter-dropdown">
              <b-dropdown-form v-for="[tagColor, selected] in Object.entries(tagColors)" :key="tagColor" @click="toggleTagColorSelected(tagColor)" :class="[selected ? 'selected' : '']">
                <NotificationTag :tag-color=tagColor class="tag" :tag-style-prop="{height: '1.5rem', width: '100%'}"></NotificationTag>
              </b-dropdown-form>
              <b-dropdown-item v-if="anyTagSelected" @click="removeAllTagsFromFilter()">
                <P><b-icon-x-circle-fill/> Remove Filters </p>
              </b-dropdown-item>
            </b-dropdown>
            <h3 v-else>(Archived)</h3>
          </b-col>
          <b-col lg="2" cols="6">
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
        <div v-for="notification in filteredNotifications" v-bind:key="notification.id" class="notification-cards shadow">
          <notification @tagColorChanged="filterNotificationsByTag" :archived-selected="isArchivedSelected" :notification="notification"
                        :in-navbar="false" @deleteNotification="createDeleteToast">
          </notification>
        </div>
      </b-overlay>
    </b-card>
    </b-row>
    <div class="undoToastClass" >
    <b-toast v-if="pendingDeletedNotificationId && this.$route.name==='home-page'"
             id="undoToast" variant="danger" toaster="b-toaster-bottom-right" toast-class="undoToastClass"
             @hide="deleteNotification(pendingDeletedNotificationId)"
             no-auto-hide
             visible
    >
      <template #toast-title>
        Deleted notification <b-button size="sm" @click="undoDelete()" class="ml-2">Undo({{countdown.count}})</b-button>
      </template>
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

</style>

<style>
.b-toaster-bottom-right .b-toaster-slot{
  float: right;
  max-width: 16rem !important;
}

#undoToast .toast-body {
  display: none;
}
</style>

<script>
import Api from "../../Api";
import NotificationTag from "../model/NotificationTag";
import Notification from "../model/Notification";
import EventBus from "../../util/event-bus";

const beforeUnloadListener = (event) => {
  event.preventDefault();
  return event.returnValue = 'Are you sure you want to exit?';
};


export default {
  components: {Notification, NotificationTag},
  name: "HomePage",
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
      pendingDeletedNotificationId: null,
      countdown: {
        interval: null,
        timeout: null,
        count: 10
      }
    }
  },

  mounted() {
    const userId = this.$currentUser.id;
    this.getUserInfo(userId);
    EventBus.$on('notificationUpdate', this.updateNotifications);
    EventBus.$on('changedCurrentUser', this.deleteNotification);
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
            this.userData = response.data;
          })
          .catch((error) => {
            this.$log.debug(error);
          })
    },
    /**
     * Updates the notifications using the notification api requests
     */
    async updateNotifications() {
      this.loadingNotifications = true;
      this.notifications = (await Api.getNotifications(null, this.isArchivedSelected)).data;
      this.loadingNotifications = false;
    },
    /**
     * Creates the toast notification to allow user to undo delete within 10 seconds
     * adds notification id to pendingDeletedNotifications so this notification is removed from the list of displayed notifications
     * Adds listener to prevent user from accidentally leaving the page before deletion is confirmed
     * @param id Id of the notification to delete in 10 seconds
     */
    async createDeleteToast(id) {
      if (this.pendingDeletedNotificationId) {
        await this.deleteNotification(this.pendingDeletedNotificationId);
      }

      addEventListener('beforeunload', beforeUnloadListener, {capture: true});
      this.countdown.count = 10;
      this.pendingDeletedNotificationId = id;
      this.countdown.interval = setInterval(() => {
        this.countdown.count -= 1;
      }, 1000);
      this.countdown.timeout = setTimeout(() => {
        this.deleteNotification(id);
        clearInterval(this.countdown.interval);
      }, 10000);
    },

    /**
     * Sends api request to delete the notification
     * removes listener that prevents user from navigating away
     * emits to event bus to tell all components to update notifications
     */
    async deleteNotification() {
      removeEventListener('beforeunload', beforeUnloadListener, {capture: true});
      if (this.pendingDeletedNotificationId) {
        await Api.deleteNotification(this.pendingDeletedNotificationId);
        await this.updateNotifications();
        this.pendingDeletedNotificationId = null;
        clearInterval(this.countdown.interval);
        clearTimeout(this.countdown.timeout);
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
    undoDelete() {
      this.pendingDeletedNotificationId = null;
      clearInterval(this.countdown.interval);
      clearInterval(this.countdown.timeout);
      removeEventListener('beforeunload', beforeUnloadListener, {capture: true});
    },
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
    filteredNotifications: function() {
      return this.notifications.filter((notification) =>
        this.pendingDeletedNotificationId !== notification.id
          && (this.$currentUser.currentlyActingAs && notification.type === 'Business Currency Changed' && parseInt(notification.subjectId) === this.$currentUser.currentlyActingAs.id
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
    window.addEventListener("unload", () => this.deleteNotification());  // Delete pending notifications when leaving the page
  },
}
</script>

