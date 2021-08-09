<!--
navbar at the top of screen once logged in to navigate the web app
Date: sprint_1
-->
<template>
  <b-navbar
    toggleable="lg" type="dark" fixed="top"
    class="shadow"
  >
    <b-navbar-brand href="#" @mouseenter="hoverLogo" @mouseleave="hoverLogoLeave">Wasteless</b-navbar-brand>

    <b-navbar-brand>
    <b-nav-form>
      <b-input-group >
        <b-form-input   placeholder="Search Listings" v-model="searchQuery" @keyup.enter="search(searchQuery)"></b-form-input>
        <b-input-group-append>
          <b-button v-on:click="search(searchQuery)" type="submit"> <b-icon-search/> </b-button>
        </b-input-group-append>
      </b-input-group>
    </b-nav-form>
    </b-navbar-brand>

    <b-toast id="my-toast" variant="warning" solid toaster="b-toaster-top-left">
      <template #toast-title>
        Need Help?
      </template>
      This is top <em>left</em> corner of our web app. This is not a menu.
      <br>
      If you want to log out, or change who you are acting as,
      use the menu on the <strong>top <em>right</em></strong> corner <b-icon-arrow-right/>
    </b-toast>

    <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>


    <b-collapse id="nav-collapse" is-nav>
      <b-navbar-nav>
        <b-nav-item to="/homepage">Home Page</b-nav-item>
        <b-nav-item id="go-to-profile" v-on:click="goToProfile">My Profile</b-nav-item>
        <b-nav-item to="/search">Search</b-nav-item>
        <b-nav-item v-if="!$currentUser.currentlyActingAs" to="/marketPlace"> Marketplace </b-nav-item>
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
        <b-nav-item-dropdown right>
          <template #button-content>
            <div class="icon mr-1" @click="bellIconPressed">
              <b-icon v-if="numExpiredCards > 0" icon="bell" class="iconBell" variant="danger" style="font-size:  1.8rem;"></b-icon>
              <b-icon v-else-if="numberOfNotifications"  icon="bell" class="iconBell" variant="warning" style="font-size:  1.8rem"></b-icon>
              <b-icon v-else icon="bell" class="iconBell" variant="light" style="font-size:  1.8rem"></b-icon>
              <span v-if="numberOfNotifications" :style="{color: ((numExpiredCards > 0) ? 'red' : 'orange')}" style="position: absolute; transform: translateY(5px)">
                {{numberOfNotifications}}
              </span>
            </div>
          </template>
          <b-dropdown-item disabled>
            <h4 style="color: black">Notifications: <span> {{numberOfNotifications}}</span></h4>
          </b-dropdown-item>

          <b-dropdown-item v-if="numExpiredCards"  @click="clicked = !clicked" class="expired-notifications-item">
            <b-row no-gutters>
              <b-col cols="11" style="white-space: initial">
                <h6> Marketplace Card Expired:</h6>
                <span>{{ numExpiredCards}} {{ expiredText }}</span>
              </b-col>
              <b-col cols="1">
                <b-icon style="width: 30px; height: 30px; margin-top: 50%"
                        icon="trash-fill" @click="clearExpiredCards"></b-icon>
              </b-col>
            </b-row>
          </b-dropdown-item>

          <b-dropdown-item class="notifications-item" v-for="notification in notifications" v-bind:key="notification.id"  @click="goToHomePage">
            <h6> Marketplace Card: {{notification.title}}</h6>
             expires within 24 hours
          </b-dropdown-item>
        </b-nav-item-dropdown>
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

.icon  {
  display: inline;
}

.icon:hover {
  opacity: .7
}

.notifications-item {
  border-top: 1px solid #eee;
}

.expired-notifications-item {
  border-top: 1px solid #eee;
  cursor: pointer;
}

.expired-notifications-item * {
  color: orangered;
}

.expired-notifications-item h6 {
  margin-top: 3px;
}

.notifications-item h6 {
  margin-top: 3px;
}

.expired-notifications-item p {
  color: orangered;
}

.expired-notifications-item .dropdown-item:active {
  color: initial;
  background-color: #cccccc;
}

.notifications-item .dropdown-item:active {
  color: initial;
  background-color: #cccccc;
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
      clicked: false,
      showNotifications: false,
      cards: [],
      notifications: [],
      numExpiredCards:0,
      timer: null,
      searchQuery: '',
    }
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

    /**
     * Checks if there are notifications about expiring or expired cards.
     * @return The number of total notifications
     */
    numberOfNotifications: function () {
      if (this.numExpiredCards){
        return this.notifications.length+1;
      }
      return this.notifications.length;
    },

    /**
     * Checks the number of expired cards
     * @return The appropriate notification message based on number of cards
     */
    expiredText: function () {
      if (this.numExpiredCards === 1){
        return " of your cards has expired and been deleted"
      }
      return " of your cards have expired and been deleted"
    }
  },
  methods: {

    /**
     * Routes to Listing search page with search string
     * Called when user clicks search or presses enter
     * @param searchQuery the string used to search for listings passed as a query parameter
     **/
    search(searchQuery) {
      this.$router.push(`/listingSearch?searchQuery=${searchQuery}`);
    },

    /**
     * Gets all the expiring cards from for the current user.
     */
    getExpiringCards(userId) {
      return api.getExpiringCards(userId)
          .then((res) => {
            this.cards = res.data;
          })
          .catch((error) => {
            this.$log.debug(error);
          });
    },

    /**
     * Gets all the expired cards for the current user.
     */
    getExpiredCards(userId) {
      return api.expiredCardsNumber(userId)
          .then((res) => {
            this.numExpiredCards = res.data;
          })
          .catch((error) => {
            this.$log.debug(error);
          });
    },
    /**
     * Gets all the expired cards from for the current user.
     */
    clearExpiredCards() {
      api.clearHasCardsExpired(this.$currentUser.id).then(() => {
        this.numExpiredCards=0
      }).catch((error) => {
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
     * Logs out the current user and redirects to the login page.
     * Currently does nothing with managing cookies, this needs to be implemented later.
     */
    goToHomePage() {
      if (this.$route.fullPath !== '/homepage') {
        this.$router.push('/homepage');
      }
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
    async updateNotifications() {
      await this.getExpiringCards(this.$currentUser.id);
      await this.getExpiredCards(this.$currentUser.id);
      this.notifications = this.cards;
    },
    hoverLogo() {
      this.timer = setTimeout(() => {this.$bvToast.show('my-toast')}, 10000);
    },
    hoverLogoLeave() {
      if (this.timer) {
        clearTimeout(this.timer);
      }
    }
  },

  created() {
    this.updateNotifications();
    this.interval = setInterval(() => this.updateNotifications(), 60000);
  },
}
</script>
