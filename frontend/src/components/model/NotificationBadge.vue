<template>
    <b-badge variant="danger" v-if="numberOfNotifications">
            {{numberOfNotifications}}
    </b-badge>
</template>

<script>
import Api from "../../Api";
import EventBus from "../../util/event-bus";

export default {
  name: "NotificationBadge",
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
        if (!notification.read && !this.pendingDeletedNotification.includes(notification.id)
            && ((this.$currentUser.currentlyActingAs && notification.type === 'Business Currency Changed' && notification.subjectId === this.$currentUser.currentlyActingAs.id)
                || (!this.$currentUser.currentlyActingAs && notification.type!=='Business Currency Changed'))) {
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
