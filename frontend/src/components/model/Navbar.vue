<!--
navbar at the top of screen once logged in to navigate the web app
Date: sprint_1
-->
<template>
  <b-navbar
    toggleable="lg" type="dark" fixed="top"
    class="shadow"
  >
    <b-navbar-brand to="/homePage" @mouseenter="hoverLogo" @mouseleave="hoverLogoLeave"> <img src="icon.png" style="width: 2.5em" alt="LossLess Logo"></b-navbar-brand>

    <b-toast id="my-toast" variant="warning" solid toaster="b-toaster-top-left">
      <template #toast-title>
        Need Help?
      </template>
      This is top <em>left</em> corner of our web app. This is not a menu.
      <br>
      If you want to log out, or change who you are acting as,
      use the menu on the <strong>top <em>right</em></strong> corner <b-icon-arrow-right/>
    </b-toast>

    <b-navbar-toggle target="nav-collapse" class="mr-auto"></b-navbar-toggle>

    <b-collapse id="nav-collapse" is-nav>
      <b-navbar-nav>
        <b-nav-item to="/homepage">Home Page <NotificationBadge/></b-nav-item>
        <b-nav-item id="go-to-profile" v-on:click="goToProfile">My Profile</b-nav-item>
        <b-nav-item to="/search">Search Accounts</b-nav-item>
        <b-nav-item to="/listingSearch">Search Listings</b-nav-item>
        <b-nav-item v-if="!$currentUser.currentlyActingAs" to="/marketPlace"> Marketplace </b-nav-item>

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
          <b-dropdown-item :to="businessSalesReportRouteLink">
            <b-icon-graph-up/> Sales Report
          </b-dropdown-item>
        </b-nav-item-dropdown>
      </b-navbar-nav>

      <b-button v-if="!$currentUser.currentlyActingAs" to="/businesses/" class="ml-auto" id="create-business-btn">Create Business</b-button>
    </b-collapse>

    <b-dropdown right variant="link" toggle-class="text-decoration-none">
      <template #button-content>
        <b-badge v-if="isActingAsUser">{{ userBadgeRole }}</b-badge>
        <em class="ml-2" id="profile-name" style="color:white;">{{profileName}}</em>
        <b-img :src="showProfilePicture ? getURL($currentUser.profileImage.fileName) : require('../../../public/profile-default.jpg')"
               alt="User Profile Image" class="rounded-circle" style="margin-left: 5px; position: relative; height: 2rem; width:2rem"></b-img>
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
        <sub style="margin-left:2em;">Business Accounts</sub>

        <b-dropdown-item
            v-for="business in businessesInDropDown"
            v-bind:key="business.id"
            @click="actAsBusiness(business)"
            class="business-name-drop-down">
          {{business.name}}
        </b-dropdown-item>

        <hr style="margin-top: 0.5em; margin-bottom: 0.5em;">
      </div>

      <b-dropdown-item @click="logOut" style="min-width: 10rem">Log Out</b-dropdown-item>
    </b-dropdown>
  </b-navbar>
</template>

<style scoped>
#create-business-btn {
  background-color: #1c42b3;
  border-color: #1c42b3;
}
#create-business-btn:hover {
  background-color: #16348f;
  border-color: #16348f;
}
#create-business-btn:focus {
  background-color: #16348f;
  border-color: #16348f;
  box-shadow:0 0 0 .2rem rgba(28, 66, 179,.5);
}
#create-business-btn:active {
  background-color: #16348f;
  border-color: #16348f;
  box-shadow:0 0 0 .2rem rgba(28, 66, 179,.5);
}
</style>

<script>
import {initializeAuth, setCurrentlyActingAs} from '../../auth'
import EventBus from "../../util/event-bus";
import api from "../../Api";
import NotificationBadge from "./NotificationBadge";


/**
 * A navbar for the site that contains a brand link and navs to user profile and logout.
 * Will not be shown if is current in the login or register routes. This is done by checking
 * that the names (not paths!) of the current route are not 'login' or 'register'. Names of routes
 * are set in router/index.js
 */
export default {
  name: "Navbar.vue",
  components: {NotificationBadge},
  data() {
    return {
      cards: [],
      timer: null,
    }
  },
  mounted() {
    EventBus.$on('updatedUserDetails', this.updatedAccountHandler)
    EventBus.$on('updatedUserImage', this.updatedAccountHandler)
    EventBus.$on('updatedBusinessDetails', this.updatedAccountHandler)
    EventBus.$on('updatedBusinessImage', this.updatedAccountHandler)

  },
  computed: {
    isActingAsUser: function() {
      return this.$currentUser.currentlyActingAs == null;
    },

    /**
     * Returns true if we should show the user's profile image. i.e. they are acting as a user and have a profile image
     */
    showProfilePicture: function() {
      return this.$currentUser.profileImage&&this.isActingAsUser
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
     * Returns a string constructed to go to the sales listings page
     */
    businessListingsRouteLink: function() {
      return "/businesses/"+this.$currentUser.currentlyActingAs.id+"/listings"
    },

    /**
     * Returns a string constructed to go to the sales report page
     */
    businessSalesReportRouteLink: function() {
      return "/businesses/"+this.$currentUser.currentlyActingAs.id+"/salesReport"
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

    hoverLogo() {
      this.timer = setTimeout(() => {this.$bvToast.show('my-toast')}, 10000);
    },

    hoverLogoLeave() {
      if (this.timer) {
        clearTimeout(this.timer);
      }
    },

    /**
     * This is the handler for the events "updatedUserDetails", "updatedBusinessDetails", "updatedUserImage", "updatedBusinessImage".
     * The function calls initializeAuth from the Auth plugin
     * which refreshes Auth
     */
    updatedAccountHandler: function () {
      initializeAuth()
    },
    /**
     * Returns the URL required to get the image given the filename
     */
    getURL(imageFileName) {
      return api.getImage(imageFileName);
    },
  },
}
</script>
