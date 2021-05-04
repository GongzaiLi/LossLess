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
        <b-nav-item v-if="!$currentUser.currentlyActingAs" to="/homepage">Home Page</b-nav-item>
        <b-nav-item v-if="$currentUser.currentlyActingAs" :to="businessRouteLink">Product Catalogue</b-nav-item>
        <b-nav-item v-on:click="goToProfile">My Profile</b-nav-item>
        <b-nav-item to="/users/search">User Search</b-nav-item>
        <b-nav-item to="/businesses/">Create Business</b-nav-item>
      </b-navbar-nav>
      <b-navbar-nav class="ml-auto">
        <b-nav-item-dropdown right>
          <template #button-content>
            <b-badge v-if="isActingAsUser">{{getUserBadgeRole()}}</b-badge>
            <em class="ml-2" id="profile-name">{{profileName}}</em>
            <img src="../../../public/profile-default.jpg" width="30" class="rounded-circle" style="margin-left: 5px; position: relative">
          </template>


          <div v-if="!isActingAsUser">
            <hr style="margin-top: 0.5em; margin-bottom: 0;">
            <sub style="padding-left:2em;">User Accounts</sub>
            <b-dropdown-item
                style="margin-top: 0.1em"
                @click="actAsUser()"
                class="user-name-drop-down">
              {{$currentUser.firstName}}
            </b-dropdown-item>
          </div>

          <div v-if="businessesInDropDown.length > 0" style="margin-bottom: 0.1em">
            <hr style="margin-top: 0.5em; margin-bottom: 0;">
            <sub style="margin-left:2em">Business Accounts</sub>
          </div>

          <b-dropdown-item
              v-for="business in businessesInDropDown"
              v-bind:key="business.id"
              @click="actAsBusiness(business)"
              class="business-name-drop-down">
            {{business.name}}
          </b-dropdown-item>

          <hr v-if="businessesInDropDown.length > 0" style="margin-top: 0.5em; margin-bottom: 0.5em;">

          <b-dropdown-item @click="logOut">Log Out</b-dropdown-item>
        </b-nav-item-dropdown>
      </b-navbar-nav>

    </b-collapse>
  </b-navbar>
</template>

<style scoped>
  .nav-item {
    font-size: 1.2em;
  }
</style>

<script>
import {setCurrentlyActingAs} from '../../auth'
import Api from '../../Api'
/**
 * A navbar for the site that contains a brand link and navs to user profile and logout.
 * Will not be shown if is current in the login or register routes. This is done by checking
 * that the names (not paths!) of the current route are not 'login' or 'register'. Names of routes
 * are set in router/index.js
 */
export default {
  name: "Navbar.vue",
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
    businessRouteLink: function() {
      return "/businesses/"+this.$currentUser.currentlyActingAs.id+"/products";
    }
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
     * User friendly display string for the user role to be displayed as a badge.
     * Converts the user role string given by the api (eg. 'globalApplicationAdmin') to
     * a more user-friendly string to be displayed (eg. 'Site Admin').
     * Returns empty string if they are a normal user, as they have no role worth displaying
     */
    getUserBadgeRole: function () {
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
     * Sets the user to act as the given business. Also sets the API
     * module to send all future requests acting as this business
     * @param business Object representing the business the user will act as
     */
    actAsBusiness(business) {
      setCurrentlyActingAs(business);
      Api.setBusinessActingAs(business.id);
    },
    /**
     * Sets the user to act as themselves again. Also sets the API
     * module to revert to sending all requests as the normal user
     */
    actAsUser() {
      setCurrentlyActingAs(null);
      Api.setBusinessActingAs(null);
    }
  },
}
</script>
