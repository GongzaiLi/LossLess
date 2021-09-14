<template>
  <b-dropdown right class="notifications-tray" v-if="isActingAsUser" no-caret variant="link" toggle-class="text-decoration-none">
    <template #button-content>
      <div class="icon mr-1">
        <b-icon v-if="numberOfNotifications" icon="bell" class="iconBell" variant="danger"></b-icon>
        <b-icon v-else icon="bell" class="iconBell" variant="light"></b-icon>
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

    <b-dropdown-item v-for="notification in unreadNotifications" v-bind:key="notification.id" class="notifications-item" @click="goToHomePage">
      <notification :key="notificationChange" :notification="notification" :in-navbar="true"> </notification>
    </b-dropdown-item>
  </b-dropdown>
</template>

<script>
import Api from "../../Api";
import EventBus from "../../util/event-bus";
import Notification from "./Notification";

export default {
  name: "NotificationDropdown",
  components: {Notification},
  data() {
    return {
      notifications: [],
      expiringCards: [],
      notificationChange: true,
      pendingDeletedNotification:[]
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
      return this.expiringCards.length + this.unreadNotifications.length;
    },

    /**
     * Checks for unread notifications.
     * @return only the unread notifications from the list of all notifications.
     */
    unreadNotifications: function () {
      let unreadNotifications = []
      for (const notification of this.notifications) {
        if (!notification.read&&!this.pendingDeletedNotification.includes(notification.id)) {
          unreadNotifications.push(notification)
        }
      }
      return unreadNotifications;
    }
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
      this.expiringCards = (await Api.getExpiredCards(this.$currentUser.id)).data;
      this.notifications = (await Api.getNotifications()).data;
      // This is needed to re-render a notification when a star icon is needed.
      this.notificationChange = !this.notificationChange;
     },
  },

  created() {
    this.updateNotifications();
  },

  mounted() {
    /**
     * This mount listens to the notificationUpdate event
     */
    EventBus.$on('notificationUpdate', this.updateNotifications)

    /**
     * This mount listens to the notificationUpdate event on click from home feed.
     */
    EventBus.$on('notificationClicked', this.updateNotifications)

    /**
    * This mount listens to the notificationTemporarilyRemoved event.
    * when notificationTemporarilyRemoved is called it sets pendingDeletedNotification to the received value to remove
    * it from the list of notifications
     */
    EventBus.$on('notificationTemporarilyRemoved', (pendingDeletedNotification) => {
      this.pendingDeletedNotification = pendingDeletedNotification;
    })
  }
}

</script>

<style>
.notifications-item {
  border-top: 1px solid #aaa;
  width: 26rem;
  max-width: 80vw;
  padding-top: 2px;
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
  overflow-x: hidden;
}

.notifications-tray .dropdown-toggle {
  padding: 0;
}

.iconBell {
  font-size: 1.6rem !important;
}

.notifications-item a:hover {
  cursor: default;
}

@media (max-width: 992px) and (min-width: 357px) {
  .notifications-tray .dropdown-menu {
      right: -7rem;
      left: auto;
  }
}
</style>
