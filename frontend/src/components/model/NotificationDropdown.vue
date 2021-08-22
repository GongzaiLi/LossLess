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

    <b-dropdown-item v-for="notification in notifications" v-bind:key="notification.id" class="notifications-item" @click="notificationClicked(notification)">
      <notification :notification="notification"> </notification>
    </b-dropdown-item>
  </b-dropdown>
</template>

<script>
import api from "../../Api";
import EventBus from "../../util/event-bus";
import Notification from "./Notification";


export default {
  name: "NotificationDropdown",
  components: {Notification},
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
  max-width: 80vw;
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

.notifications-tray .dropdown-toggle {
  padding: 0;
}

.iconBell {
  font-size: 1.6rem !important;
}

@media (max-width: 992px) and (min-width: 357px) {
  .notifications-tray .dropdown-menu {
      right: -7rem;
      left: auto;
  }
}
</style>
