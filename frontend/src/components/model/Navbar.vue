<!--
navbar at the top of screen once logged in to navigate the web app
Date: sprint_1
-->
<template>
  <b-navbar v-if="!['login', 'register'].includes($route.name)"
    toggleable="lg" type="dark" variant="dark" fixed="top"
    class="shadow"
  >
    <b-navbar-brand href="#">Wasteless</b-navbar-brand>

    <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>

    <b-collapse id="nav-collapse" is-nav>
      <b-navbar-nav>
        <b-nav-item to="/homepage">Home Page</b-nav-item>
        <b-nav-item id="go-to-profile" v-on:click="goToProfile">My Profile</b-nav-item>
        <b-nav-item to="/users/search">User Search</b-nav-item>
        <b-nav-item to="/marketPlace"> Marketplace </b-nav-item>
        <b-nav-item v-if="!$currentUser.currentlyActingAs" to="/businesses/">Create Business</b-nav-item>


        <b-nav-item-dropdown
            v-if="$currentUser.currentlyActingAs"
            id="business-link-dropdown"
            text="Product Management"
            toggle-class="nav-link-custom"
        >
          <b-dropdown-item :to="businessProductRouteLink">
            <b-icon-newspaper/> Product Catalogue
          </b-dropdown-item>
          <b-dropdown-item :to="businessInventoryRouteLink">
            <b-icon-box-seam/> Inventory
          </b-dropdown-item>
          <b-dropdown-item :to="businessListingsRouteLink">
            <b-icon-receipt/> Sales List
          </b-dropdown-item>
        </b-nav-item-dropdown>
      </b-navbar-nav>

      <b-navbar-nav class="ml-auto">
        <div class="icon" id="bell" @click="bellIconPressed">
          <img src="https://i.imgur.com/AC7dgLA.png" alt=""><span v-if="notifications.length">{{notifications.length}}</span>
        </div>

        <div class="notifications" id="box">
          <h2>Notifications - <span> {{notifications.length}}</span></h2>
          <div v-for="notification in notifications" class="notifications-item" v-bind:key="notification.id">
            <div class="text">
              <h4> Marketplace Card: {{notification.title}}</h4>
              <h4> expires within 24 hours</h4>
            </div>
          </div>
        </div>
      </b-navbar-nav>


      <b-navbar-nav class="dropdown-menu-end">
        <b-nav-item-dropdown right>
          <template #button-content>
            <b-badge v-if="isActingAsUser">{{ userBadgeRole }}</b-badge>
            <em class="ml-2" id="profile-name">{{profileName}}</em>
            <img src="../../../public/profile-default.jpg" alt="User Profile Image" width="30" class="rounded-circle" style="margin-left: 5px; position: relative">
          </template>


          <div v-if="!isActingAsUser">
            <hr style="margin-top: 0; margin-bottom: 0;">
            <sub style="padding-left:2em;">User Accounts</sub>
            <b-dropdown-item
                style="margin-top: 0.1em"
                @click="actAsUser()"
                class="user-name-drop-down">
              {{$currentUser.firstName}}
            </b-dropdown-item>
            <hr style="margin-top: 0.5em; margin-bottom: 0;">
          </div>

          <div v-if="businessesInDropDown.length > 0" style="margin-bottom: 0.1em">
            <hr v-if="isActingAsUser" style="margin-top: 0; margin-bottom: 0;" >
            <sub style="margin-left:2em">Business Accounts</sub>

            <b-dropdown-item
                v-for="business in businessesInDropDown"
                v-bind:key="business.id"
                @click="actAsBusiness(business)"
                class="business-name-drop-down">
              {{business.name}}
            </b-dropdown-item>

            <hr style="margin-top: 0.5em; margin-bottom: 0.5em;">
          </div>

          <b-dropdown-item @click="logOut">Log Out</b-dropdown-item>
        </b-nav-item-dropdown>
      </b-navbar-nav>

    </b-collapse>
  </b-navbar>
</template>

<style>

.icon img {
  cursor: pointer;
  display: inline;
  width: 26px;
  margin-top: 4px;
}

.icon span {
  color: #f00
}

.icon:hover {
  opacity: .7
}

.notifications {
  width: 300px;
  height: 0;
  opacity: 0;
  position: absolute;
  top: 63px;
  right: 62px;
  border-radius: 5px 0 5px 5px;
  background-color: #fff;
  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19)
}

.notifications h2 {
  font-size: 14px;
  padding: 10px;
  border-bottom: 1px solid #eee;
  color: #999
}

.notifications h2 span {
  color: #f00
}

.notifications-item {
  display: flex;
  border-bottom: 1px solid #eee;
  padding: 6px 9px;
  margin-bottom: 0;
  cursor: pointer
}

.notifications-item:hover {
  background-color: #eee
}

.notifications-item .text h4 {
  color: #777;
  font-size: 16px;
  margin-top: 3px
}

.notifications-item .text p {
  color: #aaa;
  font-size: 12px
}

</style>

<script>
import {setCurrentlyActingAs} from '../../auth'
import api from "../../Api"
/**
 * A navbar for the site that contains a brand link and navs to user profile and logout.
 * Will not be shown if is current in the login or register routes. This is done by checking
 * that the names (not paths!) of the current route are not 'login' or 'register'. Names of routes
 * are set in router/index.js
 */
export default {
  name: "Navbar.vue",
  data() {
    return {
      showNotifications: false,
      cards: [],
      notifications: []
    }
  },
  mounted() {
     this.getCards(this.$currentUser.id);
  },
  computed: {
    isActingAsUser: function() {
      return this.$currentUser.currentlyActingAs == null;
    },
    /**
     * The name to be displayed in the profile area. It is either the first name of the user,
     * or the name of whichever business the user is acting as.
     */
    profileName: function() {
      if (this.isActingAsUser) {
        return this.$currentUser.firstName;
      } else {
        return this.$currentUser.currentlyActingAs.name;
      }
    },
    /**
     * The businesses to show in the dropdown for the profile name. Contains
     * all businesses the user can act as, except for the business the user is acting as currently
     */
    businessesInDropDown: function() {
      return this.$currentUser.businessesAdministered.filter(
          (business) => (this.isActingAsUser || business.id !== this.$currentUser.currentlyActingAs.id)
      );
    },

    /**
     * Returns a string constructed to go to the product page url
     */
    businessProductRouteLink: function() {
      return "/businesses/"+this.$currentUser.currentlyActingAs.id+"/products";
    },

    /**
     * Returns a string constructed to go to the inventory page
     */
    businessInventoryRouteLink: function() {
      return "/businesses/"+this.$currentUser.currentlyActingAs.id+"/inventory"
    },
    /**
     * Returns a string constructed to go to the sales page
     */
    businessListingsRouteLink: function() {
      return "/businesses/"+this.$currentUser.currentlyActingAs.id+"/listings"
    },
    /**
     * User friendly display string for the user role to be displayed as a badge.
     * Converts the user role string given by the api (eg. 'globalApplicationAdmin') to
     * a more user-friendly string to be displayed (eg. 'Site Admin').
     * Returns empty string if they are a normal user, as they have no role worth displaying
     */
    userBadgeRole: function () {
      switch (this.$currentUser.role) {
        case 'globalApplicationAdmin':
          return "Site Admin";
        case 'defaultGlobalApplicationAdmin':
          return "Default Site Admin";
        default:
          return "";
      }
    },
  },
  methods: {
    /**
     * Gets all the expiring cards from for the current user.
     */
    getCards(userId) {
      api.getExpiringCards(userId)
          .then((res) => {
            this.cards = res.data;
          })
          .catch((error) => {
            this.$log.debug(error);
          });
    },

    /**
     *  Triggers when the notification icon is pressed to display the notifications.
     */
    bellIconPressed() {
      if(this.showNotifications) {
        document.getElementById("box").style.height='0px';
        document.getElementById("box").style.opacity='0';
        this.showNotifications = false;
      } else {
        document.getElementById("box").style.height='auto';
        document.getElementById("box").style.opacity='1';
        this.showNotifications = true;
      }

    },
    /**
     * Redirects to the profile of the account the user is acting as.
     * This will be the either the use profile if they are not acting as anyone,
     * of the profile of the business they are acting as. Doesn't redirect if the user
     * is already on the profile since that throws a Vue Router error.
     */
    goToProfile: function () {
      let pathToGoTo;
      if (this.$currentUser.currentlyActingAs) {
        pathToGoTo = `/businesses/${this.$currentUser.currentlyActingAs.id}`;
      } else {
        pathToGoTo = `/users/${this.$currentUser.id}`;
      }
      if (this.$route.fullPath !== pathToGoTo) {
        this.$router.push({path: pathToGoTo});
      }
    },
    /**
     * Logs out the current user and redirects to the login page.
     * Currently does nothing with managing cookies, this needs to be implemented later.
     */
    logOut() {
      this.$currentUser = null;
      this.$router.push('/login');
    },
    /**
     * Sets the user to act as the given business. Also sets the API
     * module to send all future requests acting as this business
     * @param business Object representing the business the user will act as
     */
    actAsBusiness(business) {
      setCurrentlyActingAs(business);
      this.$router.push(`/businesses/${business.id}`);
    },
    /**
     * Sets the user to act as themselves again. Also sets the API
     * module to revert to sending all requests as the normal user
     */
    actAsUser() {
      setCurrentlyActingAs(null);
      this.$router.push(`/users/${this.$currentUser.id}`);
    },

    /**
     *  Adds a notification about a card that expires within next 24 hours.
     *  This is done by adding the expiring card to the list of notifications.
     */
    updateNotifications() {
      if (this.notifications.length < this.cards.length) {
        for (const card of this.cards) {
          if (!this.notifications.includes(card)) {
            const date = card.displayPeriodEnd.split("T")[0]
            const time = card.displayPeriodEnd.split("T")[1]
            const cardExpires = new Date(parseInt(date.split("-")[0]), parseInt(date.split("-")[1]) - 1, parseInt(date.split("-")[2]), parseInt(time.split(":")[0]), parseInt(time.split(":")[1]), parseFloat(time.split(":")[2]))
            const currentDate = new Date();
            const currentAfterADay = currentDate.setDate(currentDate.getDate() + 1);

            if (cardExpires < currentAfterADay) {
              this.notifications.push(card)
              console.log("Added")
            } else {
              console.log("none Added")

            }
          }

        }
      }
    },
  },

  created() {
    this.interval = setInterval(() => this.updateNotifications(), 1000);
  },
}
</script>
